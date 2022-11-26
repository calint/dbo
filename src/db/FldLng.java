package db;

import java.util.Map;

/** Long field */
public final class FldLng extends DbField {
	final private long defval;

	public FldLng(final long def) {
		super(0, Long.toString(def), false);
		defval = def;
	}

	public FldLng() {
		this(0);
	}

	@Override
	protected String getSqlType() {
		return "bigint";
	}

	@Override
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {
		sb.append(o.getLng(this));
	}

//	@Override
//	protected void sql_columnDefinition(final StringBuilder sb) {
//		sb.append(name).append(' ').append(getSqlType()).append(" default ").append(defval).append(" not null");
//	}

	@Override
	protected void putDefaultValue(final Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}

}
