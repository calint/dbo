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
	void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getLong(this));
	}

	@Override
	void sql_createColumn(StringBuilder sb) {
		sb.append(columnName).append(" bigint default ").append(defval);
	}

	@Override
	void initDefaultValue(Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}

}