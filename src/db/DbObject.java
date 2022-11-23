package db;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;

public abstract class DbObject {
	public final static FldId id = new FldId();

	final HashMap<String, Object> fieldValues = new HashMap<String, Object>();
	HashSet<DbField> dirtyFields;

	private HashSet<DbField> getCreatedDirtyFields() {
		if (dirtyFields == null)
			dirtyFields = new HashSet<DbField>();
		return dirtyFields;
	}

	/** alias for getId() */
	final public int id() {
		return ((Integer) fieldValues.get(id.name)).intValue();
	}

	final public int getId() {
		return id();
	}

	final public String getStr(DbField field) {
		return (String) fieldValues.get(field.name);
	}

	final public int getInt(DbField field) {
		return ((Integer) fieldValues.get(field.name)).intValue();
	}

	final public long getLong(DbField field) {
		return ((Long) fieldValues.get(field.name)).longValue();
	}

	final public float getFloat(DbField field) {
		return ((Float) fieldValues.get(field.name)).floatValue();
	}

	final public double getDbl(DbField field) {
		return ((Double) fieldValues.get(field.name)).doubleValue();
	}

	final public boolean getBool(DbField field) {
		return ((Boolean) fieldValues.get(field.name)).booleanValue();
	}

	final public Timestamp getTimestamp(DbField field) {
		return (Timestamp) fieldValues.get(field.name);
	}

	final public byte[] getBytesArray(DbField field) {
		return (byte[]) fieldValues.get(field.name);
	}

	final public Object get(DbField field) {
		return fieldValues.get(field.name);
	}

	private String subfieldnm(DbField field, String subfieldName) {
		final StringBuilder sb = new StringBuilder(64);
		sb.append('$').append(field.name).append('$').append(subfieldName);
		final String fn = sb.toString();
		return fn;
	}

	/** used by custom DbFields for optimizing transformations between sql and java representations */
	final public Object getTemp(DbField field, String subfieldName) {
		final String fn = subfieldnm(field, subfieldName);
		return fieldValues.get(fn);
	}

	/** used by custom DbFields for optimizing transformations between sql and java representations */
	final public boolean hasTemp(DbField field, String subfieldName) {
		final String fn = subfieldnm(field, subfieldName);
		return fieldValues.containsKey(fn);
	}

	/** used by custom DbFields for optimizing transformations between sql and java representations */
	final public void setTemp(DbField field, String subfieldName, Object v) {
		final String fn = subfieldnm(field, subfieldName);
		fieldValues.put(fn, v);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, Object value) {
		fieldValues.put(field.name, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, String value) {
		fieldValues.put(field.name, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, int value) {
		fieldValues.put(field.name, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, long value) {
		fieldValues.put(field.name, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, Timestamp value) {
		fieldValues.put(field.name, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, byte[] value) {
		fieldValues.put(field.name, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, float value) {
		fieldValues.put(field.name, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, double value) {
		fieldValues.put(field.name, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, boolean value) {
		fieldValues.put(field.name, value);
		getCreatedDirtyFields().add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	@Override
	public String toString() {
		return new StringBuilder(getClass().getName()).append(" ").append(fieldValues.toString()).toString();
	}
}
