package db;

import java.util.Map;

public final class FldId extends DbField {
	@Override
	void sql_updateValue(final StringBuilder sb, final DbObject o) {
		sb.append(o.getInt(this));
	}

	@Override
	void sql_createColumn(final StringBuilder sb) {
		sb.append(columnName).append(" int primary key auto_increment");
	}

	@Override
	void initDefaultValue(final Map<DbField, Object> kvm) {
	}

}