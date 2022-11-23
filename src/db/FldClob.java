package db;

public final class FldClob extends DbField {
	@Override
	protected void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append('\'');
		FldStr.escapeSqlString(sb, o.getStr(this));
		sb.append('\'');
	}

	@Override
	protected void sql_columnDefinition(StringBuilder sb) {
		sb.append(name).append(" longtext");
	}
}
