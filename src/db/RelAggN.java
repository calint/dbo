package db;

public final class RelAggN extends DbRelation {
	final Class<? extends DbObject> toCls;
	final String toTableName;
	FldRel fkfld;

	public RelAggN(Class<? extends DbObject> toCls) {
		this.toCls = toCls;
		toTableName = Db.tableNameForJavaClass(toCls);
	}

	@Override
	void connect(final DbClass dbcls) {
		final DbClass toDbCls = Db.instance().dbClassForJavaClass(toCls);
		fkfld = new FldRel();
		fkfld.columnName = dbcls.tableName + "_" + name;
		toDbCls.declaredFields.add(fkfld);
	}

	public DbObject create(final DbObject ths) {
		try {
			final DbObject o = toCls.getConstructor().newInstance();
			o.set(fkfld, ths.getId());
			o.createInDb();
			return o;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

	// select File.* from User,File where User.id=File.User_files;
	// select t2.* from User as t1,File as t2 where t1.id=t2.User_files;
}