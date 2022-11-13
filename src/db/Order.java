package db;

import java.util.ArrayList;

public final class Order {
	static class Elem {
		String tableName;
		String columnName;
		String dir;
	}

	private ArrayList<Elem> elems = new ArrayList<Elem>();

	public Order(DbField fld) {
		append(fld, true);
	}

	public Order(DbField fld, boolean ascending) {
		append(fld, ascending);
	}

	/** sort on id */
	public Order(Class<? extends DbObject> cls,boolean ascending) {
		final Elem e = new Elem();
		e.tableName = Db.tableNameForJavaClass(cls);
		e.columnName = DbObject.id.columnName;
		e.dir = ascending ? "" : "desc";
		elems.add(e);
	}

	public Order append(DbField fld) {
		return append(fld, true);
	}

	public Order append(DbField fld, boolean ascending) {
		final Elem e = new Elem();
		e.tableName = fld.tableName;
		e.columnName = fld.columnName;
		e.dir = ascending ? "" : "desc";
		elems.add(e);
		return this;
	}

	void sql_appendToQuery(final StringBuilder sb, Query.TableAliasMap tam) {
		if (elems.isEmpty())
			return;
		sb.append("order by ");
		for (final Elem e : elems) {
			final String s = tam.getAliasForTableName(e.tableName);
			sb.append(s).append('.').append(e.columnName);
			if (e.dir.length() > 0) {
				sb.append(' ').append(e.dir);
			}
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append(' ');
	}
}
