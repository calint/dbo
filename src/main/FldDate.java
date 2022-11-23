package main;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		final Date d = (Date) o.getTemp(this, "d");
		if (d == null) {
			sb.append("null");
			return;
		}
		final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); // ? optimize with other thread safe formatter
		final String s = sdf.format(d);
		final Timestamp ts = Timestamp.valueOf(s);
		sb.append('\'');
		FldStr.escapeSqlString(sb, s);
		sb.append('\'');
	}

}
