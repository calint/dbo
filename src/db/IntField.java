package db;

import java.util.Map;

public final class IntField extends DbField {
	private int defval;

	public IntField(final int def) {
		defval = def;
	}

	public IntField() {
		this(0);
	}

	@Override
	void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getInt(this));
	}

	@Override
	void sql_createField(StringBuilder sb) {
		sb.append(dbname).append(" int default ").append(defval);
	}

	@Override
	void initDefaultValue(Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}
}