package main;

import java.text.SimpleDateFormat;

import db.DbField;
import db.DbObject;
import db.FldString;

public class FldDate extends DbField {
	@Override
	protected void sql_columnDefinition(StringBuilder sb) {
		sb.append(getName()).append(" datetime");
	}

	@Override
	protected void sql_updateValue(StringBuilder sb, DbObject o) {
		final Object v = o.get(this);
		if (v == null) {
			sb.append("null");
			return;
		}
		sb.append('\'');
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		final String s = sdf.format(v);
		FldString.sqlEscapeString(sb, s);
		sb.append('\'');
	}

}
