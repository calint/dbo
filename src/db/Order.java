package db;

import java.util.ArrayList;

public final class Order {
	static class Elem {
		String arg;
		String dir;
	}

	private ArrayList<Elem> elems = new ArrayList<Elem>();

	public Order(DbField fld) {
		append(fld, true);
	}

	public Order(DbField fld, boolean ascending) {
		append(fld, ascending);
	}

	public Order append(DbField fld) {
		return append(fld, true);
	}

	public Order append(DbField fld, boolean ascending) {
		final Elem e = new Elem();
		e.arg = fld.dbname;
		e.dir = ascending ? "" : "desc";
		elems.add(e);
		return this;
	}

	void sql_to(final StringBuilder sb) {
		if (elems.isEmpty())
			return;
		sb.append(" order by ");
		for (final Elem e : elems) {
			sb.append(e.arg);
			if (e.dir.length() > 0) {
				sb.append(' ').append(e.dir);
			}
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
	}
}
