package main;

import db.DbObject;
import db.FldLong;
import db.FldString;

public final class File extends DbObject {
	public final static FldString name = new FldString();
	public final static FldLong size_B = new FldLong();

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
}