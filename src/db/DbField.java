package db;

import java.util.Map;

abstract class DbField {
	Class<? extends DbObject> cls;
	String tableName;
	String columnName;

	void sql_updateValue(StringBuilder sb, DbObject o) {
	}

	void sql_fieldName(StringBuilder sb) {
		sb.append(columnName);
	}

	void sql_createField(StringBuilder sb) {
	}

	void initDefaultValue(Map<DbField, Object> kvm) {
	}

	@Override
	public int hashCode() {
		return columnName.hashCode();
	}

	@Override
	public String toString() {
		return columnName;
	}
}