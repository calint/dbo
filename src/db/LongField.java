package db;

import java.util.Map;

public final class LongField extends DbField {
	private long defval;

	public LongField(final long def) {
		defval = def;
	}

	public LongField() {
		this(0);
	}

	@Override
	void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getLong(this));
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