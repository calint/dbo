import java.sql.ResultSet;
import java.sql.Statement;

import db.Db;
import db.DbTransaction;

// jdbc-select-book-10req-1M
//	selects 1M books 10 times
//	compare with get-book-10req-1M-cacheon
class ReqThread extends Thread {
	@Override
	public void run() {
		final DbTransaction tn = Db.initCurrentTransaction();
		try {
// ---- - - --- -- -------- -- - - -- -- -- -- - -- --- - --- --- - -- -- -- --
			final Statement stmt = tn.getJdbcStatement();
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
// ---- - - --- -- -------- -- - - -- -- -- -- - -- --- - --- --- - -- -- -- --
			tn.finishTransaction();
		} catch (Throwable t1) {
			tn.rollback();
			throw new RuntimeException(t1);
		} finally {
			Db.deinitCurrentTransaction();
		}
	}
}
