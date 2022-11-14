package db;

import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public final class DbTransaction {
	final Connection con;
	final Statement stmt;
	final HashSet<DbObject> dirtyObjects = new HashSet<DbObject>();

	DbTransaction(final Connection c) throws Throwable {
		this.con = c;
		this.stmt = c.createStatement();
	}

	public DbObject create(final Class<? extends DbObject> cls) throws Throwable {
		final DbObject o = cls.getConstructor().newInstance();
		o.createInDb();
		return o;
	}

	public List<DbObject> get(final Class<? extends DbObject> cls, final Query q, final Order ord, final Limit lmt) {
		final Query.TableAliasMap tam = new Query.TableAliasMap();
		final StringBuilder sbwhere = new StringBuilder(128);
		if (q != null) {
			sbwhere.append("where ");
			q.sql_build(sbwhere, tam);
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
			while (rs.next()) {
				final DbObject o = ctor.newInstance();
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
		Db.log("*** flush connection ***");
		for (final DbObject o : dirtyObjects) {
			o.updateDb();
		}
		dirtyObjects.clear();
		Db.log("*** done flushing connection ***");
	}

	public void rollback() throws Throwable {
		Db.log("*** rollback transaction ***");
		stmt.close();
		con.rollback();
	}

	public String toString() {
		return "dirtyObjects=" + dirtyObjects;
	}
}