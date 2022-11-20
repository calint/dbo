package db;

import java.util.Map;

public final class FldInt extends DbField {
	private int defval;

	public FldInt(final int def) {
		defval = def;
	}

	public FldInt() {
		this(0);
	}

	@Override
	void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getInt(this));
	}

	@Override
	void sql_createColumn(StringBuilder sb) {
		sb.append(columnName).append(" integer default ").append(defval);
	}

	@Override
	void putDefaultValue(Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}
}
