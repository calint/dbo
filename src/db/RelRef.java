package db;

public final class RelRef extends DbRelation {
	final Class<? extends DbObject> toCls;
	final String toTableName;
	FldRel relFld;

	public RelRef(Class<? extends DbObject> toCls) {
		this.toCls = toCls;
		toTableName = Db.tableNameForJavaClass(toCls);
	}

	@Override
	void connect(final DbClass c) {
		relFld = new FldRel();
		relFld.columnName = name;
		c.declaredFields.add(relFld);
	}

	public void set(final DbObject ths, final DbObject trg) {
		try {
			ths.set(relFld, trg.getId());
			ths.updateDb();
//			Db.currentTransaction().dirtyObjects.remove(ths);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}