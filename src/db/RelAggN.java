package db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

public final class RelAggN extends DbRelation {
	final Class<? extends DbObject> toCls;
	final String toTableName;
	FldRel relFld;

	public RelAggN(Class<? extends DbObject> toCls) {
		this.toCls = toCls;
		toTableName = Db.tableNameForJavaClass(toCls);
	}

	@Override
	void connect(final DbClass dbcls) {
		final DbClass toDbCls = Db.instance().dbClassForJavaClass(toCls);
		relFld = new FldRel();
		relFld.columnName = dbcls.tableName + "_" + name;
		toDbCls.declaredFields.add(relFld);
	}

	public DbObject create(final DbObject ths) {
		final DbObject o = Db.currentTransaction().create(toCls);
		o.set(relFld, ths.id());
		return o;
	}

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

	public void delete(final DbObject ths, final int toId) {
		final DbObject o = Db.currentTransaction().get(toCls, new Query(toCls, toId), null, null).get(0);

		if (!o.fieldValues.containsKey(relFld) || o.getInt(relFld) != ths.id())
			throw new RuntimeException(ths.getClass().getName() + "[" + ths.id() + "] does not contain "
					+ toCls.getName() + "[" + toId + "] in relation '" + this.name + "'");

//		o.deleteFromDb();
		Db.currentTransaction().delete(o);
	}

	public void delete(final DbObject ths, final DbObject o) {
		if (!o.fieldValues.containsKey(relFld) || o.getInt(relFld) != ths.id())
			throw new RuntimeException(ths.getClass().getName() + "[" + ths.id() + "] does not contain "
					+ toCls.getName() + "[" + o.id() + "] in relation '" + this.name + "'");

//		o.deleteFromDb();
		Db.currentTransaction().delete(o);
	}

	@Override
	void sql_createIndex(final Statement stmt, final DatabaseMetaData dbm) throws Throwable {
		final ResultSet rs = dbm.getIndexInfo(null, null, toTableName, false, false);
		boolean found = false;
		while (rs.next()) {
			final String indexName = rs.getString("INDEX_NAME");
			if (indexName.equals(relFld.columnName)) {
				found = true;
				break;
			}
		}
		rs.close();
		if (found == true)
			return;

		final StringBuilder sb = new StringBuilder(128);
		sb.append("create index ").append(relFld.columnName).append(" on ").append(toTableName).append('(')
				.append(relFld.columnName).append(')');

		final String sql = sb.toString();
		Db.log(sql);
		stmt.execute(sql);
	}

	@Override
	void cascadeDelete(DbObject ths) {
		final List<DbObject> ls = get(ths, null, null, null);
		for (final DbObject o : ls) {
//			o.deleteFromDb();
			Db.currentTransaction().delete(o);
		}
	}
}
