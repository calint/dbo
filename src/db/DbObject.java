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

	final public String getStr(DbField field) {
		return (String) fieldValues.get(field);
	}

	final public int getInt(DbField field) {
		return ((Integer) fieldValues.get(field)).intValue();
	}

	final public long getLong(DbField field) {
		return ((Long) fieldValues.get(field)).longValue();
	}

	final public float getFloat(DbField field) {
		return ((Float) fieldValues.get(field)).floatValue();
	}

	final public double getDbl(DbField field) {
		return ((Double) fieldValues.get(field)).doubleValue();
	}

	final public boolean getBool(DbField field) {
		return ((Boolean) fieldValues.get(field)).booleanValue();
	}

	final public Timestamp getTimestamp(DbField field) {
		return (Timestamp) fieldValues.get(field);
	}

	final public byte[] getBytesArray(DbField field) {
		return (byte[]) fieldValues.get(field);
	}

	final public Object get(DbField field) {
		return fieldValues.get(field);
	}

	final public void set(DbField field, Object value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	/** used by user defined DbFields to optimize get/set data transformations */
	final public void set(DbField field, Object value, boolean markDirty) {
		fieldValues.put(field, value);
		if (markDirty) {
			getCreatedDirtyFields().add(field);
			Db.currentTransaction().dirtyObjects.add(this);
		}
	}

	final public void set(DbField field, String value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, int value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, long value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, Timestamp value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, byte[] value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, float value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, double value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, boolean value) {
		fieldValues.put(field, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	@Override
	public String toString() {
		return new StringBuilder(getClass().getName()).append(" ").append(fieldValues.toString()).toString();
	}
}
