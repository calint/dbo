package db;

import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

	private static Db inst;

	public static void initInstance() throws Throwable {
		inst = new Db();
	}

	public static Db instance() {
		return inst;
	}

	////////////////////////////////////////////////////////////
	private final ArrayList<DbClass> classes = new ArrayList<>();
	private final HashMap<Class<? extends DbObject>, DbClass> jclsToDbCls = new HashMap<>();

	public Db() throws Throwable {
		register(DbObject.class);
	}

	public void register(Class<? extends DbObject> cls) throws Throwable {
		final DbClass dbcls = new DbClass(cls);
		classes.add(dbcls);
		jclsToDbCls.put(cls, dbcls);
	}

	final private LinkedList<Connection> conpool = new LinkedList<>();

	public void init(final String url, final String user, final String password, final int ncons) throws Throwable {

		for (final DbClass c : classes) {
			for (final DbRelation r : c.relations) {
				r.connect(c, jclsToDbCls);
			}
			System.out.println(c);
		}

		final Connection c = DriverManager.getConnection(url, user, password);
		final DatabaseMetaData dbm = c.getMetaData();

		// get table names
		final HashSet<String> tblNames = new HashSet<>();
		try (final ResultSet rs = dbm.getTables(null, null, null, new String[] { "TABLE" })) {
			while (rs.next()) {
				final String tblname = rs.getString("TABLE_NAME");
				tblNames.add(tblname);
			}
		}

		// create missing tables
		final Statement s = c.createStatement();
		for (final DbClass dbcls : classes) {
			if (Modifier.isAbstract(dbcls.jcls.getModifiers()))
				continue;
			if (tblNames.contains(dbcls.tableName))
				continue;
			final StringBuilder sb = new StringBuilder(256);
			dbcls.sql_createTable(sb, jclsToDbCls);
			final String sql = sb.toString();
			System.out.println(sql);
			s.execute(sql);
		}
		s.close();
		c.close();

		initConnectionPool(url, user, password, ncons);
	}

	private void initConnectionPool(final String url, final String user, final String password, final int ncons)
			throws Throwable {
		System.out.println("jdbc connection: " + url);
		for (int i = 0; i < ncons; i++) {
			final Connection c = DriverManager.getConnection(url, user, password);
			c.setAutoCommit(false);
			conpool.add(c);
			System.out.print(".");
			System.out.flush();
		}
		System.out.println();
	}

	public void deinitConnectionPool() {
		System.out.println("close jdbc connections");
		for (final Connection c : conpool) {
			try {
				c.close();
				System.out.print(".");
				System.out.flush();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		System.out.println();
	}

	static String tableNameForJavaClass(Class<? extends DbObject> cls) {
		final String tblnm = cls.getName().substring(cls.getName().lastIndexOf('.')+1);
		return tblnm;
	}
}