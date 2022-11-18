import java.util.List;

import db.Db;
import db.DbObject;
import db.DbTransaction;
import db.Limit;
import main.Book;

class ReqThread extends Thread {
	@Override
	public void run() {
		final DbTransaction tn = Db.initCurrentTransaction();
		try {
			// ---- - - --- -- -------- -- - - -- -- -- -- - -- --- - --- --- - -- -- -- --
			tn.cache_enabled = false;
			final int nreq = 10;
			int i = 0;
			while (true) {
				final List<DbObject> ls = tn.get(Book.class, null, null, new Limit(0, 1000000));
//				for (final DbObject o : ls) {
//					final Book bo = (Book) o;
//					System.out.println(bo.getName());
//				}
				System.out.println("objects retrieved: " + ls.size());
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
