package db;

final class FldForeignKey extends DbField {
	@Override
	void sql_updateValue(final StringBuilder sb, final DbObject o) {
		sb.append(o.getInt(this));
	}

	@Override
	void sql_createColumn(final StringBuilder sb) {
		sb.append(columnName).append(" int");
	}

}