package db;

public final class RelAgg extends DbRelation {
	private Class<? extends DbObject> cls;
	private FldForeignKey fkfld;

	public RelAgg(Class<? extends DbObject> cls) {
		this.cls = cls;
	}

	@Override
	void connect(final DbClass dbcls) {
		fkfld = new FldForeignKey();
		fkfld.dbname = name;
		dbcls.fields.add(fkfld);
	}

	public DbObject create(final DbObject ths) {
		try {
			final DbObject o = cls.getConstructor().newInstance();
			o.createInDb();
			ths.set(fkfld, o.getId());
			ths.updateDb();
			return o;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}

	}

}
