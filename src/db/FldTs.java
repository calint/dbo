package db;

import java.sql.Timestamp;
import java.util.Map;

/** Timestamp field */
public final class FldTs extends DbField {
	final private Timestamp defval;// ? min max values not big enough

	public FldTs(final Timestamp def) {
		defval = def;
	}

	public FldTs() {
		this(null);
	}

	@Override
	protected String getSqlType() {
		return "timestamp";
	}

	@Override
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {
		sb.append('\'').append(o.getTs(this)).append('\'');
	}

	@Override
	protected void sql_columnDefinition(final StringBuilder sb) {
		sb.append(name).append(' ').append(getSqlType());
		if (defval != null) {
			sb.append(" default '").append(defval).append("'");
		}
	}

	@Override
	protected void putDefaultValue(final Map<DbField, Object> kvm) {
		if (defval == null)
			return;
		kvm.put(this, defval);
	}
}
