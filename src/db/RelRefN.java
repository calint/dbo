package db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

public final class RelRefN extends DbRelation {
	private final Class<? extends DbObject> toCls;
	final String toTableName;
	MetaRelRefN rrm;

	public RelRefN(Class<? extends DbObject> toCls) {
		this.toCls = toCls;
		toTableName = Db.tableNameForJavaClass(toCls);
	}

	@Override
	void connect(final DbClass c) {
		rrm = new MetaRelRefN(c.javaClass, name, toCls);
		Db.instance().relRefNMeta.add(rrm);
	}

	public void add(final DbObject from, final int toId) {
		final Statement stmt = Db.currentTransaction().stmt;
		final StringBuilder sb = new StringBuilder(256);
		rrm.sql_addToTable(sb, from.getId(), toId);
		final String sql = sb.toString();
		Db.log(sql);
		try {
			stmt.execute(sql);
		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
	}

//	public void add(final DbObject from, final DbObject to) {
//		add(from, to.getId());
//	}

	public void remove(final DbObject from, final int toId) {
		final Statement stmt = Db.currentTransaction().stmt;
		final StringBuilder sb = new StringBuilder(256);
		rrm.sql_deleteFromTable(sb, from.getId(), toId);
		final String sql = sb.toString();
		Db.log(sql);
		try {
			stmt.execute(sql);
		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public void removeAll(final DbObject from) {
		final Statement stmt = Db.currentTransaction().stmt;
		final StringBuilder sb = new StringBuilder(256);
		rrm.sql_deleteAllFromTable(sb, from.getId());
		final String sql = sb.toString();
		Db.log(sql);
		try {
			stmt.execute(sql);
		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
	}

//	public void remove(final DbObject from, final DbObject to) {
//		remove(from, to.getId());
//	}

	@Override
	void sql_createIndex(final StringBuilder sb, final DatabaseMetaData dbm) throws Throwable {
		final ResultSet rs = dbm.getIndexInfo(null, null, rrm.tableName, false, false);
		boolean found = false;
		while (rs.next()) {
			final String indexName = rs.getString("INDEX_NAME");
			if (indexName.equals(rrm.tableName)) {
				found = true;
				break;
			}
		}
		rs.close();
		if (found == true)
			return;

		// create index User_refFiles on User_refFiles(User);
		sb.append("create index ").append(rrm.tableName).append(" on ").append(rrm.tableName).append('(')
				.append(rrm.fromColName).append(')');
	}

}
