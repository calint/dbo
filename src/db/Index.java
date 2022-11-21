package db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

public class Index {
	Class<? extends DbObject> cls;
	String name;
	String tableName;
	final ArrayList<DbField> fields = new ArrayList<DbField>();

	public Index(final DbField... flds) {
		for (final DbField f : flds) {
			fields.add(f);
		}
	}

	void sql_createIndex(final Statement stmt, final DatabaseMetaData dbm) throws Throwable {
		final ResultSet rs = dbm.getIndexInfo(null, null, tableName, false, false);
		boolean found = false;
		while (rs.next()) {
			final String indexName = rs.getString("INDEX_NAME");
			if (indexName.equals(name)) {
				found = true;
				break;
			}
		}
		rs.close();
		if (found == true)
			return;

		final StringBuilder sb = new StringBuilder(128);
		sb.append("create index ").append(name).append(" on ").append(tableName).append('(');
		for (final DbField f : fields) {
			sb.append(f.columnName).append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append(')');

		final String sql = sb.toString();
		Db.log(sql);
		stmt.execute(sql);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(128);
		sb.append(name);// .append(" on ").append(tableName).append('(');
		return sb.toString();
	}
}
