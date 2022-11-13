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

public final class Db {
	private static final ThreadLocal<DbTransaction> tn = new ThreadLocal<DbTransaction>();

	public static DbTransaction initCurrentTransaction() throws Throwable {
		final Connection c = inst.conpool.pollFirst();
		if (c == null)
			throw new RuntimeException("connection pool empty");// ?
		final DbTransaction t = new DbTransaction(c);
		tn.set(t);
		return t;
	}

	public static void deinitCurrentTransaction() {
		inst.conpool.add(tn.get().con);
		tn.remove();
	}

	public static DbTransaction currentTransaction() {
		return tn.get();
	}

	private static Db inst;

	public static void initInstance() throws Throwable {
		inst = new Db();
		inst.register(DbObject.class);
	}

	public static Db instance() {
		return inst;
	}

	////////////////////////////////////////////////////////////
	private final LinkedList<Connection> conpool = new LinkedList<>();
	private final ArrayList<DbClass> dbclasses = new ArrayList<>();
	private final HashMap<Class<? extends DbObject>, DbClass> jclsToDbCls = new HashMap<>();
	final ArrayList<MetaRelRefN> relRefNMeta = new ArrayList<>();

	public void register(Class<? extends DbObject> cls) throws Throwable {
		final DbClass dbcls = new DbClass(cls);
		dbclasses.add(dbcls);
		jclsToDbCls.put(cls, dbcls);
	}
	
	private void initDbClasses() {
		// allow relations to add necessary fields to other dbclasses
		for (final DbClass c : dbclasses) {
			for (final DbRelation r : c.relations)
				r.connect(c);
			System.out.println(c);
		}

		// create allFields lists
		for (final DbClass c : dbclasses)
			c.initAllFieldsList();
	}

	public void init(final String url, final String user, final String password, final int ncons) throws Throwable {
		initDbClasses();
		System.out.println("--- - - - ---- - - - - - -- -- --- -- --- ---- -- -- - - -");

		final Connection con = DriverManager.getConnection(url, user, password);
		final DatabaseMetaData dbm = con.getMetaData();

		// get table names
		final HashSet<String> tblNames = new HashSet<>();
		try (final ResultSet rs = dbm.getTables(null, null, null, new String[] { "TABLE" })) {
			while (rs.next()) {
				final String tblname = rs.getString("TABLE_NAME");
				tblNames.add(tblname);
			}
		}

		// create missing tables
		final Statement stmt = con.createStatement();
		for (final DbClass dbcls : dbclasses) {
			if (Modifier.isAbstract(dbcls.jcls.getModifiers()))
				continue;
			if (tblNames.contains(dbcls.tableName))
				continue;//? check columns
			
			final StringBuilder sb = new StringBuilder(256);
			dbcls.sql_createTable(sb);
			final String sql = sb.toString();
			System.out.println(sql);
			stmt.execute(sql);
		}

		// create special RefN tables
		for (final MetaRelRefN rrm : relRefNMeta) {
			if (rrm.tableIsIn(tblNames))
				continue;
			final StringBuilder sb = new StringBuilder(256);
			rrm.sql_createTable(sb);
			final String sql = sb.toString();
			System.out.println(sql);
			stmt.execute(sql);
		}

		// map DbClass fields to ResultSet
//		for (final DbClass c : dbclasses) {
//			if(Modifier.isAbstract(c.jcls.getModifiers()))
//				continue;
//			final ResultSet rs=dbm.getColumns(null, null, c.tableName, null);
//			while(rs.next()) {
//				final String colname=rs.getString(4);
//				System.out.println(colname);// column name
//			}
//		}

		// done
		stmt.close();
		con.close();

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
//		System.out.println("close jdbc connections");
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
		final String tblnm = cls.getName().substring(cls.getName().lastIndexOf('.') + 1);
		return tblnm;
	}

	DbClass dbClassForJavaClass(final Class<?> c) {
		return jclsToDbCls.get(c);
	}
}