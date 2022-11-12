package db;

final public class StringField extends DbField {
	@Override
	void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append('\'').append(o.getStr(this).replace("'","''")).append('\'');
	}
	@Override
	void sql_createField(StringBuilder sb) {
		sb.append(dbname).append(" varchar(255)");
	}
}