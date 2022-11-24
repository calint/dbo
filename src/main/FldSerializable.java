package main;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import db.DbField;
import db.DbObject;
import db.FldBlob;

public class FldSerializable extends DbField {
	@Override
	protected void sql_columnDefinition(final StringBuilder sb) {
		sb.append(getName()).append(" blob");
	}

	@Override
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {
		final Object v = o.get(this);
		if (v == null) {
			sb.append("null");
			return;
		}
		if (!(v instanceof Serializable))
			throw new RuntimeException("expected serializable object. " + o);

		// if the value has changed then it is kept in java type which is serializable
		final Serializable so = (Serializable) v;
		try {
			final ByteArrayOutputStream bos = new ByteArrayOutputStream(256);
			final ObjectOutputStream oos = new ObjectOutputStream(bos);
			oos.writeObject(so);
			oos.close();
			final byte[] ba = bos.toByteArray();
			final char[] chars = FldBlob.bytesToHex(ba);
			sb.append("0x").append(chars);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}
