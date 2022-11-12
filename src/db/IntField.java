package db;

public final class IntField extends DbField {
	@Override
	void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getInt(this));
	}

	@Override
	void sql_createField(StringBuilder sb) {
		sb.append(dbname).append(" int");
	}
}