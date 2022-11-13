package db;

import java.util.Map;

abstract class DbField {
	Class<? extends DbObject> cls;
	String dbname;

	void sql_updateValue(StringBuilder sb, DbObject o) {
	}

	void sql_fieldName(StringBuilder sb) {
		sb.append(dbname);
	}

	void sql_createField(StringBuilder sb) {
	}

	void initDefaultValue(Map<DbField, Object> kvm) {
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