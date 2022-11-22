package main;

import java.sql.Timestamp;
import java.util.Date;

import db.DbObject;

public class TestObj extends DbObject {
	public final static FldDate df = new FldDate();

	public Date getDf() {
		final Timestamp ts = (Timestamp) get(df);
		return new Date(ts.getTime());
	}

	public void setDf(Date v) {
		set(df, v);
	}
}
