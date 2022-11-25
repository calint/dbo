package main;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

import db.DbObject;

public final class TestObj extends DbObject {
	public final static FldSerializable list = new FldSerializable();
	public final static FldChars md5 = new FldChars(32, "");

	@SuppressWarnings("unchecked")
	public List<String> getList() {
		final Object v = get(list);
		if (v == null)
			return null;

		if (v instanceof List<?>) // is it transformed?
			return (List<String>) v;

		// convert from sql representation
		final byte[] ba = getBytesArray(list);
		try {
			final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(ba));
			final List<String> ls = (List<String>) ois.readObject();
			ois.close();
			put(list, ls); // put without marking field dirty
			return ls;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public void setList(final List<String> v) {
		set(list, v);
	}

	public String getMd5() {
		return getStr(md5);
	}

	public void setMd5(final String v) {
		set(md5, v);
	}
}
