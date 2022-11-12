package db.main;

import db.DbObject;
import db.FldString;

public final class File extends DbObject {
	public final static FldString name = new FldString();

	public String getName() {
		return getStr(name);
	}

	public void setName(String v) {
		set(name, v);
	}
}