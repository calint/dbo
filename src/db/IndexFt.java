package db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public final class IndexFt extends Index {
	public IndexFt(final DbField... fld) {
		super(fld);
	}

	@Override
	void sql_createIndex(final StringBuilder sb, final DatabaseMetaData dbm) throws Throwable {
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

		sb.append("create fulltext index ").append(name).append(" on ").append(tableName).append('(');
		for (final DbField f : fields) {
			sb.append(f.columnName).append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append(')');
	}
}
