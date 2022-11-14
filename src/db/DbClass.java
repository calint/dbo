package db;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public final class DbClass {
	final Class<? extends DbObject> javaClass;
	final String tableName;
	final ArrayList<DbField> declaredFields = new ArrayList<DbField>();
	final ArrayList<DbRelation> declaredRelations = new ArrayList<DbRelation>();
	/** all fields, including inherited */
	final ArrayList<DbField> allFields = new ArrayList<DbField>();

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
				dbf.tableName = Db.tableNameForJavaClass(c);
				dbf.columnName = f.getName();
				declaredFields.add(dbf);
				continue;
			}
			if (DbRelation.class.isAssignableFrom(f.getType())) {
				final DbRelation dbr = (DbRelation) f.get(null);
				dbr.cls = c;
				dbr.tableName = Db.tableNameForJavaClass(c);
				dbr.name = f.getName();
				declaredRelations.add(dbr);
				continue;
			}
		}
	}

	void initAllFieldsList() {
		initAllFieldsRec(allFields, javaClass);
	}

	private static void initAllFieldsRec(final List<DbField> ls, Class<?> cls) {
		if (!cls.getSuperclass().equals(Object.class)) {
			initAllFieldsRec(ls, cls.getSuperclass());
		}
		final DbClass dbcls = Db.instance().dbClassForJavaClass(cls);
		ls.addAll(dbcls.declaredFields);
	}

	@Override
	public String toString() {
		return javaClass.getName() + " fields:" + declaredFields + " relations:" + declaredRelations;
	}

	final String sql_createTable(StringBuilder sb) {
		sb.append("create table ").append(tableName).append("(");
		for (final DbField f : allFields) {
			f.sql_createColumn(sb);
			sb.append(',');
		}
//		sql_createTableRec(sb, jcls);
		sb.setLength(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}

//	private static void sql_createTableRec(StringBuilder sb, Class<?> c) {
//		if (!c.getSuperclass().equals(Object.class))
//			sql_createTableRec(sb, c.getSuperclass());
//		final DbClass dbcls = Db.instance().dbClassForJavaClass(c);
//		for (final DbField f : dbcls.fields) {
//			f.sql_createField(sb);
//			sb.append(',');
//		}
//	}
}
