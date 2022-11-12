package db;

public final class IdField extends DbField {
	@Override
	void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getLong(this));
	}

	@Override
	void sql_createField(StringBuilder sb) {
		sb.append(dbname).append(" int primary key auto_increment");
	}
}