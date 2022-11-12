package db;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;

public class DbObject {
	public final static LongField id = new LongField();

	private final HashMap<DbField, Object> fieldValues = new HashMap<>();
	private final HashSet<DbField> dirtyFields = new HashSet<>();

	public DbObject() throws Throwable {
		final DbTransaction t = Db.currentTransaction();
//		final Statement s = t.c.createStatement();
		final Statement s = t.s;
		final StringBuilder sb = new StringBuilder(256);
		final String tablenm = getClass().getName().replace('.', '_');
		sb.append("insert into ").append(tablenm).append(" values()");
		System.out.println(sb.toString());
		s.execute(sb.toString(), Statement.RETURN_GENERATED_KEYS);
		final ResultSet rs = s.getGeneratedKeys();
		if (rs.next()) {
			final long id = rs.getLong(1);
			setId(id);
		} else
			throw new RuntimeException("no generated id");
		rs.close();
//		s.close();
	}

	void setId(long v) {
		fieldValues.put(id, v);
	}

	final public long getId() {
		return ((Long) fieldValues.get(id)).longValue();
	}

	public String getStr(DbField field) {
		return (String) fieldValues.get(field);
	}

	public int getInt(DbField field) {
		return ((Integer) fieldValues.get(field)).intValue();
	}

	public long getLong(DbField field) {
		return ((Long) fieldValues.get(field)).longValue();
	}

	public void set(DbField field, String value) {
		fieldValues.put(field, value);
		dirtyFields.add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	public void set(DbField field, int value) {
		fieldValues.put(field, value);
		dirtyFields.add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	public void set(DbField field, long value) {
		fieldValues.put(field, value);
		dirtyFields.add(field);
		Db.currentTransaction().dirtyObjects.add(this);
	}

	public String toString() {
		return getClass().getName() + fieldValues.toString();
	}

	final public void update() throws Throwable {
		final DbTransaction t = Db.currentTransaction();
		final StringBuilder sb = new StringBuilder(256);
		sb.append("update ").append(getClass().getName().replace('.', '_')).append(" set ");
		for (DbField f : dirtyFields) {
			sb.append(f.dbname).append('=');
			f.sql_appendUpdateValue(sb, this);
			sb.append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append(" where id=").append(getLong(id));
		System.out.println(sb.toString());
//		final Statement s = t.c.createStatement();
		final Statement s = t.s;
		s.execute(sb.toString());
//		s.close();
		t.dirtyObjects.remove(this);
	}

	final public void delete() throws Throwable {
		final DbTransaction t = Db.currentTransaction();
		final StringBuilder sb = new StringBuilder(256);
		sb.append("delete from ").append(getClass().getName().replace('.', '_')).append(" where id=")
				.append(getLong(id));
		System.out.println(sb.toString());
//		final Statement s = t.c.createStatement();
		final Statement s = t.s;
		s.execute(sb.toString());
//		s.close();
		t.dirtyObjects.remove(this);
	}

}