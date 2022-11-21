package main;

import db.Db;
import db.test.Book;
import db.test.DataBinary;
import db.test.DataText;
import db.test.File;
import db.test.Game;
import db.test.TestCase;
import db.test.User;
import db.test.count;

public class Main {
	private static void run(Class<? extends TestCase> cls) throws Throwable {
		final TestCase r = cls.getConstructor().newInstance();
		final Thread t = new Thread(r);
		t.start();
		t.join();
	}

	public static final void main(String[] args) throws Throwable {
		Db.initInstance();
		Db db = Db.instance();
		db.register(User.class);
		db.register(File.class);
		db.register(DataBinary.class);
		db.register(DataText.class);
		db.register(Book.class);
		db.register(Game.class);
		db.init("jdbc:mysql://localhost:3306/testdb", "c", "password", 5);

//		run(import_books.class);
//		run(jdbc_select_books.class);
//		run(get_books.class);
//		run(fulltext_search_books.class);
//		run(import_games.class);
//		run(test.class);
//		run(delete_books.class);
//		run(print_column_types.class);
//		run(refn_orphans.class);
		run(count.class);
		
		db.shutdown();
	}
}