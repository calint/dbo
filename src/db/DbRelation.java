package db;

import java.sql.DatabaseMetaData;
import java.sql.Statement;

abstract class DbRelation {
	Class<? extends DbObject> cls;
	String tableName;
	String name;
	FldRel relFld;

	/**
	 * Called after all DbClasses have been created. Necessary relation fields can
	 * be added to other DbClasses.
	 */
	void init(final DbClass c) {
	}

	/**
	 * Called after all tables have been created and relations can create necessary
	 * indexes. DatabaseMetaData is used to check if index already exists.
	 */
	// ? createIndex(DatabaseMetadata) instead, relations executes sql already
	void sql_createIndex(final Statement stmt, final DatabaseMetaData dbm) throws Throwable {
	}

	void cascadeDelete(final DbObject ths) {
	}

	@Override
	public String toString() {
		return name;
	}
}
