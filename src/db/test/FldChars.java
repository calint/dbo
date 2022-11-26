package db.test;

import java.util.Map;

import db.DbField;
import db.DbObject;
import db.FldStr;

public final class FldChars extends DbField {

	public FldChars(int size, String def) {
		super(size, def == null ? null : def, false);
	}

	@Override
	protected String getSqlType() {
		return "char";
	}

	@Override
	protected boolean isDefaultValueString() {
		return true;
	}

	@Override
	protected void sql_updateValue(StringBuilder sb, DbObject o) {
		final Object v = DbObject.getFieldValue(o, this);
		sb.append('\'');
		FldStr.escapeSqlString(sb, v.toString());
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
