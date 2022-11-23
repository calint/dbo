package main;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.util.List;

import db.DbField;
import db.DbObject;
import db.FldBlob;

public class FldSerList extends DbField {
	@Override
	protected void sql_columnDefinition(StringBuilder sb) {
		sb.append(getName()).append(" blob");
	}

	@Override
	protected void sql_updateValue(StringBuilder sb, DbObject o) {
		final Object v = o.get(this);
		if (v == null) {
			sb.append("null");
			return;
		}
		// if the value has changed it was through setList so it is a List
		@SuppressWarnings("unchecked")
		final List<String> ls = (List<String>) v;
		try {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream(256);
			final ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(ls);
			oos.close();
			final byte[] ba = bos.toByteArray();
			final char[] chars = FldBlob.bytesToHex(ba);
			sb.append("0x").append(chars);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}
