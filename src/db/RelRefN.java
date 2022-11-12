package db;

public final class RelRefN extends DbRelation {
	private Class<? extends DbObject> cls;

	public RelRefN(Class<? extends DbObject> cls) {
		this.cls = cls;
	}
	
	@Override
	void connect(final DbClass c) {
	}

}
