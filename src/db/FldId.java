package db;

import java.util.Map;

public final class FldId extends DbField {
	@Override
	protected String getSqlType() {
		return "int";
	}

	@Override
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {// ? never gets called
		sb.append(o.getInt(this));
	}

	@Override
	protected void sql_columnDefinition(final StringBuilder sb) {
		sb.append(name).append(' ').append(getSqlType()).append(" primary key auto_increment");// ? maybe bigint
	}
	
	@Override
	protected void setDefaultValue(final Map<DbField, Object> kvm) {
	}
}
