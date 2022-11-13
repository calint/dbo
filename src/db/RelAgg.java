package db;

public final class RelAgg extends DbRelation {
	final Class<? extends DbObject> toCls;
	final String toTableName;
	FldForeignKey fkfld;

	public RelAgg(Class<? extends DbObject> toCls) {
		this.toCls = toCls;
		toTableName = Db.tableNameForJavaClass(toCls);
	}

	@Override
	void connect(final DbClass dbcls) {
		fkfld = new FldForeignKey();
		fkfld.columnName = name;
		dbcls.fields.add(fkfld);
	}

	public DbObject create(final DbObject ths) {
		try {
			final DbObject o = toCls.getConstructor().newInstance();
			o.createInDb();
			ths.set(fkfld, o.getId());
			ths.updateDb();
//			Db.currentTransaction().dirtyObjects.remove(ths);
			return o;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}

	}

}
