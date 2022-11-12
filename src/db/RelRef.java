package db;

public final class RelRef extends DbRelation {
//	private final Class<? extends DbObject> cls;
	private FldForeignKey fkfld;

	public RelRef(Class<? extends DbObject> cls) {
//		this.cls = cls;
	}

	@Override
	void connect(final DbClass c) {
		fkfld = new FldForeignKey();
		fkfld.dbname = name;
		c.fields.add(fkfld);
	}

	public void set(final DbObject ths, final DbObject trg) {
		try {
			ths.set(fkfld, trg.getId());
			ths.updateDb();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}