package db;

import java.util.ArrayList;

public class Query {
	static class Elem {
		int elemOp;
		String lh;
		int op;
		String rh;

		public void sql_build(final StringBuilder sb) {
			switch (elemOp) {
			case AND:
				sb.append(" and");
				break;
			case OR:
				sb.append(" OR");
				break;
			case NOP:
				break;
			default:
				throw new RuntimeException("invalid elemOp " + elemOp);
			}
			sb.append(' ');
			sb.append(lh);
			switch (op) {
			case OP_EQ:
				sb.append('=');
				break;
			default:
				throw new RuntimeException("op " + op + " not supported");
			}
			sb.append(rh);
		}
	}

	private ArrayList<Elem> elems = new ArrayList<>();
	public final static int NOP = 0;
	public final static int AND = 1;
	public final static int OR = 2;

	public final static int OP_EQ = 1;
	public final static int OP_NEQ = 2;
	public final static int OP_GT = 3;
	public final static int OP_GTE = 4;
	public final static int OP_LT = 5;
	public final static int OP_LTE = 6;

	public Query append(String lh, int op, String rh) {
		final Elem e = new Elem();
		e.elemOp = NOP;
		e.lh = lh;
		e.op = op;
		e.rh = rh;
		elems.add(e);
		return this;
	}

	public Query append(int elemOp, String lh, int op, String rh) {
		final Elem e = new Elem();
		e.elemOp = elemOp;
		e.lh = lh;
		e.op = op;
		e.rh = rh;
		elems.add(e);
		return this;
	}

	public Query append(DbField lh, int op, String rh) {
		return append(NOP, lh.dbname, op, rh);
	}

	public Query and(DbField lh, int op, String rh) {
		return append(AND, lh.dbname, op, rh);
	}

	public Query and(DbField lh, int op, DbField rh) {
		return append(AND, lh.dbname, op, rh.dbname);
	}

	public void sql_build(final StringBuilder sb) {
		for (final Elem e : elems)
			e.sql_build(sb);
	}
}
