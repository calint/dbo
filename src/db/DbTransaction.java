package db;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashSet;

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