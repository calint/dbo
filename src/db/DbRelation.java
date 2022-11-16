package db;

import java.sql.DatabaseMetaData;

abstract class DbRelation {
	Class<? extends DbObject> cls;
	String tableName;
	String name;

	/**
	 * called after initial init, at this time all dbclasses are created and fields
	 * can be added to other classes
	 */
	void connect(final DbClass c) {
	}

	@Override
	public String toString() {
		return name;
	}

	/**
	 * called after all tables have been created and relations can create necessary indexes
	 */
	void sql_createIndex(final StringBuilder sb,final DatabaseMetaData dbm) throws Throwable {		
	}

}
