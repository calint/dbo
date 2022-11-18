package db;

public final class FldClob extends DbField {
	@Override
	void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append('\'').append(o.getStr(this).replace("'", "''")).append('\'');
	}

	@Override
	void sql_createColumn(StringBuilder sb) {
		sb.append(columnName).append(" longtext");
	}
}