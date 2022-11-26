package db;

import java.util.Map;

/** Double field */
public final class FldDbl extends DbField {
	final private double defval;

	public FldDbl(final double def) {
		super(0, defValToStr(def), false);
		defval = def;
	}

	// mysql default values returns no decimals if none necessary
	private static String defValToStr(final double def) {
		String s = Double.toString(def);
		if (s.endsWith(".0"))
			s = s.substring(0, s.length() - 2);
		return s;
	}

	public FldDbl() {
		this(0.0);
	}

	@Override
	protected String getSqlType() {
		return "double";
	}

	@Override
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {
		sb.append(o.getDbl(this));
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
