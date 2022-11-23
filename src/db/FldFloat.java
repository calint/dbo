package db;

import java.util.Map;

public final class FldFloat extends DbField {
	private float defval;

	public FldFloat(final float def) {
		defval = def;
	}

	public FldFloat() {
		this(0.0f);
	}

	@Override
	protected void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getFloat(this));
	}

	@Override
	protected void sql_columnDefinition(StringBuilder sb) {
		sb.append(name).append(" float default ").append(defval);
	}

	@Override
	protected void setDefaultValue(Map<String, Object> kvm) {
		kvm.put(name, defval);
	}
}
