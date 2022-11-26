package db;

import java.util.Map;

public final class FldBool extends DbField {
	final private boolean defval;

	public FldBool(final boolean def) {
		defval = def;
	}

	public FldBool() {
		this(false);
	}

	@Override
	protected String getSqlType() {
		return "bit";
	}

	@Override
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {
		sb.append(o.getBool(this));
	}

	@Override
	protected void sql_columnDefinition(final StringBuilder sb) {
		sb.append(name).append(' ').append(getSqlType()).append("(1) default ").append(defval ? "1" : "0").append(" not null");
	}

	@Override
	protected void putDefaultValue(final Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}
}
