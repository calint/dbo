package db;

import java.util.Map;

public final class FldDbl extends DbField {
	private double defval;

	public FldDbl(final double def) {
		defval = def;
	}

	public FldDbl() {
		this(0.0);
	}

	@Override
	protected void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getDbl(this));
	}

	@Override
	protected void sql_columnDefinition(StringBuilder sb) {
		sb.append(name).append(" double default ").append(defval);
	}

	@Override
	protected void setDefaultValue(Map<String, Object> kvm) {
		kvm.put(this.name, defval);
	}
}
