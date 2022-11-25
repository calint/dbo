package main;

import java.util.Map;

import db.DbField;
import db.DbObject;
import db.FldStr;

public class FldChars extends DbField {
	private final int size;
	private final String def;

	public FldChars(final int size, final String def) {
		this.size = size;
		this.def = def;
	}

	@Override
	protected String getSqlType() {
		return "char";
	}

	@Override
	protected void sql_columnDefinition(StringBuilder sb) {
		sb.append(getName()).append(' ').append(getSqlType()).append('(').append(size).append(')');
		if (def == null)
			return;
		sb.append(" default ");
		sb.append('\'');
		FldStr.escapeSqlString(sb, def);
		sb.append('\'');
	}

	@Override
	protected void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append('\'');
		FldStr.escapeSqlString(sb, o.getStr(this));
		sb.append('\'');
	}

	@Override
	protected void putDefaultValue(Map<DbField, Object> kvm) {
		kvm.put(this, def);
	}

}
