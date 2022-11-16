package db;

import java.util.Set;

// represents the relation table
final class MetaRelRefN {
//	final Class<? extends DbObject> fromCls;
//	final Class<? extends DbObject> toCls;
	final String tableName; // the table name for this association NN table
	final String fromTableName; // the table name of the source
	final String fromColName; // column name where id referencing source of relation id
	final String toTableName; // table name of "to" class
	final String toColName; // column name where id to referencing target

	MetaRelRefN(final Class<? extends DbObject> fromCls, final String relName, final Class<? extends DbObject> toCls) {
//		this.fromCls = fromCls;
//		this.toCls = toCls;
		this.fromTableName = Db.tableNameForJavaClass(fromCls);
		this.fromColName = Db.tableNameForJavaClass(fromCls);
		this.toTableName = Db.tableNameForJavaClass(toCls);
		this.toColName = Db.tableNameForJavaClass(toCls);
		tableName = new StringBuilder(256).append(fromTableName).append('_').append(relName)
				.toString();
	}

	void sql_createTable(final StringBuilder sb) {
		sb.append("create table ").append(tableName).append('(').append(fromTableName).append(" int,")
				.append(toTableName).append(" int)");
	}

	void sql_addToTable(final StringBuilder sb, DbObject from, DbObject to) {
		sb.append("insert into ").append(tableName).append(" values(").append(from.getId()).append(',')
				.append(to.getId()).append(')').toString();
	}

	boolean tableIsIn(Set<String> tblNames) {
		return tblNames.contains(tableName);
	}
}
