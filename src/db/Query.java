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

		void sql_appendSelectFromTables(StringBuilder sb) {
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

	public Query() {
	}

	////////////////////////////////////////////////////////
	/** query by id */
	public Query(Class<? extends DbObject> c, long id) {
		append(NOP, Db.tableNameForJavaClass(c), DbObject.id.dbname, EQ, null, Long.toString(id));
	}

	public Query(DbField lh, int op, String rh) {
		append(NOP, lh.tableName, lh.dbname, op, null, sqlStr(rh));
	}

	public Query(DbField lh, int op, int rh) {
		append(NOP, lh.tableName, lh.dbname, op, null, Integer.toString(rh));
	}

	public Query(DbField lh, int op, DbField rh) {
		append(NOP, lh.tableName, lh.dbname, op, rh.tableName, rh.dbname);
	}

	public Query(RelAggN rel) {
		append(NOP, rel.tableName, DbObject.id.dbname, EQ, rel.toTableName, rel.fkfld.dbname);
	}

	public Query(RelRefN rel) {
		append(NOP, rel.tableName, DbObject.id.dbname, EQ, rel.rrm.tableName, rel.rrm.fromColName).append(AND,
				rel.rrm.tableName, rel.rrm.toColName, EQ, rel.rrm.toTableName, DbObject.id.dbname);
	}

	public Query(RelAgg rel) {
		append(NOP, rel.tableName, rel.fkfld.dbname, EQ, rel.toTableName, DbObject.id.dbname);
	}

	public Query(RelRef rel) {
		append(NOP, rel.tableName, rel.fkfld.dbname, EQ, rel.toTableName, DbObject.id.dbname);
	}

	///////////////////////////////////////////////////////////////////////
	public Query and(Query q) {
		return append(AND, q);
	}

	public Query or(Query q) {
		return append(OR, q);
	}

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
	/** query by id */
	public Query and(Class<? extends DbObject> c, long id) {
		return append(AND, Db.tableNameForJavaClass(c), DbObject.id.dbname, EQ, null, Long.toString(id));
	}

	public Query and(DbField lh, int op, String rh) {
		return append(AND, lh.tableName, lh.dbname, op, null, sqlStr(rh));
	}

	public Query and(DbField lh, int op, int rh) {
		return append(AND, lh.tableName, lh.dbname, op, null, Integer.toString(rh));
	}

	public Query and(DbField lh, int op, DbField rh) {
		return append(AND, lh.tableName, lh.dbname, op, rh.tableName, rh.dbname);
	}

	public Query and(RelAggN rel) {
		return append(AND, rel.tableName, DbObject.id.dbname, EQ, rel.toTableName, rel.fkfld.dbname);
	}

	public Query and(RelRefN rel) {
		return append(AND, rel.tableName, DbObject.id.dbname, EQ, rel.rrm.tableName, rel.rrm.fromColName).append(AND,
				rel.rrm.tableName, rel.rrm.toColName, EQ, rel.rrm.toTableName, DbObject.id.dbname);
	}

	public Query and(RelAgg rel) {
		return append(AND, rel.tableName, rel.fkfld.dbname, EQ, rel.toTableName, DbObject.id.dbname);
	}

	public Query and(RelRef rel) {
		return append(AND, rel.tableName, rel.fkfld.dbname, EQ, rel.toTableName, DbObject.id.dbname);
	}

	// - - - - - - -- --- - - -- - - -- - - -- --
	/** query by id */
	public Query or(Class<? extends DbObject> c, long id) {
		return append(OR, Db.tableNameForJavaClass(c), DbObject.id.dbname, EQ, null, Long.toString(id));
	}

	public Query or(DbField lh, int op, String rh) {
		return append(OR, lh.tableName, lh.dbname, op, null, sqlStr(rh));
	}

	public Query or(DbField lh, int op, int rh) {
		return append(OR, lh.tableName, lh.dbname, op, null, Integer.toString(rh));
	}

	public Query or(DbField lh, int op, DbField rh) {
		return append(OR, lh.tableName, lh.dbname, op, rh.tableName, rh.dbname);
	}

	public Query or(RelAggN rel) {
		return append(OR, rel.tableName, DbObject.id.dbname, EQ, rel.toTableName, rel.fkfld.dbname);
	}

	public Query or(RelRefN rel) {
		return append(OR, rel.tableName, DbObject.id.dbname, EQ, rel.rrm.tableName, rel.rrm.fromColName).append(AND,
				rel.rrm.tableName, rel.rrm.toColName, EQ, rel.rrm.toTableName, DbObject.id.dbname);
	}

	public Query or(RelAgg rel) {
		return append(OR, rel.tableName, rel.fkfld.dbname, EQ, rel.toTableName, DbObject.id.dbname);
	}

	public Query or(RelRef rel) {
		return append(OR, rel.tableName, rel.fkfld.dbname, EQ, rel.toTableName, DbObject.id.dbname);
	}
}
