package db;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;

public final class DbClass {
	final Class<? extends DbObject> jcls;
	final String tableName;
	final ArrayList<DbField> fields = new ArrayList<>();
	final ArrayList<DbRelation> relations = new ArrayList<>();

	DbClass(Class<? extends DbObject> c) throws Throwable {
		this.jcls = c;
		tableName = Db.tableNameForJavaClass(c);

		for (final Field f : c.getDeclaredFields()) {
			if (!Modifier.isStatic(f.getModifiers()))
				continue;
			if (DbField.class.isAssignableFrom(f.getType())) {
				final DbField dbf = (DbField) f.get(null);
				dbf.dbname = f.getName();
				fields.add(dbf);
				continue;
			}
			if (DbRelation.class.isAssignableFrom(f.getType())) {
				final DbRelation dbr = (DbRelation) f.get(null);
				dbr.name = f.getName();
				relations.add(dbr);
				continue;
			}
		}

	}

	@Override
	public String toString() {
		return jcls.getName() + " fields:" + fields + " relations:" + relations;
	}

	final String sql_createTable(StringBuilder sb) {
		sb.append("create table ").append(tableName).append("(");
		sql_createTableRec(sb, jcls);
		sb.setLength(sb.length() - 1);
		sb.append(")");
		return sb.toString();
	}

	private static void sql_createTableRec(StringBuilder sb, Class<?> c) {
		if (!c.getSuperclass().equals(Object.class))
			sql_createTableRec(sb, c.getSuperclass());
		final DbClass dbcls = Db.instance().dbClassForJavaClass(c);
		for (final DbField f : dbcls.fields) {
			f.sql_createField(sb);
			sb.append(',');
		}
	}

}
