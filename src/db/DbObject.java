package db;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

public abstract class DbObject {
	public final static FldId id = new FldId();

	private final HashMap<DbField, Object> fieldValues = new HashMap<DbField, Object>();
	private final HashSet<DbField> dirtyFields = new HashSet<DbField>();

	final void createInDb() throws Throwable {
		final DbTransaction t = Db.currentTransaction();
		final Statement s = t.stmt;
		final StringBuilder sbSql = new StringBuilder(256);
		sbSql.append("insert into ").append(Db.tableNameForJavaClass(getClass()));
		final StringBuilder sbFields = new StringBuilder(256);
		final StringBuilder sbValues = new StringBuilder(256);
		for (final DbField f : dirtyFields) {
			f.sql_columnName(sbFields);
			sbFields.append(',');
			f.sql_updateValue(sbValues, this);
			sbValues.append(',');
		}
		dirtyFields.clear();
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
			final long id = rs.getLong(1);
			setId(id);
		} else
			throw new RuntimeException("no generated id");
		rs.close();

		// init default values
		final DbClass dbcls = Db.instance().dbClassForJavaClass(getClass());
		for (final DbField f : dbcls.declaredFields) {
			f.initDefaultValue(fieldValues);
		}
	}

	final public void updateDb() throws Throwable {
		if (dirtyFields.isEmpty()) // ? fishy, when relation field changes object updates db but is not removed
									// from dirty objects list
			return;

		final DbTransaction t = Db.currentTransaction();
		final StringBuilder sb = new StringBuilder(256);
		sb.append("update ").append(Db.tableNameForJavaClass(getClass())).append(" set ");
		for (final DbField f : dirtyFields) {
			sb.append(f.columnName).append('=');
			f.sql_updateValue(sb, this);
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append(" where id=").append(getId());
		final String sql = sb.toString();
		Db.log(sql);
		t.stmt.execute(sql);
		dirtyFields.clear();
	}

	final public void deleteFromDb() throws Throwable {
		final StringBuilder sb = new StringBuilder(256);
		sb.append("delete from ").append(Db.tableNameForJavaClass(getClass())).append(" where id=").append(getId());
		final String sql = sb.toString();
		Db.log(sql);
		final DbTransaction t = Db.currentTransaction();
		t.stmt.execute(sql);
		t.dirtyObjects.remove(this);
	}

	final void setId(long v) {
		fieldValues.put(id, v);
	}

	final public long getId() {
		return ((Long) fieldValues.get(id)).longValue();
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

	final public void set(DbField field, String value) {
		fieldValues.put(field, value);
		dirtyFields.add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, int value) {
		fieldValues.put(field, value);
		dirtyFields.add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	final public void set(DbField field, long value) {
		fieldValues.put(field, value);
		dirtyFields.add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	public String toString() {
		return new StringBuilder(getClass().getName()).append(" ").append(fieldValues.toString()).toString();
	}

	void readResultSet(DbClass cls, ResultSet rs) throws Throwable {
		int i = 1;
		for (final DbField f : cls.allFields) {
			final Object v = rs.getObject(i);
			fieldValues.put(f, v);
			i++;
		}
	}

}