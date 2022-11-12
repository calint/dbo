package db;

public class RelAggN extends DbField {
	private Class<? extends DbObject> toCls;

	public RelAggN(Class<? extends DbObject> cls) {
		this.toCls = cls;
	}

	@Override
	void sql_appendUpdateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getLong(this));
	}

	public DbObject create(DbObject from) {
		try {
			DbObject to = toCls.getConstructor().newInstance();
			to.set(this, from.getId());
			return to;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
	}

}
