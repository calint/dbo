package main;

import java.util.Map;

import db.DbField;
import db.DbObject;

public class FldDateTime extends DbField {

	@Override
	protected String getSqlType() {
		return "datetime";
	}

	@Override
	protected void sql_updateValue(StringBuilder sb, DbObject o) {
		sb.append('\'').append(o.getTs(this)).append('\'');	
	}

	@Override
	protected void putDefaultValue(Map<DbField, Object> kvm) {
	}

}
