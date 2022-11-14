package db;

import java.util.Map;

public final class FldBlob extends DbField {

	public FldBlob() {
	}

	@Override
	void sql_updateValue(StringBuilder sb, DbObject o) {
		final byte[] data = o.getBytesArray(this);
		final char[] chars = bytesToHex(data);
		sb.append("0x").append(chars);
	}

	@Override
	void sql_createColumn(StringBuilder sb) {
		sb.append(columnName).append(" longblob");
	}

	@Override
	void initDefaultValue(Map<DbField, Object> kvm) {
	}

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	static char[] bytesToHex(byte[] bytes) {
		final char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			final int v = bytes[j] & 0xFF;
			hexChars[j * 2    ] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return hexChars;
	}
}