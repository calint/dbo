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
		escapeString(sb, o.getStr(this));
		sb.append('\'');
	}

	@Override
	void sql_createColumn(StringBuilder sb) {
		sb.append(columnName).append(" varchar(").append(size).append(")");
		if (defval != null) {
			sb.append(" default '");
			escapeString(sb, defval);
			sb.append("'");
		}
	}

	@Override
	void initDefaultValue(Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}

	static void escapeString(final StringBuilder sb, final String s) {
		sb.append(s.replace("'", "''").replace("\\", "\\\\")); // ? make better escape
	}
}