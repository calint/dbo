package db;

import java.sql.Statement;

public final class RelRefN extends DbRelation {
	private final Class<? extends DbObject> cls;
	private RelRefNMeta rrm;

	public RelRefN(Class<? extends DbObject> cls) {
		this.cls = cls;
	}

	@Override
	void connect(final DbClass c) {
		rrm = new RelRefNMeta();
		rrm.fromCls = c.jcls;
		rrm.toCls = cls;
		final StringBuilder sb = new StringBuilder(256).append(Db.tableNameForJavaClass(rrm.fromCls)).append('_')
				.append(name);
		rrm.tableName = sb.toString();
		Db.instance().relRefNMeta.add(rrm);
	}

	public void add(DbObject from, DbObject to) {
		final Statement stmt = Db.currentTransaction().stmt;
		final StringBuilder sb = new StringBuilder(256);
		sb.append("insert into ").append(rrm.tableName).append(" values(").append(from.getId()).append(',')
				.append(to.getId()).append(')');
		final String sql = sb.toString();
		System.out.println(sql);
		try {
			stmt.execute(sql);
		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
	}

}
