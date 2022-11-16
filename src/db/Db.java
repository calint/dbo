package db;

import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public final class Db {
	private static final ThreadLocal<DbTransaction> tn = new ThreadLocal<DbTransaction>();

	static void log(String s) {
		System.out.println(s);
	}

	public static DbTransaction initCurrentTransaction() throws Throwable {
		final Connection c = inst.conpool.pollFirst();
		if (c == null)// ? fix
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
	private final LinkedList<Connection> conpool = new LinkedList<Connection>();
	private final ArrayList<DbClass> dbclasses = new ArrayList<DbClass>();
	private final HashMap<Class<? extends DbObject>, DbClass> jclsToDbCls = new HashMap<Class<? extends DbObject>, DbClass>();
	final ArrayList<MetaRelRefN> relRefNMeta = new ArrayList<MetaRelRefN>();

	public void register(Class<? extends DbObject> cls) throws Throwable {
		final DbClass dbcls = new DbClass(cls);
		dbclasses.add(dbcls);
		jclsToDbCls.put(cls, dbcls);
	}

	public void init(final String url, final String user, final String password, final int ncons) throws Throwable {
		Db.log("jdbc connection: " + url);
		final Connection con = DriverManager.getConnection(url, user, password);

		final DatabaseMetaData dbm = con.getMetaData();
		Db.log(dbm.getDatabaseProductName() + " " + dbm.getDatabaseProductVersion());

		Db.log("--- - - - ---- - - - - - -- -- --- -- --- ---- -- -- - - -");

		// allow DbClass relations to add necessary fields to other DbClasses
		for (final DbClass c : dbclasses) {
			for (final DbRelation r : c.declaredRelations)// ? what about inherited relations
				r.connect(c);
		}

		// create allFields lists
		for (final DbClass c : dbclasses) {
			c.initAllFieldsList();
			Db.log(c.toString());
		}

		// DbClasses fields are now ready for create tables

		Db.log("--- - - - ---- - - - - - -- -- --- -- --- ---- -- -- - - -");

		// create tables
		final Statement stmt = con.createStatement();
		for (final DbClass dbcls : dbclasses) {
			if (Modifier.isAbstract(dbcls.javaClass.getModifiers()))
				continue;
			final StringBuilder sb = new StringBuilder(256);
			dbcls.sql_createTable(sb, dbm);
			if (sb.length() == 0)
				continue;
			final String sql = sb.toString();
			Db.log(sql);
			stmt.execute(sql);
		}

		// create RefN tables
		for (final MetaRelRefN rrm : relRefNMeta) {
			final StringBuilder sb = new StringBuilder(256);
			rrm.sql_createTable(sb, dbm);
			if (sb.length() == 0)
				continue;
			final String sql = sb.toString();
			Db.log(sql);
			stmt.execute(sql);
		}

		// all tables have been created

		// create indexes
		for (final DbClass dbcls : dbclasses) {
			for (final DbRelation dbrel : dbcls.declaredRelations) {// ? what about inherited relations
				final StringBuilder sb = new StringBuilder(256);
				dbrel.sql_createIndex(sb, dbm);
				if (sb.length() == 0)
					continue;
				final String sql = sb.toString();
				Db.log(sql);
				stmt.execute(sql);
			}
		}

		Db.log("--- - - - ---- - - - - - -- -- --- -- --- ---- -- -- - - -");

		// output tables, columns, indexes
		final ResultSet rs2 = dbm.getTables(null, null, null, new String[] { "TABLE" });
		while (rs2.next()) {
			final String tblname = rs2.getString("TABLE_NAME");
			Db.log("[" + tblname + "]");
			ResultSet rscols = dbm.getColumns(null, null, tblname, null);
			while (rscols.next()) {
				String columnName = rscols.getString("COLUMN_NAME");
				String datatype = rscols.getString("TYPE_NAME");
				String defval = rscols.getString("COLUMN_DEF");
				final StringBuilder sb = new StringBuilder();
				sb.append("    ").append(columnName).append(' ').append(datatype).append(' ');
				if (defval != null) {
					sb.append('\'').append(defval).append('\'');
				}

				Db.log(sb.toString());
			}
			rscols.close();
			
			ResultSet rsix = dbm.getIndexInfo(null, null, tblname, false, false);
			while (rsix.next()) {
				System.out.println(
						"  index " + rsix.getString("INDEX_NAME") + " on " + rsix.getString("COLUMN_NAME"));
			}
			rsix.close();
			System.out.println(" ");
		}
		rs2.close();

//		final ResultSet rs3 = stmt.executeQuery("select * from Data");
//		while (rs3.next()) {
//			Object o1 = rs3.getObject(2);
//			System.out.println(o1 == null ? "null" : o1.getClass().getName());
//			InputStream o2 = rs3.getBinaryStream(2);
//			System.out.println(o2 == null ? "null" : o2.getClass().getName());
//		}
//		rs3.close();

		stmt.close();
		con.close();

		// create connection pool
		for (int i = 0; i < ncons; i++) {
			final Connection c = DriverManager.getConnection(url, user, password);
			c.setAutoCommit(false);
			conpool.add(c);
		}

		Db.log("--- - - - ---- - - - - - -- -- --- -- --- ---- -- -- - - -");

	}

	public void deinitConnectionPool() {
		for (final Connection c : conpool) {
			try {
				c.close();
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	static String tableNameForJavaClass(Class<? extends DbObject> cls) {
		final String tblnm = cls.getName().substring(cls.getName().lastIndexOf('.') + 1);// ? package name
		return tblnm;
	}

	DbClass dbClassForJavaClass(final Class<?> c) {
		return jclsToDbCls.get(c);
	}
}