package db;

abstract class DbRelation {
	String name;

	void connect(final DbClass c) {
	}

	@Override
	public String toString() {
		return name;
	}

}
