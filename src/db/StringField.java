package db;

public final class StringField extends DbField {
	public final static int max_size = 65535;
	private int size = 255;

	public StringField() {
	}

	public StringField(int size) {
		if (size > max_size)
			throw new RuntimeException("size " + size + " exceeds maximum of " + max_size);
		this.size = size;
	}

	@Override
	void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append('\'').append(o.getStr(this).replace("'", "''")).append('\'');
	}

	@Override
	void sql_createField(StringBuilder sb) {
		sb.append(dbname).append(" varchar(").append(size).append(")");
	}
}