package db;

abstract class DbRelation {
	Class<? extends DbObject> cls;
	String name;

	void connect(final DbClass c) {
	}

	@Override
	public String toString() {
		return name;
	}

}
