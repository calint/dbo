package db;

import java.sql.Timestamp;
import java.util.Map;

public final class FldTimestamp extends DbField {
	private Timestamp defval;// ? min max values not big enough

	public FldTimestamp(Timestamp def) {
		defval = def;
	}

	public FldTimestamp() {
		this(null);
	}

	@Override
	void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append('\'').append(o.getTimestamp(this)).append('\'');
	}

	@Override
	void sql_createColumn(StringBuilder sb) {
		sb.append(columnName).append(" timestamp");
		if (defval != null) {
			sb.append(" default '").append(defval).append("'");
		}
	}

	@Override
	void putDefaultValue(Map<DbField, Object> kvm) {
		if (defval != null)
			kvm.put(this, defval);
	}

}