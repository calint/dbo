package db;

import java.util.Map;

public final class FldDouble extends DbField {
	private double defval;

	public FldDouble(final double def) {
		defval = def;
	}

	public FldDouble() {
		this(0.0);
	}

	@Override
	protected void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getDouble(this));
	}

	@Override
	protected void sql_columnDefinition(StringBuilder sb) {
		sb.append(name).append(" double default ").append(defval);
	}

	@Override
	protected void setDefaultValue(Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}
}
