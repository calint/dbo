package db.test;

import java.util.Map;

import db.DbField;

public final class FldChars extends DbField {

	public FldChars(int size, String def) {
		super("char", size, def == null ? null : def, false, true);
	}

//	@Override
//	protected void sql_updateValue(StringBuilder sb, DbObject o) {
//		final Object v = DbObject.getFieldValue(o, this);
//		sb.append('\'');
//		FldStr.escapeSqlString(sb, v.toString());
//		sb.append('\'');
//	}

	@Override
	protected void putDefaultValue(Map<DbField, Object> kvm) {
		final String def = getDefaultValue();
		if (def == null)
			return;
		kvm.put(this, def);
	}

}
