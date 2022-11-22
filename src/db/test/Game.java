package db.test;

import db.DbObject;
import db.FldString;

public final class Game extends DbObject {
	public final static FldString name = new FldString();
	public final static FldString description = new FldString(8000);

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getName() {
		return getStr(name);
	}

	public void setName(final String v) {
		set(name, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getDescription() {
		return getStr(description);
	}

	public void setDescription(final String v) {
		set(description, v);
	}

}