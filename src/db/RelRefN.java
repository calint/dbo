package db;

public class RelRefN extends DbField {
	private Class<? extends DbObject> toCls;

	public RelRefN(Class<? extends DbObject> cls) {
		this.toCls = cls;
	}

	@Override
	void sql_appendUpdateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getLong(this));
	}

}
