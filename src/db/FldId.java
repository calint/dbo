package db;

import java.util.Map;

public final class FldId extends DbField {
	@Override
	void sql_updateValue(final StringBuilder sb,final DbObject o) {
		sb.append(o.getLong(this));
	}

	@Override
	void sql_createField(final StringBuilder sb) {
		sb.append(dbname).append(" int primary key auto_increment");
	}

	@Override
	void initDefaultValue(final Map<DbField, Object> kvm) {
	}

}