package db;

import java.sql.Timestamp;
import java.util.Map;

public class FldDateTime extends DbField {
	final private Timestamp defval;

	public FldDateTime() {
		this(null);
	}

	public FldDateTime(final Timestamp def) {
		super(0, def == null ? null : def.toString(), true);
		defval = def;
	}

	@Override
	protected String getSqlType() {
		return "datetime";
	}

	@Override
	protected void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append('\'').append(o.getTs(this)).append('\'');
	}

	@Override
	protected void putDefaultValue(Map<DbField, Object> kvm) {
		if (defval == null)
			return;

		kvm.put(this, defval);
	}

}
