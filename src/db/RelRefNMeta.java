package db;

// represents the relation table
class RelRefNMeta {
	Class<? extends DbObject> fromCls;
	Class<? extends DbObject> toCls;
	String tableName;

	public void sql_createTable(final StringBuilder sb) {
		sb.append("create table ").append(tableName).append('(').append(Db.tableNameForJavaClass(fromCls))
				.append(" int,").append(Db.tableNameForJavaClass(toCls)).append(" int)");
	}
}
