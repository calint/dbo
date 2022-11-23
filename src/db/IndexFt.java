package db;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

public final class IndexFt extends Index {
	public IndexFt(final DbField... fld) {
		super(fld);
	}

	@Override
	void createIndex(final Statement stmt, final DatabaseMetaData dbm) throws Throwable {
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
		sb.append("create fulltext index ").append(name).append(" on ").append(tableName).append('(');
		for (final DbField f : fields) {
			sb.append(f.name).append(',');
		}
		sb.setLength(sb.length() - 1);
		sb.append(')');

		final String sql = sb.toString();
		Db.log(sql);
		stmt.execute(sql);
	}
}
