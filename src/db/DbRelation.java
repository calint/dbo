package db;

import java.util.HashMap;

abstract class DbRelation {
	String name;

	void connect(final DbClass c, final HashMap<Class<? extends DbObject>, DbClass> jclsToDbCls) {
	}

	@Override
	public String toString() {
		return name;
	}

}
