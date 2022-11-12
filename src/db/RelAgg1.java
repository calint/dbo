package db;

public class RelAgg1 extends DbField {
	private Class<? extends DbObject> toCls;

	public RelAgg1(Class<? extends DbObject> cls) {
		this.toCls = cls;
	}

	@Override
	void sql_appendUpdateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getLong(this));
	}

	public DbObject create(DbObject from) {
		try {
			DbObject to = toCls.getConstructor().newInstance();
			from.set(this, to.getId());
			return to;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}
}
