package db;

public class RelRef extends DbField {
	private Class<? extends DbObject> toCls;

	public RelRef(Class<? extends DbObject> cls) {
		this.toCls = cls;
	}

	@Override
	void sql_appendUpdateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getLong(this));
	}

}
