package db;

/** field/column that refers to an id in a different class/table */
final class FldRel extends DbField {
	@Override
	void sql_updateValue(final StringBuilder sb, final DbObject o) {
		sb.append(o.getInt(this));
	}

	@Override
	void sql_createColumn(final StringBuilder sb) {
		sb.append(columnName).append(" int");
	}

}