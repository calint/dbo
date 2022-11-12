package db;

public final class RelRef extends DbRelation {
	private Class<? extends DbObject> cls;
	ForeignKeyField relfld;

	public RelRef(Class<? extends DbObject> cls) {
		this.cls = cls;
	}

	@Override
	void connect(final DbClass c) {
		relfld = new ForeignKeyField();
		relfld.dbname = name;
		c.fields.add(relfld);
	}

	public void set(final DbObject ths, final DbObject trg) {
		try {
			ths.set(relfld, trg.getId());
			ths.updateDb();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}
