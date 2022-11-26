package db;

import java.util.Map;

/** Float field */
public final class FldFlt extends DbField {
	private float defval;

	public FldFlt(final float def) {
		super(0, defValToStr(def), false);
		defval = def;
	}

	// mysql default values returns no decimals if none necessary
	private static String defValToStr(final float def) {
		String s = Float.toString(def);
		if (s.endsWith(".0"))
			s = s.substring(0, s.length() - 2);
		return s;
	}

	public FldFlt() {
		this(0.0f);
	}

	@Override
	protected String getSqlType() {
		return "float";
	}

	@Override
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {
		sb.append(o.getFlt(this));
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
