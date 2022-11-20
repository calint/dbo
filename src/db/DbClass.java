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

	DbClass(Class<? extends DbObject> c) throws Throwable {
		javaClass = c;
		tableName = Db.tableNameForJavaClass(c);
		// collect declared fields and relations
		for (final Field f : javaClass.getDeclaredFields()) {
			if (!Modifier.isStatic(f.getModifiers()))
				continue;
			if (DbField.class.isAssignableFrom(f.getType())) {
				final DbField dbf = (DbField) f.get(null);
//				dbf.cls = c;
				dbf.tableName = tableName;
				dbf.columnName = f.getName();
				declaredFields.add(dbf);
				continue;
			}
			if (DbRelation.class.isAssignableFrom(f.getType())) {
				final DbRelation dbr = (DbRelation) f.get(null);
				dbr.cls = c;
				dbr.tableName = tableName;
				dbr.name = f.getName();
				declaredRelations.add(dbr);
				continue;
			}
			if (Index.class.isAssignableFrom(f.getType())) {
				final Index ix = (Index) f.get(null);
				ix.cls = c;
				ix.name = f.getName();
				ix.tableName = tableName;
				declaredIndexes.add(ix);
				continue;
			}
		}
	}

	/** recurse initiates allFields, allRelations, allIndexes lists */
	void initAllFieldsAndRelationsLists() {
		initAllFieldsAndRelationsListsRec(allFields, allRelations, allIndexes, javaClass);
	}

	private static void initAllFieldsAndRelationsListsRec(final List<DbField> lsfld, final List<DbRelation> lsrel,
			final List<Index> lsix, final Class<?> cls) {
		final Class<?> scls = cls.getSuperclass();
		if (!scls.equals(Object.class)) {
			initAllFieldsAndRelationsListsRec(lsfld, lsrel, lsix, scls);
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
			f.sql_createColumn(sb);
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append(")");
		return;
	}

	@Override
	public String toString() {
		return javaClass.getName() + " fields:" + allFields + " relations:" + allRelations;
	}
}
