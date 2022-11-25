package db;

import java.util.Map;

/** field/column that refers to an id. it may be null or 0 */
final class FldRel extends DbField {
	@Override
	protected String getSqlType() {
		return "int";
	}

	@Override
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {
		final int id = o.getInt(this);
		if (id == 0) {
			sb.append("null");
			return;
		}
		sb.append(id);
	}

	@Override
	protected void sql_columnDefinition(final StringBuilder sb) {
		sb.append(name).append(' ').append(getSqlType());
	}
	
	@Override
	protected void putDefaultValue(final Map<DbField, Object> kvm) {
	}
}
