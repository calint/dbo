package db;

import java.sql.Timestamp;
import java.util.Map;

/** timestamp field */
public final class FldTs extends DbField {
	final private Timestamp defval;// ? min max values not big enough

	public FldTs(final Timestamp def) {
		defval = def;
	}

	public FldTs() {
		this(null);
	}

	@Override
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {
		sb.append('\'').append(o.getTs(this)).append('\'');
	}

	@Override
	protected void sql_columnDefinition(final StringBuilder sb) {
		sb.append(name).append(" timestamp");
		if (defval != null) {
			sb.append(" default '").append(defval).append("'");
		}
	}

	@Override
	protected void setDefaultValue(final Map<DbField, Object> kvm) {
		if (defval != null)
			kvm.put(this, defval);
	}
}
