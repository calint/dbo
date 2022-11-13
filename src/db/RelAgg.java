package db;

public final class RelAgg extends DbRelation {
	private Class<? extends DbObject> toCls;
	private FldForeignKey fkfld;

	public RelAgg(Class<? extends DbObject> toCls) {
		this.toCls = toCls;
	}

	@Override
	void connect(final DbClass dbcls) {
		fkfld = new FldForeignKey();
		fkfld.dbname = name;
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
