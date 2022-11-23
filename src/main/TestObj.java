package main;

import java.sql.Timestamp;
import java.util.Date;

import db.DbObject;

public class TestObj extends DbObject {
	public final static FldDate df = new FldDate();

	public Date getDf() {
		Date d = null;
		if (hasTemp(df, "d")) { // cached object may be null. is converted and cached?
			d = (Date) getTemp(df, "d"); // yes, get from cache
		} else { // not converted. convert and cache
			final Timestamp ts = (Timestamp) get(df); // get sql representation
			if (ts == null) {
				setTemp(df, "d", null); // set null value
			} else {
				d = new Date(ts.getTime()); // convert (this case simple but it may be costly)
				setTemp(df, "d", d); // cache
			}
		}
		return d;
	}

	public void setDf(Date v) {
		setTemp(df, "d", v); // set cached
	}
}
