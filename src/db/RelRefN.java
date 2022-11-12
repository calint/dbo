package db;

public class RelRefN extends DbRelation {
	private Class<? extends DbObject> toCls;

	public RelRefN(Class<? extends DbObject> cls) {
		this.toCls = cls;
	}

}
