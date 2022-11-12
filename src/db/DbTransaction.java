package db;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashSet;

final public class DbTransaction {
	final Connection c;
	final Statement stmt;
	final HashSet<DbObject> dirtyObjects = new HashSet<>();

	DbTransaction(final Connection c) throws Throwable {
		this.c = c;
		this.stmt = c.createStatement();
	}

	public DbObject create(final Class<? extends DbObject> cls) throws Throwable {
		final DbObject o=cls.getConstructor().newInstance();
		o.createInDb();
		return o;
	}

//	public void delete(final DbObject o) throws Throwable {
//		o.delete();
//	}

	public void commit() throws Throwable {
		for (DbObject o : dirtyObjects) {
			o.updateDb();
		}
		dirtyObjects.clear();
		stmt.close();
		c.commit();
	}

	public void rollback() throws Throwable {
		stmt.close();
		c.rollback();
	}

	public String toString() {
		return "dirtyObjects=" + dirtyObjects;
	}
}