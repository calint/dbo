package main;

import db.DbObject;
import db.FldBlob;

public final class Data extends DbObject {
	public final static FldBlob data = new FldBlob();

	public byte[] getData() {
		return getBytesArray(data);
	}

	public void setData(byte[] v) {
		set(data, v);
	}
}