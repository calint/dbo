package db;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public final class DbClass {
	final Class<? extends DbObject> javaClass;
	final String tableName;
	final ArrayList<DbField> declaredFields = new ArrayList<DbField>();
	final ArrayList<DbRelation> declaredRelations = new ArrayList<DbRelation>();
	/** all fields, including inherited */
	final ArrayList<DbField> allFields = new ArrayList<DbField>();
	final ArrayList<DbRelation> allRelations = new ArrayList<DbRelation>();

	final ArrayList<Index> declaredIndexes = new ArrayList<Index>();
	final ArrayList<Index> allIndexes = new ArrayList<Index>();

	final ArrayList<RelRefN> referingRefN = new ArrayList<RelRefN>();

	boolean doCascadeDelete = false;

	DbClass(Class<? extends DbObject> c) throws Throwable {
		javaClass = c;
		tableName = Db.tableNameForJavaClass(c);
		// collect declared fields and relations
		for (final Field f : javaClass.getDeclaredFields()) {
			if (!Modifier.isStatic(f.getModifiers()))
				continue;
			if (DbField.class.isAssignableFrom(f.getType())) {
				final DbField dbf = (DbField) f.get(null);
				dbf.name = f.getName();
//				dbf.cls = c;
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

	final void sql_createTable(StringBuilder sb, DatabaseMetaData dbm) throws Throwable {
		final ResultSet rs = dbm.getTables(null, null, tableName, new String[] { "TABLE" });
		if (rs.next()) {
			rs.close();
			return;// ? check columns
		}
		rs.close();

		sb.append("create table ").append(tableName).append("(");
		for (final DbField f : allFields) {
			f.sql_columnDefinition(sb);
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append(")");
		return;
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
}
