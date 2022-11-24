package db;

import java.util.Map;

public final class FldLng extends DbField {
	final private long defval;

	public FldLng(final long def) {
		defval = def;
	}

	public FldLng() {
		this(0);
	}

	@Override
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {
		sb.append(o.getLng(this));
	}

	@Override
	protected void sql_columnDefinition(final StringBuilder sb) {
		sb.append(name).append(" bigint default ").append(defval);
	}

	@Override
	protected void setDefaultValue(final Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}

}
