package main;

import java.text.SimpleDateFormat;

import db.DbField;
import db.DbObject;
import db.FldStr;

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
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); // ? optimize with other thread safe formatter
		final String s = sdf.format(v);
		FldStr.escapeSqlString(sb, s);
		sb.append('\'');
	}

}
