package db.test;

import java.util.List;

import db.Db;
import db.DbObject;
import db.DbTransaction;
import db.Limit;
import db.Query;

//// get-book-10req-1M
public class fulltext_search_books extends TestCase {
	@Override
	protected boolean isResetDatabase() {
		return false;
	}

	@Override
	protected boolean isRunWithoutCache() {
		return false;
	}

	@Override
	public void doRun() throws Throwable {
		final DbTransaction tn = Db.currentTransaction();
		final int nreq = 1;
		int i = 0;
		final String qstr = "+whispers +spinning";
		final Query qry = new Query(DataText.ft, qstr).and(Book.data);
		final Limit lmt = new Limit(0, 20);
		final int totalcount = tn.getCount(Book.class, null);
		System.out.println("  searchable books: " + totalcount);
		while (true) {
			System.out.println("   searching '" + qstr + "'");
			final int count = tn.getCount(Book.class, qry);
			System.out.println("      found " + count);
			final List<DbObject> ls = tn.get(Book.class, qry, null, lmt);
			for (final DbObject o : ls) {
				final Book bo = (Book) o;
				System.out.println(bo.id() + "  " + bo.getName());
			}
			System.out.println("  objects retrieved: " + ls.size());
			i++;
			System.out.println("requests: " + i);
			if (i == nreq)
				break;
		}
	}
}