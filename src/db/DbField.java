package db;

import java.util.Map;

public abstract class DbField {
	String name;
	Class<? extends DbObject> cls;
	String tableName;

	public final String getName() {
		return name;
	}

	/** Called by DbClass when asserting that column type matches DbField type. */
	protected abstract String getSqlType();

	/** Called by DbClass at column creation and move. */
	protected abstract void sql_columnDefinition(final StringBuilder sb);

	/** Called by DbTransaction at object update to Db. */
	protected abstract void sql_updateValue(final StringBuilder sb, final DbObject o);

	/** Called by DbTransaction at object creation. */
	protected abstract void putDefaultValue(final Map<DbField, Object> kvm);

	@Override
	public final int hashCode() {
		return name.hashCode();
	}

	@Override
	public String toString() {
		return name;
	}
}
