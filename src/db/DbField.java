package db;

import java.util.Map;

public abstract class DbField {
	String name;
	Class<? extends DbObject> cls;
	String tableName;

	public final String getName() {
		return name;
	}

	protected abstract String getSqlType();

	protected abstract void sql_columnDefinition(final StringBuilder sb);

	protected abstract void sql_updateValue(final StringBuilder sb, final DbObject o);

	protected abstract void setDefaultValue(final Map<DbField, Object> kvm);

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}
}
