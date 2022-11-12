package db;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.LinkedList;

final public class Db {
	private static final ThreadLocal<DbTransaction> tn = new ThreadLocal<DbTransaction>();

	public static DbTransaction initCurrentTransaction() throws Throwable {
		final Connection c = inst.conpool.pollFirst();
		if (c == null)
			throw new RuntimeException("connection pool empty");
		final DbTransaction t = new DbTransaction(c);
		tn.set(t);
		return t;
	}

	public static void deinitCurrentTransaction() {
		inst.conpool.add(tn.get().c);
		tn.remove();
	}

	public static DbTransaction currentTransaction() {
		return tn.get();
	}

	final static Db inst = new Db();

	public final static Db instance() {
		return inst;
	}

	////////////////////////////////////////////////////////////
	private ArrayList<Class<? extends DbObject>> classes = new ArrayList<>();

	public Db() {
		register(DbObject.class);
	}

	public void register(Class<? extends DbObject> cls) {
		classes.add(cls);
		Field fs[] = cls.getDeclaredFields();
		for (Field f : fs) {
			if (!Modifier.isStatic(f.getModifiers()))
				continue;
			if (DbField.class.isAssignableFrom(f.getType())) {
				DbField dbf;
				try {
					dbf = (DbField) f.get(null);
				} catch (Throwable t) {
					throw new RuntimeException(t);
				}
				dbf.dbname = f.getName();
			}else if(RelAgg1.class.isAssignableFrom(f.getType())) {
				RelAgg1 dbf;
				try {
					dbf = (RelAgg1) f.get(null);
				} catch (Throwable t) {
					throw new RuntimeException(t);
				}
				dbf.dbname = f.getName();				
			}
		}
	}

	private LinkedList<Connection> conpool = new LinkedList<>();

	public void initConnectionPool(final String url, final String user, final String password, final int ncons)
			throws Throwable {
		for (int i = 0; i < ncons; i++) {
			final Connection c = DriverManager.getConnection(url, user, password);
			c.setAutoCommit(false);
			conpool.add(c);
			System.out.println((i + 1) + " connection");
		}
	}

	public void deinitConnectionPool() {
		for (Connection c : conpool) {
			try {
				c.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

}