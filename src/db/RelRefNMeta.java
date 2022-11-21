package db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

// represents the relation table
final class RelRefNMeta {
//	final Class<? extends DbObject> fromCls;
//	final Class<? extends DbObject> toCls;
	final String tableName; // the table name for this association NN table
	final String fromTableName; // the table name of the source
	final String fromColName; // column name where id referencing source of relation id
	final String toTableName; // table name of "to" class
	final String toColName; // column name where id to referencing target

	RelRefNMeta(final Class<? extends DbObject> fromCls, final String relName, final Class<? extends DbObject> toCls) {
//		this.fromCls = fromCls;
//		this.toCls = toCls;
		this.fromTableName = Db.tableNameForJavaClass(fromCls);
		this.fromColName = Db.tableNameForJavaClass(fromCls);
		this.toTableName = Db.tableNameForJavaClass(toCls);
		this.toColName = Db.tableNameForJavaClass(toCls);
		tableName = new StringBuilder(256).append(fromTableName).append('_').append(relName).toString();
	}

	void sql_createTable(final StringBuilder sb, final DatabaseMetaData dbm) throws Throwable {
		final ResultSet rs = dbm.getTables(null, null, tableName, new String[] { "TABLE" });
		if (rs.next()) {
			rs.close();
			return;// ? check columns
		}
		rs.close();

		sb.append("create table ").append(tableName).append('(').append(fromTableName).append(" int,")
				.append(toTableName).append(" int)");
	}

	void sql_addToTable(final StringBuilder sb, final int fromId, final int toId) {
		sb.append("insert into ").append(tableName).append(" values(").append(fromId).append(',').append(toId)
				.append(')');
	}

	void sql_deleteFromTable(StringBuilder sb, int fromId, int toId) {
		sb.append("delete from ").append(tableName).append(" where ").append(fromColName).append('=').append(fromId)
				.append(" and ").append(toColName).append('=').append(toId);

	}

	void sql_deleteAllFromTable(StringBuilder sb, int fromId) {
		sb.append("delete from ").append(tableName).append(" where ").append(fromColName).append('=').append(fromId);
	}

	void sql_deleteReferencesTo(final StringBuilder sb, int id) {
		sb.append("delete from ").append(tableName).append(" where ").append(toColName).append('=').append(id);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(this.tableName);
		return super.toString();
	}
}
