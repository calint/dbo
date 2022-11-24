package db;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;

public abstract class DbObject {
	public final static FldId id = new FldId();

	final HashMap<DbField, Object> fieldValues = new HashMap<DbField, Object>();
	HashSet<DbField> dirtyFields;

	private HashSet<DbField> getCreatedDirtyFields() {
		if (dirtyFields == null)
			dirtyFields = new HashSet<DbField>();
		return dirtyFields;
	}

	/** alias for getId() */
	final public int id() {
		return ((Integer) fieldValues.get(id)).intValue();
	}

	final public int getId() {
		return id();
	}

	final public String getStr(final DbField field) {
		return (String) fieldValues.get(field);
	}

	final public int getInt(final DbField field) {
		return ((Integer) fieldValues.get(field)).intValue();
	}

	final public long getLong(final DbField field) {
		return ((Long) fieldValues.get(field)).longValue();
	}

	final public float getFloat(final DbField field) {
		return ((Float) fieldValues.get(field)).floatValue();
	}

	final public double getDbl(final DbField field) {
		return ((Double) fieldValues.get(field)).doubleValue();
	}

	final public boolean getBool(final DbField field) {
		return ((Boolean) fieldValues.get(field)).booleanValue();
	}

	final public Timestamp getTimestamp(final DbField field) {
		return (Timestamp) fieldValues.get(field);
	}

	final public byte[] getBytesArray(final DbField field) {
		return (byte[]) fieldValues.get(field);
	}

	final public Object get(final DbField field) {
		return fieldValues.get(field);
	}

	final public void set(final DbField field, final Object value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	/**
	 * puts and object in the field value map without marking field dirty and
	 * triggering an update. used by user defined DbFields to optimize get/set data
	 * transformations
	 */
	final public void put(final DbField field, final Object value) {
		fieldValues.put(field, value);
	}

	final public void set(final DbField field, final String value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(final DbField field, final int value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(final DbField field, final long value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(final DbField field, final Timestamp value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(final DbField field, final byte[] value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(final DbField field, final float value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(final DbField field, final double value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(final DbField field, final boolean value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	@Override
	public String toString() {
		return new StringBuilder(getClass().getName()).append(" ").append(fieldValues.toString()).toString();
	}
}
