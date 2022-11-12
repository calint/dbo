package db;

public class RelRefN extends DbRelation {
	private Class<? extends DbObject> cls;

	public RelRefN(Class<? extends DbObject> cls) {
		this.cls = cls;
	}

}
