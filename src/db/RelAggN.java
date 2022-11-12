package db;

public final class RelAggN extends DbRelation {
	private final Class<? extends DbObject> cls;
	private ForeignKeyField fkfld;

	public RelAggN(Class<? extends DbObject> cls) {
		this.cls = cls;
	}

	@Override
	void connect(final DbClass dbcls) {
		final DbClass toDbCls = Db.instance().dbClassForJavaClass(cls);
		fkfld = new ForeignKeyField();
		fkfld.dbname = dbcls.tableName + "_" + name;
		toDbCls.fields.add(fkfld);
	}

	public DbObject create(final DbObject ths) {
		try {
			final DbObject o = cls.getConstructor().newInstance();
			o.set(fkfld, ths.getId());
			o.createInDb();
			return o;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}