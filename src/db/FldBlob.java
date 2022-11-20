package db;

public final class FldBlob extends DbField {

	@Override
	void sql_updateValue(StringBuilder sb, DbObject o) {
		final byte[] data = o.getBytesArray(this);
		final char[] chars = bytesToHex(data);
//		sb.ensureCapacity(chars.length + 2); //? fix
		sb.append("0x").append(chars);
	}

	@Override
	void sql_createColumn(StringBuilder sb) {
		sb.append(columnName).append(" longblob");
	}

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	private static char[] bytesToHex(byte[] bytes) {
		final char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			final int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return hexChars;
	}
}
