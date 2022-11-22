package db;

import java.util.Map;

public abstract class DbField {
//	Class<? extends DbObject> cls;
	String name;
	String tableName;

	protected void sql_updateValue(StringBuilder sb, DbObject o) {
	}

	protected void sql_columnDefinition(StringBuilder sb) {
	}

	protected void setDefaultValue(Map<DbField, Object> kvm) {
	}

	public final String getName() {
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
