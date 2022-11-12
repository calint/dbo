package db;

import java.util.Map;

final class ForeignKeyField extends DbField {
	@Override
	void sql_updateValue(final StringBuilder sb,final DbObject o) {
		sb.append(o.getLong(this));
	}

	@Override
	void sql_createField(final StringBuilder sb) {
		sb.append(dbname).append(" int");
	}

	@Override
	void initDefaultValue(final Map<DbField, Object> kvm) {
	}

}