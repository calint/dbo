package db;

final public class StringField extends DbField {
	@Override
	void sql_appendUpdateValue(StringBuilder sb, DbObject o) {
		sb.append('\'').append(o.getStr(this).replace("'","''")).append('\'');
	}
	@Override
	public void sql_createField(StringBuilder sb) {
		sb.append(dbname).append(" varchar(255)");
	}
}