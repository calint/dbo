package db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.util.List;

public final class RelRefN extends DbRelation {
	private final Class<? extends DbObject> toCls;
	final String toTableName;
	RelRefNMeta rrm;

	public RelRefN(Class<? extends DbObject> toCls) {
		this.toCls = toCls;
		toTableName = Db.tableNameForJavaClass(toCls);
	}

	@Override
	void connect(final DbClass c) {
		rrm = new RelRefNMeta(c.javaClass, name, toCls);
		Db.instance().relRefNMeta.add(rrm);
		final DbClass todbcls = Db.instance().dbClassForJavaClass(toCls);
		todbcls.referingRefN.add(this);
	}

	public void add(final DbObject from, final int toId) {
		final StringBuilder sb = new StringBuilder(256);
		rrm.sql_addToTable(sb, from.id(), toId);
		Db.currentTransaction().execSql(sb);
	}

//	public void add(final DbObject from, final DbObject to) {
//		add(from, to.getId());
//	}

	public List<DbObject> get(final DbObject ths, final Query qry, final Order ord, final Limit lmt) {
		final Query q = new Query(ths.getClass(), ths.id()).and(this);
		if (qry != null)
			q.and(qry);
		return Db.currentTransaction().get(toCls, q, ord, lmt);
	}

	public int getCount(final DbObject ths, final Query qry) {
		final Query q = new Query(ths.getClass(), ths.id()).and(this);
		if (qry != null)
			q.and(qry);
		return Db.currentTransaction().getCount(toCls, q);
	}

	public void remove(final DbObject from, final int toId) {
		final StringBuilder sb = new StringBuilder(256);
		rrm.sql_deleteFromTable(sb, from.id(), toId);
		Db.currentTransaction().execSql(sb);
	}

	void removeAll(final int id) {
		final StringBuilder sb = new StringBuilder(256);
		rrm.sql_deleteAllFromTable(sb, id);
		Db.currentTransaction().execSql(sb);
	}

	void deleteReferencesTo(final int id) {
		final StringBuilder sb = new StringBuilder(256);
		rrm.sql_deleteReferencesTo(sb, id);
		Db.currentTransaction().execSql(sb);
	}

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

	@Override
	void cascadeDelete(DbObject ths) {
		removeAll(ths.id());
	}
}
