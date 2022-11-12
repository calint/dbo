package db;

import java.util.Map;

final class ForeignKeyField extends DbField {
	@Override
	void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append(o.getLong(this));
	}

	@Override
	void sql_createField(StringBuilder sb) {
		sb.append(dbname).append(" int");
	}

	@Override
	void initDefaultValue(Map<DbField, Object> kvm) {
	}

}