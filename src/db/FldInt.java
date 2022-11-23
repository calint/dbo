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
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {
		sb.append(o.getInt(this));
	}

	@Override
	protected void sql_columnDefinition(final StringBuilder sb) {
		sb.append(name).append(" int default ").append(defval);
	}

	@Override
	protected void setDefaultValue(final Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}
}
