package db.test;

import java.sql.ResultSet;
import java.sql.Statement;

import db.Db;

//// jdbc-select-book-10req-1M
public class jdbc_select_books extends DbRunnable {
	@Override
	public void doRun() throws Throwable {
		final Statement stmt = Db.currentTransaction().getJdbcStatement();
		final int nreq = 10;
		int i = 0;
		final String sql = "select t1.* from Book as t1 limit 0,1000000";
		while (true) {
			System.out.println(sql);
			final ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				final String desc = rs.getString(2);
//					System.out.println(desc);
			}
			rs.close();
			i++;
			System.out.println("requests: " + i);
			if (i == nreq)
				break;
		}
	}
}