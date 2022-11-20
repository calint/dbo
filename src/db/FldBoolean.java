package db;

import java.util.Map;

public final class FldBoolean extends DbField {
	private boolean defval;

	public FldBoolean(final boolean def) {
		defval = def;
	}

	public FldBoolean() {
		this(false);
	}

	@Override
	void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getBoolean(this));
	}

	@Override
	void sql_createColumn(StringBuilder sb) {
		sb.append(columnName).append(" boolean default ").append(defval);
	}

	@Override
	void putDefaultValue(Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}
}
