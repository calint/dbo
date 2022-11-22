package db;

import java.util.Map;

public abstract class DbField {
//	Class<? extends DbObject> cls;
	String name;
	String tableName;

	void sql_updateValue(StringBuilder sb, DbObject o) {
	}

	void sql_columnName(StringBuilder sb) {
		sb.append(name);
	}

	void sql_createColumn(StringBuilder sb) {
	}

	void putDefaultValue(Map<DbField, Object> kvm) {
	}

	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}
}
