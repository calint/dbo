package db.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Timestamp;

import db.DbObject;
import db.FldLong;
import db.FldStr;
import db.FldTimestamp;
import db.RelAgg;

public final class File extends DbObject {
	public final static FldStr name = new FldStr();
	public final static FldLong size_B = new FldLong();
	public final static FldTimestamp created_ts = new FldTimestamp();
	public final static RelAgg data = new RelAgg(DataBinary.class);

	public void loadFile(final String path) throws Throwable {
		final DataBinary d = getData(true);
		final java.io.File f = new java.io.File(path);
		if (!f.exists())
			throw new RuntimeException("file '" + path + "' not found");
		final long len = f.length();
		setSizeB(len);
		// ? does not handle files bigger than 4G
		final byte[] ba = new byte[(int) len];
		final FileInputStream fis = new FileInputStream(f);
		fis.read(ba);
		fis.close();
		d.setData(ba);
	}

	public void writeFile(final String path) throws Throwable {
		final DataBinary d = getData(false);
		if (d == null)
			return;
		final java.io.File f = new java.io.File(path);
		// ? does not handle files bigger than 4G
		final byte[] ba = d.getData();
		final FileOutputStream fos = new FileOutputStream(f);
		fos.write(ba);
		fos.close();
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getName() {
		return getStr(name);
	}

	public void setName(String v) {
		set(name, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public long getSizeB() {
		return getLong(size_B);
	}

	public void setSizeB(long v) {
		set(size_B, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public Timestamp getCreatedTs() {
		return getTimestamp(created_ts);
	}

	public void setCreatedTs(Timestamp v) {
		set(created_ts, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public DataBinary getData(final boolean createIfNone) {
		return (DataBinary) data.get(this, createIfNone);
	}

	public void deleteData() {
		data.delete(this);
	}

}