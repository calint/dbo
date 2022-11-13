package db;

public final class RelAggN extends DbRelation {
	private final Class<? extends DbObject> toCls;
	private FldForeignKey fkfld;

	public RelAggN(Class<? extends DbObject> toCls) {
		this.toCls = toCls;
	}

	@Override
	void connect(final DbClass dbcls) {
		final DbClass toDbCls = Db.instance().dbClassForJavaClass(toCls);
		fkfld = new FldForeignKey();
		fkfld.dbname = dbcls.tableName + "_" + name;
		toDbCls.fields.add(fkfld);
	}

	public DbObject create(final DbObject ths) {
		try {
			final DbObject o = toCls.getConstructor().newInstance();
			o.set(fkfld, ths.getId());
			o.createInDb();
			return o;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}