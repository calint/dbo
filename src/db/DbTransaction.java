package db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class DbTransaction {
	final Connection con;
	final Statement stmt;
	final HashSet<DbObject> dirtyObjects = new HashSet<>();

	DbTransaction(final Connection c) throws Throwable {
		this.con = c;
		this.stmt = c.createStatement();
	}

	public DbObject create(final Class<? extends DbObject> cls) throws Throwable {
		final DbObject o = cls.getConstructor().newInstance();
		o.createInDb();
		return o;
	}

	public List<DbObject> get(final Class<? extends DbObject> cls) {
		final ArrayList<DbObject> ls = new ArrayList<>();
		final StringBuilder sb = new StringBuilder(256);
		final DbClass dbcls = Db.instance().dbClassForJavaClass(cls);
		sb.append("select * from ").append(dbcls.tableName);
		final String sql = sb.toString();
		System.out.println(sql);
		try {
			final ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				final DbObject o = cls.getConstructor().newInstance();
				o.readResultSet(dbcls, rs);
				ls.add(o);
			}
		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
		return ls;
	}

//	public void delete(final DbObject o) throws Throwable {
//		o.delete();
//	}

	public void commit() throws Throwable {
		flush();
		stmt.close();
		con.commit();
	}

	public void flush() throws Throwable {
		for (final DbObject o : dirtyObjects) {
			o.updateDb();
		}
		dirtyObjects.clear();
	}

	public void rollback() throws Throwable {
		stmt.close();
		con.rollback();
	}

	public String toString() {
		return "dirtyObjects=" + dirtyObjects;
	}
}