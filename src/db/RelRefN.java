package db;

import java.sql.Statement;

public final class RelRefN extends DbRelation {
	private final Class<? extends DbObject> cls;
	private MetaRelRefN rrm;

	public RelRefN(Class<? extends DbObject> cls) {
		this.cls = cls;
	}

	@Override
	void connect(final DbClass c) {
		rrm = new MetaRelRefN(c.jcls, name, cls);
		Db.instance().relRefNMeta.add(rrm);
	}

	public void add(final DbObject from, final DbObject to) {
		final Statement stmt = Db.currentTransaction().stmt;
		final StringBuilder sb = new StringBuilder(256);
		rrm.sql_addToTable(sb, from, to);
		final String sql = sb.toString();
		System.out.println(sql);
		try {
			stmt.execute(sql);
		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
	}

}
