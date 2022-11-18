package main;

import db.DbObject;
import db.FldClob;
import db.FldString;

public final class Book extends DbObject {
	public final static FldString name = new FldString();
	public final static FldClob description = new FldClob();

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getName() {
		return getStr(name);
	}

	public void setName(String v) {
		set(name, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getDescription() {
		return getStr(description);
	}

	public void setDescription(String v) {
		set(description, v);
	}
}