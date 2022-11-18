package main;

import db.DbObject;
import db.FldClob;

public final class DataText extends DbObject {
	public final static FldClob data = new FldClob();

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getData() {
		return getStr(data);
	}

	public void setData(String v) {
		set(data, v);
	}
}