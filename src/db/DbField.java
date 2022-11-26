package db;

import java.util.Map;

public abstract class DbField {
	String name;
	Class<? extends DbObject> cls;
	String tableName;
	final int size;
	final String defVal;
	final boolean allowNull;

	/**
	 * @param size      where applicable (varchar, char, bit)
	 * @param defVal    default value as returned by COLUMN_DEF from
	 *                  DatabaseMetaData.getColumns(...).
	 * @param allowNull true if null is allowed.
	 */
	protected DbField(final int size, final String sqlDefVal, final boolean allowNull) {
		this.size = size;
		this.defVal = sqlDefVal;
		this.allowNull = allowNull;
	}

	protected DbField() {
		this(0, null, true);
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

	public final String getDefaultValue() {
		return defVal;
	}

	/**
	 * @return true if default value is a string and should be quoted and escaped
	 *         when defining column.
	 */
	protected boolean isDefaultValueString() {
		return false;
	}

	/** Called by DbClass when asserting that column type matches DbField type. */
	protected abstract String getSqlType();

	/** Called by DbClass at column creation and move column. */
	protected void sql_columnDefinition(final StringBuilder sb) {
		sb.append(name).append(' ').append(getSqlType());
		if (size != 0)
			sb.append("(").append(getSize()).append(")");
		if (defVal != null) {
			sb.append(" default ");
			if (isDefaultValueString()) {
				sb.append('\'');
				FldStr.escapeSqlString(sb, defVal);
				sb.append('\'');
			} else {
				sb.append(defVal);
			}
		}
		if (!allowNull) {
			sb.append(" not null");
		}
	}

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
