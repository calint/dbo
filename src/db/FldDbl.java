package db;

import java.util.Map;

/** double field */
public final class FldDbl extends DbField {
	final private double defval;

	public FldDbl(final double def) {
		defval = def;
	}

	public FldDbl() {
		this(0.0);
	}

	@Override
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {
		sb.append(o.getDbl(this));
	}

	@Override
	protected void sql_columnDefinition(final StringBuilder sb) {
		sb.append(name).append(" double default ").append(defval);
	}

	@Override
	protected void setDefaultValue(final Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}
}
