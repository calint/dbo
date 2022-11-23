package db;

public final class FldId extends DbField {
	@Override
	protected void sql_updateValue(final StringBuilder sb, final DbObject o) {// ? never gets called
		sb.append(o.getInt(this));
	}

	@Override
	protected void sql_columnDefinition(final StringBuilder sb) {
		sb.append(name).append(" integer primary key auto_increment");// ? maybe bigint
	}
}
