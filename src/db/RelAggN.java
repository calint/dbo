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
		try {
			final DbObject o = toCls.getConstructor().newInstance();
			o.set(relFld, ths.id());
			o.createInDb();
			return o;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public List<DbObject> get(final DbObject ths, final Query qry, final Order ord, final Limit lmt) {
		final Query q = new Query(ths.getClass(), ths.id()).and(this);
		if (qry != null)
			q.and(qry);
		return Db.currentTransaction().get(toCls, q, ord, lmt);
	}

	public void delete(final DbObject ths, final int toId) {
		final Statement stmt = Db.currentTransaction().stmt;
		final StringBuilder sb = new StringBuilder(256);
//		sb.append("delete from ").append(toTableName).append(" where ").append(relFld.columnName).append('=')
//				.append(ths.getId()).append(" and ").append(DbObject.id.columnName).append('=').append(toId);
		sb.append("delete from ").append(toTableName).append(" where ").append(DbObject.id.columnName).append('=')
				.append(toId);
		final String sql = sb.toString();
		Db.log(sql);
		try {
			stmt.execute(sql);
			// ? if object is in diry list it will make an update the will change no rows.
			// bug?
		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public void deleteAll(final DbObject ths) {
		final Statement stmt = Db.currentTransaction().stmt;
		final StringBuilder sb = new StringBuilder(256);
		sb.append("delete from ").append(toTableName).append(" where ").append(relFld.columnName).append('=')
				.append(ths.id());
//		sb.append("delete from ").append(toTableName).append(" where ").append(DbObject.id.columnName).append('=')
//				.append(toId);
		final String sql = sb.toString();
		Db.log(sql);
		try {
			stmt.execute(sql);
			// ? if object is in diry list it will make an update the will change no rows.
			// bug?
		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
	}

	@Override
	void sql_createIndex(final StringBuilder sb, final DatabaseMetaData dbm) throws Throwable {
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

		// create index User_refFiles on User_refFiles(User);
		sb.append("create index ").append(relFld.columnName).append(" on ").append(toTableName).append('(')
				.append(relFld.columnName).append(')');

	}
}