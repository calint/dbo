package db;

import java.util.Map;

/** String field */
public final class FldStr extends DbField {
	public final static int MAX_SIZE = 65535;

	public FldStr() {
		this(255, null, true);
	}

	public FldStr(int size) {
		this(size, null, true);
	}

	public FldStr(final String def) {
		this(255, def, true);
	}

	public FldStr(final int size, final String def) {
		this(size, def, true);
	}

	public FldStr(final int size, final String def, final boolean allowNull) {
		super(size, def, allowNull);
		if (size > MAX_SIZE) // ? mysql specifc
			throw new RuntimeException("size " + size + " exceeds maximum of " + MAX_SIZE);
	}

	@Override
	protected String getSqlType() {
		return "varchar";
	}

	@Override
	protected boolean isDefaultValueString() {
		return true;
	}

	@Override
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {
		final String s = o.getStr(this);
		if (s == null) {
			sb.append("null");
			return;
		}
		sb.append('\'');
		sb.ensureCapacity(sb.length() + s.length() + 128); // ? magic number
		escapeSqlString(sb, s);
		sb.append('\'');
	}

//	@Override
//	protected void sql_columnDefinition(final StringBuilder sb) {
//		sb.append(name).append(' ').append(getSqlType()).append("(").append(getSize()).append(")");
//		if (defval != null) {
//			sb.append(" default '");
//			escapeSqlString(sb, defval);
//			sb.append("'");
//		}
//	}

	@Override
	protected void putDefaultValue(final Map<DbField, Object> kvm) {
		if (defVal == null)
			return;

		kvm.put(this, defVal);
	}

//	public static String sqlDefaultStringValue(final String v) {
//		if (v == null)
//			return null;
//		final StringBuilder sb = new StringBuilder(v.length() + 16); // ? magic number
//		sb.append('\'');
//		escapeSqlString(sb, v);
//		sb.append('\'');
//		return sb.toString();
//	}

	// note: from
	// https://stackoverflow.com/questions/1812891/java-escape-string-to-prevent-sql-injection
	public static void escapeSqlString(final StringBuilder sb, final String s) {
		final int len = s.length();
//		sb.ensureCapacity(sb.length() + len + 128); // ? magic number
		for (int i = 0; i < len; ++i) {
			final char ch = s.charAt(i);
			switch (ch) {
			case 0: // Must be escaped for 'mysql'
				sb.append('\\');
				sb.append('0');
				break;
			case '\n': // Must be escaped for logs
				sb.append('\\');
				sb.append('n');
				break;
			case '\r':
				sb.append('\\');
				sb.append('r');
				break;
			case '\\':
				sb.append('\\');
				sb.append('\\');
				break;
			case '\'':
				sb.append('\\');
				sb.append('\'');
				break;
			case '\032': // This gives problems on Win32
				sb.append('\\');
				sb.append('Z');
				break;
			case '\u00a5':
			case '\u20a9':
				// escape characters interpreted as backslash by mysql
				// fall through
			default:
				sb.append(ch);
			}
		}
	}
}
