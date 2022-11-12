package db;

import java.util.HashMap;

public final class RelAggN extends DbRelation {
	final Class<? extends DbObject> cls;
	LongField relfld;

	public RelAggN(Class<? extends DbObject> cls) {
		this.cls = cls;
	}

	@Override
	void connect(final DbClass dbcls, final HashMap<Class<? extends DbObject>, DbClass> jclsToDbCls) {
		final DbClass toDbCls = jclsToDbCls.get(cls);
		relfld = new LongField();
		relfld.dbname = dbcls.tableName + "_" + name;
		toDbCls.fields.add(relfld);
	}

	public DbObject create(final DbObject ths) {
		try {
			final DbObject o = cls.getConstructor().newInstance();
			o.set(relfld, ths.getId());
			o.createInDb();
			return o;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}