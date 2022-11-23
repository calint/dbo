package db;

import java.util.Map;

public abstract class DbField {
	protected String name;
	protected Class<? extends DbObject> cls;
	protected String tableName;

	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {
	}

	protected void sql_columnDefinition(final StringBuilder sb) {
	}

	protected void setDefaultValue(final Map<DbField, Object> kvm) {
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
