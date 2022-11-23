package db;

import java.util.Map;

public final class FldLong extends DbField {
	private long defval;

	public FldLong(final long def) {
		defval = def;
	}

	public FldLong() {
		this(0);
	}

	@Override
	protected void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getLong(this));
	}

	@Override
	protected void sql_columnDefinition(StringBuilder sb) {
		sb.append(name).append(" bigint default ").append(defval);
	}

	@Override
	protected void setDefaultValue(Map<String, Object> kvm) {
		kvm.put(name, defval);
	}

}
