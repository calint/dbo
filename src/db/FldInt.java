package db;

import java.util.Map;

public final class FldInt extends DbField {
	private int defval;

	public FldInt(final int def) {
		defval = def;
	}

	public FldInt() {
		this(0);
	}

	@Override
	protected void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getInt(this));
	}

	@Override
	protected void sql_columnDefinition(StringBuilder sb) {
		sb.append(name).append(" integer default ").append(defval);
	}

	@Override
	protected void setDefaultValue(Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}
}
