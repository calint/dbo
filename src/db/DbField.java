package db;

import java.util.Map;

public abstract class DbField {
	String name;
	Class<? extends DbObject> cls;
	String tableName;
	final int size;

	/** @param size where applicable (varchar and char) */
	protected DbField(final int size) {
		this.size = size;
	}

	protected DbField() {
		this(0);
	}

	/** @return Size of field if applicable (varchar and char) or 0 */
	public final int getSize() {
		return size;
	}

	/**
	 * @return the field name in the java class where index was created
	 */
	public final String getName() {
		return name;
	}

	/** Called by DbClass when asserting that column type matches DbField type. */
	protected abstract String getSqlType();

	/** Called by DbClass at column creation and move column. */
	protected abstract void sql_columnDefinition(final StringBuilder sb);

	/** Called by DbTransaction at update database from object. */
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
