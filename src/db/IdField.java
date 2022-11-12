package db;

final public class IdField extends DbField {
	@Override
	void sql_appendUpdateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getInt(this));
	}
	@Override
	public void sql_createField(StringBuilder sb) {
		sb.append(dbname).append(" int primary key auto_increment");
	}
}