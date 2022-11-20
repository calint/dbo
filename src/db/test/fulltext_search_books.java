package db.test;

import java.util.List;

import db.Db;
import db.DbObject;
import db.DbTransaction;
import db.Limit;
import db.Query;

public class fulltext_search_books extends DbRunnable {
	@Override
	public void doRun() {
		final DbTransaction tn = Db.currentTransaction();
		List<DbObject> ls;
		Query q;
		Limit l;

		q = new Query(Book.ft, "+guide +ultimate -training");
		l = new Limit(0, 25);
		ls = tn.get(Book.class, q, null, l);
		System.out.println("results: " + ls.size());
		for (final DbObject o : ls) {
			System.out.println(o);
		}
	}
}