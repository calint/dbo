package main;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.util.List;

import db.DbObject;

public class TestObj extends DbObject {
	public final static FldSerList list = new FldSerList();

	@SuppressWarnings("unchecked")
	public List<String> getList() {
		final Object v = get(list);
		if (v == null)
			return null;
		if (v instanceof List<?>) {
			// it is transformed
			return (List<String>) v;
		}
		final byte[] ba = getBytesArray(list); // get sql representation
		if (ba == null)
			return null;

		try {
			final ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(ba));
			final List<String> ls = (List<String>) ois.readObject();
			set(list, ls, false); // cache
			return ls;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public void setList(List<String> v) {
		set(list, v);
	}
}
