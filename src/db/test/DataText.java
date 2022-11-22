package db.test;

import db.DbObject;
import db.FldClob;
import db.IndexFt;

public final class DataText extends DbObject {
	public final static FldClob meta = new FldClob();
	public final static FldClob data = new FldClob();

	public final static IndexFt ft = new IndexFt(meta, data);

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getData() {
		return getStr(data);
	}

	public void setData(String v) {
		set(data, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getMeta() {
		return getStr(meta);
	}

	public void setMeta(String v) {
		set(meta, v);
	}
}