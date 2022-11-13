package db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class Query {
	public final static int EQ = 1;
	public final static int NEQ = 2;
	public final static int GT = 3;
	public final static int GTE = 4;
	public final static int LT = 5;
	public final static int LTE = 6;

	static class Elem {
		int elemOp;
		String lhtbl;// left hand table name
		String lh; // left hand field name
		int op;
		String rhtbl;
		String rh;
		Query query;

		public void sql_build(final StringBuilder sb, TableAliasMap tam) {
			switch (elemOp) {
			case AND:
				sb.append("and ");
				break;
			case OR:
				sb.append("or ");
				break;
			case NOP:
				break;
			default:
				throw new RuntimeException("invalid elemOp " + elemOp);
			}

			// sub query
			if (query != null) {
				sb.append('(');
				query.sql_build(sb, tam);
				sb.append(") ");
				return;
			}

			// left hand right hand
			if (lhtbl != null) {
				sb.append(tam.getAliasForTableName(lhtbl)).append('.');
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

			if (rhtbl != null) {
				sb.append(tam.getAliasForTableName(rhtbl)).append('.');
			}
			sb.append(rh).append(' ');
		}
	}

	static class TableAliasMap {
		private int seq;
		final private HashMap<String, String> tblToAlias = new HashMap<String, String>();

		String getAliasForTableName(String tblname) {
			String tblalias = tblToAlias.get(tblname);
			if (tblalias == null) {
				seq++;
				tblalias = "t" + seq;
				tblToAlias.put(tblname, tblalias);
			}
			return tblalias;
		}

		void sql_appendFromTables(StringBuilder sb) {
			for (Map.Entry<String, String> kv : tblToAlias.entrySet()) {
				sb.append(kv.getKey()).append(" as ").append(kv.getValue()).append(", ");
			}
			sb.setLength(sb.length() - 2);
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

	void sql_build(final StringBuilder sb, TableAliasMap tam) {
		for (final Elem e : elems)
			e.sql_build(sb, tam);
	}

//	public Query() {
//	}
	////////////////////////////////////////////////////////
	public Query(DbField lh, int op, String rh) {
		append(NOP, Db.tableNameForJavaClass(lh.cls), lh.dbname, op, null, sqlStr(rh));
	}

	public Query(DbField lh, int op, int rh) {
		append(NOP, Db.tableNameForJavaClass(lh.cls), lh.dbname, op, null, Integer.toString(rh));
	}

	public Query(DbField lh, int op, DbField rh) {
		append(NOP, Db.tableNameForJavaClass(lh.cls), lh.dbname, op, Db.tableNameForJavaClass(rh.cls), rh.dbname);
	}

	public Query(RelAggN rel) {
		append(NOP, Db.tableNameForJavaClass(rel.cls), DbObject.id.dbname, EQ, Db.tableNameForJavaClass(rel.toCls),
				rel.fkfld.dbname);
	}

	public Query(RelRefN rel) {
		append(NOP, Db.tableNameForJavaClass(rel.cls), DbObject.id.dbname, EQ, rel.rrm.tableName,
				Db.tableNameForJavaClass(rel.rrm.fromCls)).append(AND, rel.rrm.tableName,
						Db.tableNameForJavaClass(rel.rrm.toCls), EQ, Db.tableNameForJavaClass(rel.rrm.toCls),
						DbObject.id.dbname);
	}

	public Query(RelAgg rel) {
		append(NOP, Db.tableNameForJavaClass(rel.cls), rel.fkfld.dbname, EQ, Db.tableNameForJavaClass(rel.toCls),
				DbObject.id.dbname);
	}

	public Query(RelRef rel) {
		append(NOP, Db.tableNameForJavaClass(rel.cls), rel.fkfld.dbname, EQ, Db.tableNameForJavaClass(rel.toCls),
				DbObject.id.dbname);
	}

//	public Query(RelAgg lh, int op, int rh) {
//		append(NOP, Db.tableNameForJavaClass(lh.cls), lh.name, op, null, Integer.toString(rh));
//	}
//
//	public Query(RelRef lh, int op, int rh) {
//		append(NOP, Db.tableNameForJavaClass(lh.cls), lh.name, op, null, Integer.toString(rh));
//	}
	///////////////////////////////////////////////////////////////////////

	private Query append(int elemOp, String lhtbl, String lh, int op, String rhtbl, String rh) {
		final Elem e = new Elem();
		e.elemOp = elemOp;
		e.lhtbl = lhtbl;
		e.lh = lh;
		e.op = op;
		e.rhtbl = rhtbl;
		e.rh = rh;
		elems.add(e);
		return this;
	}

	private Query append(int elemOp, Query q) {
		final Elem e = new Elem();
		e.elemOp = elemOp;
		e.query = q;
		elems.add(e);
		return this;
	}

	// - - - - - - -- --- - - -- - - -- - - -- --
	public Query and(Query q) {
		return append(AND, q);
	}

	public Query or(Query q) {
		return append(OR, q);
	}

	public Query and(Class<? extends DbObject> cls, int op, int rh) {// ?
		return append(AND, Db.tableNameForJavaClass(cls), DbObject.id.dbname, op, null, Integer.toString(rh));
	}

	public Query and(DbField lh, int op, String rh) {
		return append(AND, Db.tableNameForJavaClass(lh.cls), lh.dbname, op, null, sqlStr(rh));
	}

	public Query and(DbField lh, int op, int rh) {
		return append(AND, Db.tableNameForJavaClass(lh.cls), lh.dbname, op, null, Integer.toString(rh));
	}

	public Query and(DbField lh, int op, DbField rh) {
		return append(AND, Db.tableNameForJavaClass(lh.cls), lh.dbname, op, Db.tableNameForJavaClass(rh.cls),
				rh.dbname);
	}

	/** join */
	public Query and(RelAggN rel) {
		return append(AND, Db.tableNameForJavaClass(rel.cls), DbObject.id.dbname, EQ,
				Db.tableNameForJavaClass(rel.toCls), rel.fkfld.dbname);
	}

	/** join */
	public Query and(RelRefN rel) {
		return append(AND, Db.tableNameForJavaClass(rel.cls), DbObject.id.dbname, EQ, rel.rrm.tableName,
				Db.tableNameForJavaClass(rel.rrm.fromCls)).append(AND, rel.rrm.tableName,
						Db.tableNameForJavaClass(rel.rrm.toCls), EQ, Db.tableNameForJavaClass(rel.rrm.toCls),
						DbObject.id.dbname);
	}
////	public Query and(RelAgg lh, int op, int rh) {
////		return append(AND, lh.name, op, Integer.toString(rh));
////	}
////
////	public Query and(RelRef lh, int op, int rh) {
////		return append(AND, lh.name, op, Integer.toString(rh));
////	}
//
}
