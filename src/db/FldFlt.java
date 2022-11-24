package db;

import java.util.Map;

public final class FldFlt extends DbField {
	private float defval;

	public FldFlt(final float def) {
		defval = def;
	}

	public FldFlt() {
		this(0.0f);
	}

	@Override
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {
		sb.append(o.getFlt(this));
	}

	@Override
	protected void sql_columnDefinition(final StringBuilder sb) {
		sb.append(name).append(" float default ").append(defval);
	}

	@Override
	protected void setDefaultValue(final Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}
}