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
	void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getFloat(this));
	}

	@Override
	void sql_createColumn(StringBuilder sb) {
		sb.append(columnName).append(" float default ").append(defval);
	}

	@Override
	void putDefaultValue(Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}
}