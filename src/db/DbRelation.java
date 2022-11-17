package db;

import java.sql.DatabaseMetaData;

abstract class DbRelation {
	Class<? extends DbObject> cls;
	String tableName;
	String name;

	/**
	 * Called after all DbClasses have been created and necessary relation fields
	 * can be added to other DbClasses.
	 */
	void connect(final DbClass c) {
	}

	/**
	 * Called after all tables have been created and relations can create necessary
	 * indexes. DatabaseMetaData is used to check if index already exists.
	 */
	void sql_createIndex(final StringBuilder sb, final DatabaseMetaData dbm) throws Throwable {
	}

	void cascadeDelete(final DbObject ths) {
	}

	@Override
	public String toString() {
		return name;
	}
}
