package db;

public final class RelRef extends DbRelation {
	final Class<? extends DbObject> toCls;
	final String toTableName;
	FldForeignKey fkfld;

	public RelRef(Class<? extends DbObject> toCls) {
		this.toCls = toCls;
		toTableName = Db.tableNameForJavaClass(toCls);
	}

	@Override
	void connect(final DbClass c) {
		fkfld = new FldForeignKey();
		fkfld.columnName = name;
		c.fields.add(fkfld);
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