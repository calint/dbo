package main;

import java.util.Map;

import db.DbField;
import db.DbObject;
import db.FldStr;

public final class FldChars extends DbField {
//	private final String def;

	public FldChars(int size, String def) {
		super(size, def == null ? null : def, false);
//		this.def = def;
	}

	@Override
	protected String getSqlType() {
		return "char";
	}
	
	@Override
	protected boolean isDefaultValueString() {
		return true;
	}

//	@Override
//	protected void sql_columnDefinition(StringBuilder sb) {
//		sb.append(getName()).append(' ').append(getSqlType()).append('(').append(getSize()).append(')');
//		final String def = getDefaultValue();
//		if (def == null)
//			return;
//		sb.append(" default ");
//		sb.append('\'');
//		FldStr.escapeSqlString(sb, def);
//		sb.append("\' not null");
//	}

	@Override
	protected void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append('\'');
		FldStr.escapeSqlString(sb, o.getStr(this));
		sb.append('\'');
	}

	@Override
	protected void putDefaultValue(Map<DbField, Object> kvm) {
		final String def = getDefaultValue();
		if (def == null)
			return;
		kvm.put(this, def);
	}

}
