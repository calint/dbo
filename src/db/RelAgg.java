package db;

import java.util.HashMap;

public final class RelAgg extends DbRelation {
	private Class<? extends DbObject> cls;
	LongField relfld;

	public RelAgg(Class<? extends DbObject> cls) {
		this.cls = cls;
	}

	@Override
	void connect(final DbClass dbcls, final HashMap<Class<? extends DbObject>, DbClass> jclsToDbCls) {
		relfld = new LongField();
		relfld.dbname = name;
		dbcls.fields.add(relfld);
	}

	public DbObject create(final DbObject ths) {
		try {
			final DbObject o = cls.getConstructor().newInstance();
			o.createInDb();
			ths.set(relfld, o.getId());
			ths.updateDb();
			return o;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}

	}

}
