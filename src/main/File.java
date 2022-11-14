package main;

import java.sql.Timestamp;

import db.DbObject;
import db.FldLong;
import db.FldString;
import db.FldTimestamp;

public final class File extends DbObject {
	public final static FldString name = new FldString();
	public final static FldLong size_B = new FldLong();
	public final static FldTimestamp created_ts = new FldTimestamp();
	
	public String getName() {
		return getStr(name);
	}

	public void setName(String v) {
		set(name, v);
	}

	public long getSizeB() {
		return getLong(size_B);
	}

	public void setSizeB(long v) {
		set(size_B, v);
	}

	public Timestamp getCreatedTs() {
		return getTimestamp(created_ts);
	}

	public void setCreatedTs(Timestamp v) {
		set(created_ts, v);
	}

}