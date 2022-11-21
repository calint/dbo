package db;

import java.util.Map;

public final class FldString extends DbField {
	public final static int max_size = 65535;
	private int size = 255;
	private String defval = "";

	public FldString() {
		this("", 255);
	}

	public FldString(final String def) {
		this(def, 255);
	}

	public FldString(int size) {
		this("", size);
	}

	public FldString(final String def, final int size) {
		if (size > max_size)
			throw new RuntimeException("size " + size + " exceeds maximum of " + max_size);
		this.size = size;
		this.defval = def;
	}

	public int getSize() {
		return size;
	}

	@Override
	void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append('\'');
		sqlEscapeString(sb, o.getStr(this));
		sb.append('\'');
	}

	@Override
	void sql_createColumn(StringBuilder sb) {
		sb.append(name).append(" varchar(").append(size).append(")");
		if (defval != null) {
			sb.append(" default '");
			sqlEscapeString(sb, defval);
			sb.append("'");
		}
	}

	@Override
	void putDefaultValue(Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}

//	static void escapeString(final StringBuilder sb, final String s) {
//		sb.append(s.replace("'", "''").replace("\\", "\\\\").replace("\0", "\\0")); // ? make better escape
//	}

	// note: from
	// https://stackoverflow.com/questions/1812891/java-escape-string-to-prevent-sql-injection
	static void sqlEscapeString(final StringBuilder sb, final String x) {
		final int len = x.length();
		for (int i = 0; i < len; ++i) {
			final char ch = x.charAt(i);
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
