package db;

import java.sql.Connection;
import java.sql.Statement;
import java.util.HashSet;

final public class DbTransaction {
	final Connection c;
	final Statement s;
	final HashSet<DbObject> dirtyObjects = new HashSet<>();

	DbTransaction(final Connection c) throws Throwable {
		this.c = c;
		this.s = c.createStatement();
	}

//	public DbObject create(final Class<? extends DbObject> cls) throws Throwable {
//		DbObject o;
//		try {
//			o = cls.getConstructor().newInstance();
//		} catch (Throwable t) {
//			throw new RuntimeException(t);
//		}
////		final Statement s = c.createStatement();
//		final StringBuilder sb = new StringBuilder(256);
//		final String tablenm = o.getClass().getName().replace('.', '_');
////		CREATE TABLE db_User(id INT PRIMARY KEY AUTO_INCREMENT,name varchar(20) default '',passhash varchar(255) default '',nlogins int default 0);
//		sb.append("insert into ").append(tablenm).append(" values()");
//		System.out.println(sb.toString());
//		s.execute(sb.toString(), Statement.RETURN_GENERATED_KEYS);
//		final ResultSet rs = s.getGeneratedKeys();
//		if (rs.next()) {
//			final long id = rs.getLong(1);
//			o.setId(id);
//		} else
//			throw new RuntimeException("no generated id");
//		rs.close();
//		return o;
//	}

//	public void delete(final DbObject o) throws Throwable {
//		o.delete();
//	}

	public void commit() throws Throwable {
		for (DbObject o : dirtyObjects) {
			o.update();
		}
		dirtyObjects.clear();
		s.close();
		c.commit();
	}

	public void rollback() throws Throwable {
		s.close();
		c.rollback();
	}

	public String toString() {
		return "dirtyObjects=" + dirtyObjects;
	}
}