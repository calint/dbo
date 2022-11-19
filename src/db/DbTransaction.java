package db;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public final class DbTransaction {
	final Connection con;
	final Statement stmt;
	final HashSet<DbObject> dirtyObjects = new HashSet<DbObject>();
	final Cache cache = new Cache();
	public boolean cache_enabled = true;

	static final class Cache {
		final HashMap<Class<? extends DbObject>, HashMap<Integer, DbObject>> clsToIdObjMap = new HashMap<Class<? extends DbObject>, HashMap<Integer, DbObject>>();

		void put(final DbObject o) {
			HashMap<Integer, DbObject> idToObjMap = clsToIdObjMap.get(o.getClass());
			if (idToObjMap == null) {
				idToObjMap = new HashMap<Integer, DbObject>();
				clsToIdObjMap.put(o.getClass(), idToObjMap);
			}
			idToObjMap.put(o.id(), o);
		}

		DbObject get(Class<? extends DbObject> cls, int id) {
			HashMap<Integer, DbObject> idToObjMap = clsToIdObjMap.get(cls);
			if (idToObjMap == null)
				return null;
			final DbObject o = idToObjMap.get(id);
			return o;
		}

		void remove(final DbObject o) {
			final HashMap<Integer, DbObject> idToObjMap = clsToIdObjMap.get(o.getClass());
			idToObjMap.remove(o.id());
		}

		void clear() {
			clsToIdObjMap.clear();
		}
	}

	DbTransaction(final Connection c) throws Throwable {
		this.con = c;
		this.stmt = c.createStatement();
	}

	public Statement getJdbcStatement() {
		return stmt;
	}

	public Connection getJdbcConnetion() {
		return con;
	}

	public DbObject create(final Class<? extends DbObject> cls) {
		try {
			final DbObject o = cls.getConstructor().newInstance();
			o.createInDb();
			if (cache_enabled)
				cache.put(o);
			return o;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public void delete(final DbObject o) {
		o.deleteFromDb();
	}

	public List<DbObject> get(final Class<? extends DbObject> cls, final Query qry, final Order ord, final Limit lmt) {
		flush(); // update database before query

		final Query.TableAliasMap tam = new Query.TableAliasMap();
		final StringBuilder sbwhere = new StringBuilder(128);
		if (qry != null) {
			sbwhere.append("where ");
			qry.sql_build(sbwhere, tam);
		}

		final DbClass dbcls = Db.instance().dbClassForJavaClass(cls);
		final StringBuilder sb = new StringBuilder(256);
		sb.append("select ").append(tam.getAliasForTableName(dbcls.tableName)).append(".* from ");
		tam.sql_appendSelectFromTables(sb);
		sb.append(" ");

		if (sbwhere.length() != 0)
			sb.append(sbwhere);

		if (ord != null)
			ord.sql_appendToQuery(sb, tam);

		if (lmt != null)
			lmt.sql_appendToQuery(sb);

		sb.setLength(sb.length() - 1);

		final String sql = sb.toString();
		Db.log(sql);
		final ArrayList<DbObject> ls = new ArrayList<DbObject>(128);
		try {
			final Constructor<? extends DbObject> ctor = cls.getConstructor();
			final ResultSet rs = stmt.executeQuery(sql);
			if (cache_enabled) {
				while (rs.next()) {
					final DbObject cachedObj = cache.get(cls, rs.getInt(1));// ? assumes id index
					if (cachedObj != null) {
						ls.add(cachedObj);
						continue;
					}
					final DbObject o = ctor.newInstance();
					o.readResultSet(dbcls, rs);
					cache.put(o);
					ls.add(o);
				}
			} else {
				while (rs.next()) {
					final DbObject o = ctor.newInstance();
					o.readResultSet(dbcls, rs);
					ls.add(o);
				}
			}
		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
		return ls;
	}

	public void commit() throws Throwable {
		flush();
		if (cache_enabled) // will keep memory usage down at batch imports
			cache.clear();
		con.commit();
	}

	public void finishTransaction() throws Throwable {
		commit();
		stmt.close();
	}

	private void flush() {
		Db.log("*** flush connection. " + dirtyObjects.size() + " objects");
		try {
			for (final DbObject o : dirtyObjects) {
				o.updateDb();
			}
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}

		dirtyObjects.clear();
		Db.log("*** done flushing connection");
	}

	public void rollback() {
		Db.log("*** rollback transaction");
		if (cache_enabled)
			cache.clear();
		try {
			stmt.close();
			con.rollback();
			Db.log("*** rollback done");
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	public String toString() {
		return "dirtyObjects=" + dirtyObjects;
	}
}