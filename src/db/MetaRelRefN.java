package db;

import java.util.Set;

// represents the relation table
final class MetaRelRefN {
	final Class<? extends DbObject> fromCls;
//	private final String relName;
	final Class<? extends DbObject> toCls;
	final String tableName;

	public MetaRelRefN(final Class<? extends DbObject> fromCls, final String relName,
			final Class<? extends DbObject> toCls) {
		this.fromCls = fromCls;
		this.toCls = toCls;
//		this.relName = relName;
		tableName = new StringBuilder(256).append(Db.tableNameForJavaClass(fromCls)).append('_').append(relName)
				.toString();
	}

	public void sql_createTable(final StringBuilder sb) {
		sb.append("create table ").append(tableName).append('(').append(Db.tableNameForJavaClass(fromCls))
				.append(" int,").append(Db.tableNameForJavaClass(toCls)).append(" int)");
	}

	public void sql_addToTable(final StringBuilder sb, DbObject from, DbObject to) {
		sb.append("insert into ").append(tableName).append(" values(").append(from.getId()).append(',')
				.append(to.getId()).append(')').toString();
	}

	public boolean tableIsIn(Set<String> tblNames) {
		return tblNames.contains(tableName);
	}
}
