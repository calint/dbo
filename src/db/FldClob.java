package db;

public final class FldClob extends DbField {
	@Override
	void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append('\'');
		FldString.sqlEscapeString(sb, o.getStr(this));
		sb.append('\'');
	}

	@Override
	void sql_createColumn(StringBuilder sb) {
		sb.append(columnName).append(" longtext");
	}
}