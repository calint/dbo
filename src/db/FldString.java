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

	@Override
	void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append('\'').append(o.getStr(this).replace("'", "''")).append('\'');
	}

	@Override
	void sql_createField(StringBuilder sb) {
		sb.append(columnName).append(" varchar(").append(size).append(")");
		if (defval != null) {
			sb.append(" default '").append(defval.replace("'", "''")).append("'");
		}
	}

	@Override
	void initDefaultValue(Map<DbField, Object> kvm) {
		kvm.put(this, defval);
	}

}