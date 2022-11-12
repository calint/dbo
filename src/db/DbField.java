package db;

class DbField {
	String dbname;

	void sql_appendUpdateValue(StringBuilder sb, DbObject o) {
	}

	void sql_appendFieldName(StringBuilder sb) {
		sb.append(dbname);
	}

	public void sql_createField(StringBuilder sb) {
	}

	@Override
	public int hashCode() {
		return dbname.hashCode();
	}

	@Override
	public String toString() {
		return dbname;
	}
}