package db;

import java.lang.reflect.Modifier;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public final class Db {
	private static final ThreadLocal<DbTransaction> tn = new ThreadLocal<DbTransaction>();

	public static boolean enable_log = true;

	public static void log(String s) {
		if (!enable_log)
			return;
		System.out.println(s);
	}

	public Connection createJdbcConnection() throws Throwable {
		final Connection c = DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPasswd);
		return c;
	}

	/** initiates thread local for Db.currentTransaction() */
	public static DbTransaction initCurrentTransaction() {
		Connection c = null;
		synchronized (inst.conpool) {
			while (inst.conpool.isEmpty()) { // spurious interrupt might happen
				try {
					inst.conpool.wait();
				} catch (InterruptedException e) {
					e.printStackTrace(); // ? what to do?
				}
			}
			c = inst.conpool.removeFirst();
		}
		try {
			final DbTransaction t = new DbTransaction(c);
			tn.set(t);
			return t;
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}

	public static void deinitCurrentTransaction() {
		final DbTransaction t = tn.get();

		// make sure statement is closed here. should be. // ? stmt.isClosed() is not in
		// java 1.5
//		final boolean stmtIsClosed;
//		try {
//			stmtIsClosed = t.stmt.isClosed();
//		} catch (Throwable e) {
//			throw new RuntimeException(e);
//		}
//		if (!stmtIsClosed)
//			throw new RuntimeException(
//					"Statement should be closed here. DbTransaction.finishTransaction() not called?");
//
		synchronized (inst.conpool) {
			inst.conpool.add(t.con);
			inst.conpool.notify();
		}

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

	// --- -- - -- -- - - --- - -- -- - -- --- - - - -- - - -- - -- -- -- - -- - - -
	private final LinkedList<Connection> conpool = new LinkedList<Connection>();
	private final ArrayList<DbClass> dbclasses = new ArrayList<DbClass>();
	private final HashMap<Class<? extends DbObject>, DbClass> clsToDbClsMap = new HashMap<Class<? extends DbObject>, DbClass>();
	final ArrayList<RelRefNMeta> relRefNMeta = new ArrayList<RelRefNMeta>();

	/** if true undeclared columns are deleted */
	public boolean enable_delete_unused_columns = true;

	/** if true undeclared indexes are deleted */
	public boolean enable_drop_undeclared_indexes = true;

	/**
	 * Objects deleted that are being referred to by other classes trigger update of
	 * referring classes setting referring field to null.
	 */
	public boolean enable_update_referring_tables = true;

	/**
	 * Objects retrieved from database return the same instance that was previously
	 * retrieved in the transaction.
	 */
	public boolean enable_cache = true;

	private String jdbcUrl;
	private String jdbcUser;
	private String jdbcPasswd;

	public void register(final Class<? extends DbObject> cls) throws Throwable {
		final DbClass dbcls = new DbClass(cls);
		dbclasses.add(dbcls);
		clsToDbClsMap.put(cls, dbcls);
	}

	public void init(final String url, final String user, final String password, final int ncons) throws Throwable {
		jdbcUrl = url;
		jdbcUser = user;
		jdbcPasswd = password;
		Db.log("--- - - - ---- - - - - - -- -- --- -- --- ---- -- -- - - -");
		Db.log("connection: " + url);
		Db.log("      user: " + user);
		Db.log("  password: " + (password == null ? "[none]" : "[not displayed]"));
		final Connection con = DriverManager.getConnection(url, user, password);

		final DatabaseMetaData dbm = con.getMetaData();
		Db.log("    driver: " + dbm.getDriverName() + " " + dbm.getDriverVersion());
		Db.log("    server: " + dbm.getDatabaseProductName() + " " + dbm.getDatabaseProductVersion());
		Db.log("--- - - - ---- - - - - - -- -- --- -- --- ---- -- -- - - -");

		// recursively populate lists: allFields, allRelations, allIndexes
		for (final DbClass c : dbclasses) {
			c.init();
		}

		// allow DbClass relations to add necessary fields and indexes, even to other
		// DbClasses
		for (final DbClass c : dbclasses) {
			for (final DbRelation r : c.allRelations)
				r.init(c);
		}

		// allow indexes to initiate using fully initiated relations.
		for (final DbClass c : dbclasses) {
			for (final Index ix : c.allIndexes)
				ix.init(c);
		}

		// print summary
		for (final DbClass c : dbclasses) {
			Db.log(c.toString());
		}

		// DbClasses, fields and indexes are now ready for create/modify

		Db.log("--- - - - ---- - - - - - -- -- --- -- --- ---- -- -- - - -");

		ensureTablesAndIndexes(con, dbm);

		Db.log("--- - - - ---- - - - - - -- -- --- -- --- ---- -- -- - - -");

		printDbMetaInfo(dbm);

		con.close();

		// create connection pool
		for (int i = 0; i < ncons; i++) {
			final Connection c = DriverManager.getConnection(url, user, password);
			c.setAutoCommit(false);
			conpool.add(c);
		}

		Db.log("--- - - - ---- - - - - - -- -- --- -- --- ---- -- -- - - -");
	}

	private void printDbMetaInfo(final DatabaseMetaData dbm) throws SQLException {
		// output tables, columns, indexes
		final ResultSet rstbls = dbm.getTables(null, null, null, new String[] { "TABLE" });
		while (rstbls.next()) {
			final String tblname = rstbls.getString("TABLE_NAME");
			Db.log("[" + tblname + "]");
			ResultSet rscols = dbm.getColumns(null, null, tblname, null);
			while (rscols.next()) {
				String columnName = rscols.getString("COLUMN_NAME");
				String datatype = rscols.getString("TYPE_NAME");
				String defval = rscols.getString("COLUMN_DEF");
				final StringBuilder sb = new StringBuilder();
				sb.append("    ").append(columnName).append(' ').append(datatype);
				if (defval != null) {
					sb.append(" '").append(defval).append('\'');
				}
				Db.log(sb.toString());
			}
			rscols.close();

			final ResultSet rsix = dbm.getIndexInfo(null, null, tblname, false, false);
			while (rsix.next()) {
				Db.log("  index " + rsix.getString("INDEX_NAME") + " on " + rsix.getString("COLUMN_NAME"));
			}
			rsix.close();
			Db.log("");
		}
		rstbls.close();
	}

	private void ensureTablesAndIndexes(final Connection con, final DatabaseMetaData dbm) throws Throwable {
		// ensure RefN tables exist and match to definition
		final Statement stmt = con.createStatement();

		for (final DbClass dbcls : dbclasses) {
			if (Modifier.isAbstract(dbcls.javaClass.getModifiers()))
				continue;
			dbcls.ensureTable(stmt, dbm);
		}

		// ensure RefN tables exist and match to definition
		for (final RelRefNMeta rrm : relRefNMeta) {
			rrm.ensureTable(stmt, dbm);
		}

		// all tables exist

		// relations might need to create index directly with Statement or add to
		// allIndexes to other classes
		for (final DbClass dbcls : dbclasses) {
			for (final DbRelation dbrel : dbcls.allRelations) {
				dbrel.ensureIndexes(stmt, dbm);
			}
		}

		// ensure indexes exist and match definition
		for (final DbClass dbcls : dbclasses) {
			for (final Index ix : dbcls.allIndexes) {
				ix.ensureIndexes(stmt, dbm);
			}
		}

		if (enable_drop_undeclared_indexes) {
			// drop undeclared indexes
			for (final DbClass dbcls : dbclasses) {
				dbcls.dropUndeclaredIndexes(stmt, dbm);
			}

			// drop unused RefN tables
			final ArrayList<String> refsTbls = new ArrayList<String>();
			final ResultSet rstbls = RelRefNMeta.getAllRefsTables(dbm);
			while (rstbls.next()) {
				final String tbl = rstbls.getString("TABLE_NAME");
				refsTbls.add(tbl);
			}

			for (final RelRefNMeta rrm : relRefNMeta) {
				refsTbls.remove(rrm.tableName);
			}

			for (final String s : refsTbls) {
				final StringBuilder sb = new StringBuilder(128);
				sb.append("drop table ").append(s);
				final String sql = sb.toString();
				Db.log(sql);
				stmt.execute(sql);
			}
		}

		stmt.close();
	}

	/**
	 * !!! deletes all and recreates tables and indexes. used by testing framework
	 */
	public void reset() {
		Db.log("*** reseting database");
		Connection con = null;
		try {
			con = createJdbcConnection();
			final Statement stmt = con.createStatement();
			final StringBuilder sb = new StringBuilder();
			for (final DbClass dbc : dbclasses) {
				if (Modifier.isAbstract(dbc.javaClass.getModifiers()))
					continue;
				sb.setLength(0);
				sb.append("drop table ").append(dbc.tableName);
				final String sql = sb.toString();
				Db.log(sql);
				stmt.execute(sql);
			}
			for (final RelRefNMeta rnm : relRefNMeta) {
				sb.setLength(0);
				sb.append("drop table ").append(rnm.tableName);
				final String sql = sb.toString();
				Db.log(sql);
				stmt.execute(sql);
			}
			stmt.close();

			ensureTablesAndIndexes(con, con.getMetaData());
		} catch (Throwable t) {
			t.printStackTrace();
		} finally {
			try {
				if (con != null)
					con.close();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		}
	}

	public void shutdown() {
		Db.inst = null;
		synchronized (conpool) {
			for (final Connection c : conpool) {
				try {
					c.close();
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
			conpool.clear();
		}
	}

	static String tableNameForJavaClass(final Class<? extends DbObject> cls) {
		final String tblnm = cls.getName().substring(cls.getName().lastIndexOf('.') + 1);// ? package name
//		final String tblnm = cls.getName().replace('.', '_');
//		final String tblnm = cls.getName();
		return tblnm;
	}

	DbClass dbClassForJavaClass(final Class<?> c) {
		return clsToDbClsMap.get(c);
	}

//	public List<DbClass> getDbClasses() {
//		return dbclasses;
//	}

	public DbClass getDbClassForJavaClass(final Class<? extends DbObject> cls) {
		return clsToDbClsMap.get(cls);
	}
}
