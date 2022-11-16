package db;

public final class RelAggN extends DbRelation {
	final Class<? extends DbObject> toCls;
	final String toTableName;
	FldRel relFld;

	public RelAggN(Class<? extends DbObject> toCls) {
		this.toCls = toCls;
		toTableName = Db.tableNameForJavaClass(toCls);
	}

	@Override
	void connect(final DbClass dbcls) {
		final DbClass toDbCls = Db.instance().dbClassForJavaClass(toCls);
		relFld = new FldRel();
		relFld.columnName = dbcls.tableName + "_" + name;
		toDbCls.declaredFields.add(relFld);
	}

	public DbObject create(final DbObject ths) {
		try {
			final DbObject o = toCls.getConstructor().newInstance();
			o.set(relFld, ths.getId());
			o.createInDb();
			return o;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}