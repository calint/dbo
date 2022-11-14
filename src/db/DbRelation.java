package db;

abstract class DbRelation {
	Class<? extends DbObject> cls;
	String tableName;
	String name;

	/** called after initial init, at this time all dbclasses are created and fields can be added to other classes */
	void connect(final DbClass c) {
	}

	@Override
	public String toString() {
		return name;
	}

}
