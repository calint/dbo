package db;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.HashSet;

public abstract class DbObject {
	public final static FldId id = new FldId();

	final HashMap<DbField, Object> fieldValues = new HashMap<DbField, Object>();
	private HashSet<DbField> dirtyFields;

	private void ensureDirtyFieldsIsCreated() {
		if (dirtyFields != null)
			return;
		dirtyFields = new HashSet<DbField>();
	}

	final void createInDb() throws Throwable {// ? move code to DbTransaction?
		final DbTransaction t = Db.currentTransaction();
		final Statement s = t.stmt;
		final StringBuilder sbSql = new StringBuilder(256);
		sbSql.append("insert into ").append(Db.tableNameForJavaClass(getClass()));
		final StringBuilder sbFields = new StringBuilder(256);
		final StringBuilder sbValues = new StringBuilder(256);
		if (dirtyFields != null) {
			for (final DbField f : dirtyFields) {
				f.sql_columnName(sbFields);
				sbFields.append(',');
				f.sql_updateValue(sbValues, this);
				sbValues.append(',');
			}
			dirtyFields.clear();
		}
		if (sbFields.length() > 0) {
			sbSql.append('(');
			sbFields.setLength(sbFields.length() - 1);
			sbSql.append(sbFields);
			sbValues.setLength(sbValues.length() - 1);
			sbSql.append(") values(").append(sbValues).append(")");
		} else
			sbSql.append(" values()");

		final String sql = sbSql.toString();
		Db.log(sql);
		s.execute(sql, Statement.RETURN_GENERATED_KEYS);
		final ResultSet rs = s.getGeneratedKeys();
		if (rs.next()) {
			final int id = rs.getInt(1);
			setId(id);
		} else
			throw new RuntimeException("no generated id");
		rs.close();

		// init default values
		final DbClass dbcls = Db.instance().dbClassForJavaClass(getClass());
		for (final DbField f : dbcls.allFields) {// ? allFields
			f.putDefaultValue(fieldValues);
		}
	}

	final void updateDb() throws Throwable {// ? move code to DbTransaction?
		final DbTransaction t = Db.currentTransaction();
		final StringBuilder sb = new StringBuilder(256);
		sb.append("update ").append(Db.tableNameForJavaClass(getClass())).append(" set ");
		for (final DbField f : dirtyFields) {
			sb.append(f.columnName).append('=');
			f.sql_updateValue(sb, this);
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append(" where id=").append(id());
		final String sql = sb.toString();
		Db.log(sql);
		t.stmt.execute(sql);
		dirtyFields.clear();
	}

	final void deleteFromDb() {// ? move code to DbTransaction?
		final DbClass dbcls = Db.instance().dbClassForJavaClass(getClass());
		for (final DbRelation r : dbcls.allRelations) {
			r.cascadeDelete(this);
		}
		final StringBuilder sb = new StringBuilder(256);
		sb.append("delete from ").append(dbcls.tableName).append(" where id=").append(id());
		final String sql = sb.toString();
		Db.log(sql);
		final DbTransaction tn = Db.currentTransaction();
		try {
			tn.stmt.execute(sql);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		tn.dirtyObjects.remove(this);
		if (tn.cache_enabled)
			tn.cache.remove(this);
	}

	final void setId(int v) {
		fieldValues.put(id, v);
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

	final public double getDouble(DbField field) {
		return ((Double) fieldValues.get(field)).doubleValue();
	}

	final public boolean getBoolean(DbField field) {
		return ((Boolean) fieldValues.get(field)).booleanValue();
	}

	final public Timestamp getTimestamp(DbField field) {
		return (Timestamp) fieldValues.get(field);
	}

	final public byte[] getBytesArray(DbField field) {
		return (byte[]) fieldValues.get(field);
	}

	final public void set(DbField field, String value) {
		fieldValues.put(field, value);
		ensureDirtyFieldsIsCreated();
		dirtyFields.add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, int value) {
		fieldValues.put(field, value);
		ensureDirtyFieldsIsCreated();
		dirtyFields.add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, long value) {
		fieldValues.put(field, value);
		ensureDirtyFieldsIsCreated();
		dirtyFields.add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, Timestamp value) {
		fieldValues.put(field, value);
		ensureDirtyFieldsIsCreated();
		dirtyFields.add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, byte[] value) {
		fieldValues.put(field, value);
		ensureDirtyFieldsIsCreated();
		dirtyFields.add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, float value) {
		fieldValues.put(field, value);
		ensureDirtyFieldsIsCreated();
		dirtyFields.add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, double value) {
		fieldValues.put(field, value);
		ensureDirtyFieldsIsCreated();
		dirtyFields.add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, boolean value) {
		fieldValues.put(field, value);
		ensureDirtyFieldsIsCreated();
		dirtyFields.add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final void readResultSet(DbClass cls, ResultSet rs) throws Throwable {// ? move code to DbTransaction?
		int i = 1;
		for (final DbField f : cls.allFields) {
			final Object v = rs.getObject(i);
			fieldValues.put(f, v);
			i++;
		}
	}

	@Override
	public String toString() {
		return new StringBuilder(getClass().getName()).append(" ").append(fieldValues.toString()).toString();
	}

}
