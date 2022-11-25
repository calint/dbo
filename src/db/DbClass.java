package db;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public final class DbClass {
	/** The java class that this DbClass describes. */
	final Class<? extends DbObject> javaClass;
	/** The table name of this type. Set by Db at init. */
	final String tableName;
	final ArrayList<DbField> declaredFields = new ArrayList<DbField>();
	final ArrayList<DbRelation> declaredRelations = new ArrayList<DbRelation>();
	final ArrayList<Index> declaredIndexes = new ArrayList<Index>();

	/** All fields, relations, indexes, including inherited */
	final ArrayList<DbField> allFields = new ArrayList<DbField>();
	final ArrayList<DbRelation> allRelations = new ArrayList<DbRelation>();
	final ArrayList<Index> allIndexes = new ArrayList<Index>();

	/**
	 * Contains RefN relations declared in other classes referring to this class.
	 * Used to delete orphan entries in the RefN table when an object of this type
	 * is deleted.
	 */
	final ArrayList<RelRefN> referingRefN = new ArrayList<RelRefN>();

	/**
	 * True if this type needs to cascade deletes because it contains relations that
	 * need to cascade deletes. Set by Db at init.
	 */
	boolean doCascadeDelete = false;

	DbClass(Class<? extends DbObject> c) throws Throwable {
		javaClass = c;
		tableName = Db.tableNameForJavaClass(c);
		// collect declared fields, relations and indexes
		for (final Field f : javaClass.getDeclaredFields()) {
			if (!Modifier.isStatic(f.getModifiers()))
				continue;
			if (DbField.class.isAssignableFrom(f.getType())) {
				final DbField dbf = (DbField) f.get(null);
				dbf.name = f.getName();
				dbf.cls = c;
				dbf.tableName = tableName;
				declaredFields.add(dbf);
				continue;
			}
			if (DbRelation.class.isAssignableFrom(f.getType())) {
				final DbRelation dbr = (DbRelation) f.get(null);
				dbr.name = f.getName();
				dbr.cls = c;
				dbr.tableName = tableName;
				declaredRelations.add(dbr);
				if (dbr.cascadeDeleteNeeded())
					doCascadeDelete = true;
				continue;
			}
			if (Index.class.isAssignableFrom(f.getType())) {
				final Index ix = (Index) f.get(null);
				ix.name = f.getName();
				ix.cls = c;
				ix.tableName = tableName;
				declaredIndexes.add(ix);
				continue;
			}
		}
	}

	/** recurse initiates allFields, allRelations, allIndexes lists */
	void init() {
		init_rec(allFields, allRelations, allIndexes, javaClass);
	}

	private static void init_rec(final List<DbField> lsfld, final List<DbRelation> lsrel, final List<Index> lsix,
			final Class<?> cls) {
		final Class<?> scls = cls.getSuperclass();
		if (!scls.equals(Object.class)) {
			init_rec(lsfld, lsrel, lsix, scls);
		}
		final DbClass dbcls = Db.instance().dbClassForJavaClass(cls);
		lsfld.addAll(dbcls.declaredFields);
		lsrel.addAll(dbcls.declaredRelations);
		lsix.addAll(dbcls.declaredIndexes);
	}

	/** called by Db at init. check and create if necessary */
	void ensureTable(final Statement stmt, final DatabaseMetaData dbm) throws Throwable {
		final ResultSet rs = dbm.getTables(null, null, tableName, new String[] { "TABLE" });
		if (rs.next()) {
			rs.close();
			assertColumns(stmt, dbm);
			return;
		}
		rs.close();

		createTable(stmt);

		return;
	}

	private void createTable(final Statement stmt) throws SQLException {
		final StringBuilder sb = new StringBuilder(128);
		sb.append("create table ").append(tableName).append("(");
		for (final DbField f : allFields) {
			f.sql_columnDefinition(sb);
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append(")");

		final String sql = sb.toString();
		Db.log(sql);
		stmt.execute(sql);
	}

	private void assertColumns(final Statement stmt, final DatabaseMetaData dbm) throws Throwable {
		addMissingColumns(stmt, dbm);
		if (Db.instance().delete_unused_columns)
			deleteUnusedColumns(stmt, dbm);
		arrangeColumns(stmt, dbm);
		assertColumnTypes(stmt, dbm);
//		assertColumnDefaultValues(stmt, dbm);
	}

	private void deleteUnusedColumns(final Statement stmt, final DatabaseMetaData dbm) throws Throwable {
		final List<Column> columns = getColumnsFromDb(dbm);
		for (final DbField f : allFields) {
			columns_removeColumn(columns, f.name);
		}
		if (columns.isEmpty())
			return;

		for (final Column c : columns) {
			final StringBuilder sb = new StringBuilder(128);
			sb.append("alter table ").append(tableName).append(" drop column ").append(c.name);
			final String sql = sb.toString();
			Db.log(sql);
			stmt.execute(sql);
		}
	}

	private void assertColumnTypes(final Statement stmt, final DatabaseMetaData dbm) throws Throwable {
		final List<Column> columns = getColumnsFromDb(dbm);
		final int n = allFields.size();
		for (int i = 0; i < n; i++) {
			final DbField f = allFields.get(i);
			final Column c = columns.get(i);
			final String ct = c.type_name.toLowerCase();
			if (f.getSqlType().toLowerCase().equals(ct)) {
				// todo check size
//				if (ct.equals("varchar")) {
//					if(c.column_size==f.getSize())
//						continue;
//				}
				continue;
			}
			final StringBuilder sb = new StringBuilder(128);
			sb.append("alter table ").append(tableName).append(" modify ");
			f.sql_columnDefinition(sb);
			final String sql = sb.toString();
			Db.log(sql);
			stmt.execute(sql);
		}
	}

	private void arrangeColumns(final Statement stmt, final DatabaseMetaData dbm) throws Throwable {
		// ? better algorithm for fields moved "down" in list?
		while (!columnsAreInOrder(dbm)) {
			final List<Column> columns = getColumnsFromDb(dbm);
			DbField prevField = null;
			final int n = allFields.size();
			for (int i = 0; i < n; i++) {
				final DbField f = allFields.get(i);
				final Column c = columns.get(i);
				if (f.name.equals(c.name)) {
					prevField = f;
					continue;
				}
				// example:
				// flds: id, name, passhash, nlogins, birthTime, lng, flt, dbl, bool,
				// profilePic, groupPic
				// cols: id, name, passhash, nlogins, lng, flt, dbl, bool, birthTime,
				// profilePic, groupPic
				final StringBuilder sb = new StringBuilder(128);
				sb.append("alter table ").append(tableName).append(" modify ");
				f.sql_columnDefinition(sb);
				sb.append(' ');
				if (prevField == null) {
					sb.append("first");
				} else {
					sb.append("after ");
					sb.append(prevField.name);
				}
				final String sql = sb.toString();
				Db.log(sql);
				stmt.execute(sql);
				break;
			}
		}
	}

	private boolean columnsAreInOrder(final DatabaseMetaData dbm) throws Throwable {
		final ArrayList<Column> columns = getColumnsFromDb(dbm);
		final int n = allFields.size();
		for (int i = 0; i < n; i++) {
			final DbField f = allFields.get(i);
			final Column c = columns.get(i);
			if (f.name.equals(c.name))
				continue;
			return false;
		}
		return true;
	}

	private void addMissingColumns(final Statement stmt, final DatabaseMetaData dbm) throws Throwable {
		final ArrayList<Column> columns = getColumnsFromDb(dbm);
		DbField prevField = null;
		for (final DbField f : allFields) {
			if (columns_containsColumn(columns, f.name)) {
				prevField = f;
				continue;
			}

			addColumn(stmt, f, prevField);

			prevField = f;
		}
	}

	private void addColumn(final Statement stmt, final DbField fld, final DbField prevFld) throws Throwable {
		final StringBuilder sb = new StringBuilder(128);
		sb.append("alter table ").append(tableName).append(" add ");
		fld.sql_columnDefinition(sb);
		sb.append(' ');
		if (prevFld == null) {
			sb.append("first");
		} else {
			sb.append("after ");
			sb.append(prevFld.name);
		}
		final String sql = sb.toString();
		Db.log(sql);
		stmt.execute(sql);
	}

	private ArrayList<Column> getColumnsFromDb(final DatabaseMetaData dbm) throws SQLException {
		final ResultSet rs = dbm.getColumns(null, null, tableName, null);
		final ArrayList<Column> columns = new ArrayList<Column>();
		while (rs.next()) {
			final Column col = new Column();
			col.name = rs.getString("COLUMN_NAME");
			col.ordinal_position = rs.getInt("ORDINAL_POSITION");
			col.type_name = rs.getString("TYPE_NAME");
//			col.column_size = rs.getInt("COLUMN_SIZE");
//			col.column_def = rs.getString("COLUMN_DEF");
			columns.add(col);
		}
		rs.close();

		// sort columns in the way they appear in the result set
		Collections.sort(columns, new Comparator<Column>() {
			public int compare(final Column o1, final Column o2) {
				return o1.ordinal_position - o2.ordinal_position;
			};
		});

		return columns;
	}

	private boolean columns_containsColumn(final List<Column> columns, final String name) {
		for (final Column c : columns) {
			if (c.name.equals(name))
				return true;
		}
		return false;
	}

	private void columns_removeColumn(final List<Column> columns, final String name) {
		final int n = columns.size();
		for (int i = 0; i < n; i++) {
			final Column c = columns.get(i);
			if (c.name.equals(name)) {
				columns.remove(i);
				return;
			}
		}
		throw new RuntimeException("expected to find column " + name + " in " + columns);
	}

	private final static class Column {
		String name;
		int ordinal_position;
		String type_name;
//		int column_size;
//		String column_def;

		@Override
		public String toString() {
			return name;
		}
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(128);
		sb.append(javaClass.getName()).append(" fields:").append(allFields).append(" relations:").append(allRelations)
				.append(" indexes:").append(allIndexes);
		return sb.toString();
	}

	public List<DbField> getDeclaredFields() {
		return declaredFields;
	}

	public List<DbRelation> getDeclaredRelations() {
		return declaredRelations;
	}

	void dropUndeclaredIndexes(final Statement stmt, final DatabaseMetaData dbm) throws Throwable {
		final HashSet<String> indexes=new HashSet<String>();
		// collect all indexes in this table
		final ResultSet rs = dbm.getIndexInfo(null, null, tableName, false, false);
		while (rs.next()) {
			final String indexName = rs.getString("INDEX_NAME");
			if (indexName.equals("PRIMARY")) { // mysql added index on id
				continue;
			}
			indexes.add(indexName);
		}
		rs.close();

		for(final Index ix:allIndexes) {
			indexes.remove(ix.name);
		}
		
		if(indexes.isEmpty())
			return;
		
		for(final String s:indexes) {
			final StringBuilder sb=new StringBuilder(128);
			sb.append("drop index ").append(s).append(" on ").append(tableName);
			final String sql=sb.toString();
			Db.log(sql);
			stmt.execute(sql);
		}
	}
}
