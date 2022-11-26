package db;

import java.util.Map;

public final class FldInt extends DbField {
	final private int defval;

	public FldInt(final int def) {
		defval = def;
	}

	public FldInt() {
		this(0);
	}

	@Override
	protected String getSqlType() {
		return "int";
	}

	@Override
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {
		sb.append(o.getInt(this));
	}

	@Override
	protected void sql_columnDefinition(final StringBuilder sb) {
		sb.append(name).append(' ').append(getSqlType()).append(" default ").append(defval).append(" not null");
	}

	@Override
	protected void putDefaultValue(final Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}
}
