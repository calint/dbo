package db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

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
			o.set(relFld, ths.getId());
			o.createInDb();
			return o;
		} catch (Throwable t) {
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