package db;

import java.util.Map;

public final class FldClob extends DbField {
	@Override
	protected String getSqlType() {
		return "longtext";
	}

	@Override
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {
		sb.append('\'');
		FldStr.escapeSqlString(sb, o.getStr(this));
		sb.append('\'');
	}

	@Override
	protected void sql_columnDefinition(final StringBuilder sb) {
		sb.append(name).append(' ').append(getSqlType());
	}
	
	@Override
	protected void putDefaultValue(final Map<DbField, Object> kvm) {
	}
}
