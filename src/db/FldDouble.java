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
	void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getDouble(this));
	}

	@Override
	void sql_createColumn(StringBuilder sb) {
		sb.append(columnName).append(" double default ").append(defval);
	}

	@Override
	void initDefaultValue(Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}
}