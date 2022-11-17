package db;

/** field/column that refers to an id that may be null */
final class FldRel extends DbField {
	@Override
	void sql_updateValue(final StringBuilder sb, final DbObject o) {
		final int id = o.getInt(this);
		if (id == 0) {
			sb.append("null");
			return;
		}
		sb.append(id);
	}

	@Override
	void sql_createColumn(final StringBuilder sb) {
		sb.append(columnName).append(" int");
	}

}