package db;

public final class FldBlob extends DbField {

	@Override
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {
		final byte[] data = o.getBytesArray(this);
//		final char[] chars = bytesToHex(data);
//		sb.append("0x").append(chars);
		final StringBuilder hexed = new StringBuilder(data.length * 2);
		appendHexedBytesToStringBuilder(hexed, data);
		sb.append("0x");
		sb.append(hexed);
	}

	@Override
	protected void sql_columnDefinition(final StringBuilder sb) {
		sb.append(name).append(" longblob");
	}

	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	public static void appendHexedBytesToStringBuilder(final StringBuilder sb, final byte[] bytes) {
		final char[] hex = new char[2];
		for (int i = 0; i < bytes.length; i++) {
			final int v = bytes[i] & 0xFF;
			hex[0] = HEX_ARRAY[v >>> 4];
			hex[1] = HEX_ARRAY[v & 0x0F];
			sb.append(hex);
		}
	}

	public static char[] bytesToHex(final byte[] bytes) {
		final char[] hexChars = new char[bytes.length * 2];
		for (int i = 0; i < bytes.length; i++) {
			final int v = bytes[i] & 0xFF;
			hexChars[i * 2] = HEX_ARRAY[v >>> 4];
			hexChars[i * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return hexChars;
	}
}
