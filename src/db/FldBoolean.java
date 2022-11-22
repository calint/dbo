package db;

import java.util.Map;

public final class FldBoolean extends DbField {
	private boolean defval;

	public FldBoolean(final boolean def) {
		defval = def;
	}

	public FldBoolean() {
		this(false);
	}

	@Override
	protected void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getBoolean(this));
	}

	@Override
	protected void sql_columnDefinition(StringBuilder sb) {
		sb.append(name).append(" boolean default ").append(defval);
	}

	@Override
	protected void setDefaultValue(Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}
}
