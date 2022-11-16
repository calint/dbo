package db;

public final class RelRef extends DbRelation {
	final Class<? extends DbObject> toCls;
	final String toTableName;
	FldRel fkfld;

	public RelRef(Class<? extends DbObject> toCls) {
		this.toCls = toCls;
		toTableName = Db.tableNameForJavaClass(toCls);
	}

	@Override
	void connect(final DbClass c) {
		fkfld = new FldRel();
		fkfld.columnName = name;
		c.declaredFields.add(fkfld);
	}

	public void set(final DbObject ths, final DbObject trg) {
		try {
			ths.set(fkfld, trg.getId());
			ths.updateDb();
//			Db.currentTransaction().dirtyObjects.remove(ths);
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}