package db;

import java.sql.Statement;
import java.util.List;

public final class RelAgg extends DbRelation {
	final Class<? extends DbObject> toCls;
	final String toTableName;
	FldRel relFld;

	public RelAgg(Class<? extends DbObject> toCls) {
		this.toCls = toCls;
		toTableName = Db.tableNameForJavaClass(toCls);
	}

	@Override
	void connect(final DbClass dbcls) {
		relFld = new FldRel();
		relFld.columnName = name;
		dbcls.declaredFields.add(relFld);
	}

	/** @returns 0 if id is null */
	public int getId(final DbObject ths) {
		final Object objId = ths.fieldValues.get(this.relFld);
		if (objId == null)
			return 0;
		return ((Integer) objId).intValue();
	}

	public DbObject get(final DbObject ths, final boolean createIfNone) {
		final int id = getId(ths);
		if (id == 0) {
			if (createIfNone) {
				try {
					final DbObject o = toCls.getConstructor().newInstance();
					o.createInDb();
					ths.set(relFld, o.id());
//					ths.updateDb();
					return o;
				} catch (Throwable t) {
					throw new RuntimeException(t);
				}
			}
			return null;
		}
		final List<? extends DbObject> ls = Db.currentTransaction().get(toCls, new Query(toCls, id), null, null);
		if (ls.isEmpty())
			throw new RuntimeException("didnt't expect empty result for " + toCls.getName() + " id=" + id);
		return ls.get(0);
	}

	public void delete(final DbObject ths) {
		final Statement stmt = Db.currentTransaction().stmt;
		final StringBuilder sb = new StringBuilder(256);
		final int toId = ths.getInt(relFld);
		sb.append("delete from ").append(toTableName).append(" where ").append(DbObject.id.columnName).append('=')
				.append(toId);
		final String sql = sb.toString();
		Db.log(sql);
		try {
			stmt.execute(sql);
			ths.set(relFld, 0);
//			ths.updateDb();
		} catch (final Throwable t) {
			throw new RuntimeException(t);
		}
	}

}
