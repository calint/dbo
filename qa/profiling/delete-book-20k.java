import java.util.List;

import db.Db;
import db.DbObject;
import db.DbTransaction;
import main.Book;

// delete all books (cascading delete of aggregated DataText)
class ReqThread extends Thread {
	@Override
	public void run() {
		DbTransaction tn;
		try {
			tn = Db.initCurrentTransaction();
		} catch (Throwable t1) {
			throw new RuntimeException(t1);
		}
		try {
//---- - - --- -- -------- -- - - -- -- -- -- - -- --- - --- --- - -- -- -- --
			List<DbObject> ls = tn.get(Book.class, null, null, null);
			for (final DbObject o : ls) {
				tn.delete(o);
			}
//---- - - --- -- -------- -- - - -- -- -- -- - -- --- - --- --- - -- -- -- --
			tn.commit();
		} catch (Throwable t1) {
			try {
				tn.rollback();
			} catch (Throwable t2) {
				t2.printStackTrace();
			}
			throw new RuntimeException(t1);
		} finally {
			Db.deinitCurrentTransaction();
		}
	}
}
