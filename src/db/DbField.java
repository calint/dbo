package db;

import java.util.Map;

public abstract class DbField {
//	Class<? extends DbObject> cls;
	String tableName;
	String columnName;

	void sql_updateValue(StringBuilder sb, DbObject o) {
	}

	void sql_columnName(StringBuilder sb) {
		sb.append(columnName);
	}

	void sql_createColumn(StringBuilder sb) {
	}

	void putDefaultValue(Map<DbField, Object> kvm) {
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
