package db;

import java.sql.DatabaseMetaData;
import java.sql.Statement;

public abstract class DbRelation {
	/**
	 * the class where the relation was declared. initiated by db after all classes
	 * have been loaded
	 */
	Class<? extends DbObject> cls;

	/** the table name of cls. initiated by db after all classes have been loaded */
	String tableName;

	/**
	 * the name of the field that declared it. initiated by db after all classes
	 * have been loaded
	 */
	String name;

	/** the class that the relations refers to */
	final Class<? extends DbObject> toCls;

	/** the table name of toCls */
	final String toTableName;

	/** field used in relations. may be null or 0 */
	FldRel relFld;

	public DbRelation(final Class<? extends DbObject> toCls) {
		this.toCls = toCls;
		toTableName = Db.tableNameForJavaClass(toCls);
	}

	/**
	 * First init. Called after all DbClasses have been created. Fields necessary
	 * for the relation can be added to the class or target class.
	 */
	void init(final DbClass c) {
	}

	/**
	 * Second init. Necessary indexes can be added to the class or target class.
	 */
	void init2(final DbClass c) {
	}

	/**
	 * Called after all tables have been created. Relation checks and creates
	 * indexes using stmt.
	 */
	void ensureIndexes(final Statement stmt, final DatabaseMetaData dbm) throws Throwable {
	}

	public final String getName() {
		return name;
	}

	public final Class<? extends DbObject> getToClass() {
		return toCls;
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
