package db;

import java.util.ArrayList;

public final class Query {
	public final static int EQ = 1;
	public final static int NEQ = 2;
	public final static int GT = 3;
	public final static int GTE = 4;
	public final static int LT = 5;
	public final static int LTE = 6;

	static class Elem {
		int elemOp;
		String lh;
		int op;
		String rh;
		Query query;

		public void sql_build(final StringBuilder sb) {
			switch (elemOp) {
			case AND:
				sb.append(" and ");
				break;
			case OR:
				sb.append(" or ");
				break;
			case NOP:
				sb.append(' ');
				break;
			default:
				throw new RuntimeException("invalid elemOp " + elemOp);
			}

			if (query != null) {
				sb.append('(');
				query.sql_build(sb);
				sb.append(')');
				return;
			}

			sb.append(lh);
			switch (op) {
			case EQ:
				sb.append('=');
				break;
			case NEQ:
				sb.append("!=");
				break;
			case GT:
				sb.append('>');
				break;
			case GTE:
				sb.append(">=");
				break;
			case LT:
				sb.append('<');
				break;
			case LTE:
				sb.append("<=");
				break;
			default:
				throw new RuntimeException("op " + op + " not supported");
			}
			sb.append(rh);
		}
	}

	private ArrayList<Elem> elems = new ArrayList<Elem>();

	public final static int NOP = 0;
	public final static int AND = 1;
	public final static int OR = 2;

	private static String sqlStr(final String s) {
		final StringBuilder sb = new StringBuilder(128);
		sb.append('\'').append(s.replace("'", "''")).append('\'');
		return sb.toString();
	}

	void sql_build(final StringBuilder sb) {
		for (final Elem e : elems)
			e.sql_build(sb);
	}

	public Query() {
	}

	private Query append(int elemOp, String lh, int op, String rh) {
		final Elem e = new Elem();
		e.elemOp = elemOp;
		e.lh = lh;
		e.op = op;
		e.rh = rh;
		elems.add(e);
		return this;
	}

	public Query(DbField lh, int op, String rh) {
		append(NOP, lh.dbname, op, sqlStr(rh));
	}

	public Query(DbField lh, int op, DbField rh) {
		append(NOP, lh.dbname, op, rh.dbname);
	}

	public Query(DbField lh, int op, int rh) {
		append(NOP, lh.dbname, op, Integer.toString(rh));
	}

	private Query append(int elemOp, Query q) {
		final Elem e = new Elem();
		e.elemOp = elemOp;
		e.query = q;
		elems.add(e);
		return this;
	}

//	public Query joinOn(RelAggN aggn) {
//
//		return this;
//	}

	public Query and(Query q) {
		return append(AND, q);
	}

	public Query and(DbField lh, int op, DbField rh) {
		return append(AND, lh.dbname, op, rh.dbname);
	}

	public Query and(DbField lh, int op, String rh) {
		return append(AND, lh.dbname, op, sqlStr(rh));
	}

	public Query and(DbField lh, int op, int rh) {
		return append(AND, lh.dbname, op, Integer.toString(rh));
	}

	public Query and(RelAgg lh, int op, int rh) {
		return append(AND, lh.name, op, Integer.toString(rh));
	}

	public Query and(RelRef lh, int op, int rh) {
		return append(AND, lh.name, op, Integer.toString(rh));
	}

	public Query or(Query q) {
		return append(OR, q);
	}

	public Query or(DbField lh, int op, DbField rh) {
		return append(OR, lh.dbname, op, rh.dbname);
	}

	public Query or(DbField lh, int op, String rh) {
		return append(OR, lh.dbname, op, sqlStr(rh));
	}

	public Query or(DbField lh, int op, int rh) {
		return append(OR, lh.dbname, op, Integer.toString(rh));
	}

	public Query or(RelAgg lh, int op, int rh) {
		return append(OR, lh.name, op, Integer.toString(rh));
	}

	public Query or(RelRef lh, int op, int rh) {
		return append(OR, lh.name, op, Integer.toString(rh));
	}

}
