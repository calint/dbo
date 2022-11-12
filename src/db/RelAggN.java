package db;

public final class RelAggN extends DbRelation {
	final Class<? extends DbObject> cls;
	ForeignKeyField relfld;

	public RelAggN(Class<? extends DbObject> cls) {
		this.cls = cls;
	}

	@Override
	void connect(final DbClass dbcls) {
		final DbClass toDbCls = Db.instance().dbClassForJavaClass(cls);
		relfld = new ForeignKeyField();
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