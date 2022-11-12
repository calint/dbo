package db.main;

import db.DbObject;
import db.StringField;

public final class File extends DbObject {
	public final static StringField name = new StringField();

	public String getName() {
		return getStr(name);
	}

	public void setName(String v) {
		set(name, v);
	}
}