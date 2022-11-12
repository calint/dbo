package db;

class DbField {
	String dbname;

	void sql_appendUpdateValue(StringBuilder sb, DbObject o) {

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