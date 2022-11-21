package db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
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
	void init(final DbClass c) {
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
	void sql_createIndex(final Statement stmt, final DatabaseMetaData dbm) throws Throwable {

		final String fromIxName = rrm.tableName + '_' + rrm.fromTableName;
		final String toIxName = rrm.tableName + '_' + rrm.toTableName;

		final HashSet<String> lookingForIndexNames = new HashSet<String>();
		lookingForIndexNames.add(fromIxName);
		lookingForIndexNames.add(toIxName);

		final ResultSet rs = dbm.getIndexInfo(null, null, rrm.tableName, false, false);
		while (rs.next()) {
			final String indexName = rs.getString("INDEX_NAME");
			lookingForIndexNames.remove(indexName);
		}
		rs.close();

		if (lookingForIndexNames.isEmpty())
			return;

		if (lookingForIndexNames.contains(fromIxName)) {
			final StringBuilder sb = new StringBuilder(128);
			sb.append("create index ").append(rrm.tableName).append('_').append(rrm.fromTableName).append(" on ")
					.append(rrm.tableName).append('(').append(rrm.fromColName).append(')');

			final String sql = sb.toString();
			Db.log(sql);
			stmt.execute(sql);
		}

		if (lookingForIndexNames.contains(toIxName)) {
			final StringBuilder sb = new StringBuilder(128);
			sb.append("create index ").append(rrm.tableName).append('_').append(rrm.toTableName).append(" on ")
					.append(rrm.tableName).append('(').append(rrm.toColName).append(')');

			final String sql = sb.toString();
			Db.log(sql);
			stmt.execute(sql);
		}
	}

	@Override
	void cascadeDelete(DbObject ths) {
		removeAll(ths.id());
	}
}
