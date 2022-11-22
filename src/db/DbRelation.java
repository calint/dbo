package db;

import java.sql.DatabaseMetaData;
import java.sql.Statement;

public abstract class DbRelation {
	/** the class where the relation was declared. initiated by db after all classes have been loaded */
	Class<? extends DbObject> cls;
	/** the table name of cls */
	String tableName;
	/** the name of the field that declared it. initiated by db after all classes have been loaded */
	String name;
	/** the class that the relations refers to */
	Class<? extends DbObject> toCls;
	/** the table name of toCls */
	final String toTableName;
	/** field used in relations. may be null or 0 */
	FldRel relFld;

	public DbRelation(Class<? extends DbObject> toCls) {
		this.toCls = toCls;
		toTableName = Db.tableNameForJavaClass(toCls);
	}

	/**
	 * Called after all DbClasses have been created. Necessary relation fields can
	 * be added to other DbClasses.
	 */
	void init(final DbClass c) {
	}

	public final String getName() {
		return name;
	}

	public final Class<? extends DbObject> getToClass() {
		return toCls;
	}

	/**
	 * Called after all tables have been created. relation can create necessary
	 * indexes. DatabaseMetaData is used to check if index already exists.
	 */
	void sql_createIndex(final Statement stmt, final DatabaseMetaData dbm) throws Throwable {
	}
	
	boolean cascadeDeleteNeeded() {
		return true;
	}

	void cascadeDelete(final DbObject ths) {
	}

	@Override
	public String toString() {
		return name;
	}
}
