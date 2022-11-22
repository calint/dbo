package main;

import db.Db;
import db.test.Book;
import db.test.DataBinary;
import db.test.DataText;
import db.test.File;
import db.test.Game;
import db.test.TestCase;
import db.test.User;
import db.test.fulltext_search_books;
import db.test.import_books_sample;

public final class Main {
	private static void run(Class<? extends TestCase> cls) throws Throwable {
		final TestCase r = cls.getConstructor().newInstance();
		final Thread t = new Thread(r);
		t.start();
		t.join();
	}

//	@SuppressWarnings("unchecked")
//	public static Class<? extends DbObject>[] getClasses() {
//		final Class<?>[] a = new Class<?>[] { User.class, File.class, DataBinary.class, DataText.class, Book.class,
//				Game.class };
//		return (Class<? extends DbObject>[]) a;
//	}

	public static final void main(String[] args) throws Throwable {
		Db.initInstance();
		Db db = Db.instance();
		db.register(User.class);
		db.register(File.class);
		db.register(DataBinary.class);
		db.register(DataText.class);
		db.register(Book.class);
		db.register(Game.class);

//		final Class<? extends DbObject>[] clsa = getClasses();
//		for (int i = 0; i < clsa.length; i++) {
//			db.register(clsa[i]);
//		}

//		db.init("jdbc:mysql://localhost:3306/testdb", "c", "password", 5);
//		db.init("jdbc:mysql://localhost:3306/testdb?autoReconnect=true&useSSL=false", "c", "password", 5);
		db.init("jdbc:mysql://localhost:3306/testdb?useSSL=false", "c", "password", 10);
//		db.init("jdbc:mysql://localhost:3306/testdb?verifyServerCertificate=false&useSSL=true", "c", "password", 5);

//		Db.log_enable = false;

//		run(fulltext_search_books.class);
//		run(import_games.class);
//		run(test.class);
//		run(delete_books.class);
//		run(print_column_types.class);

//		final Thread t1 = new Thread(new import_books_sample());
//		final Thread t2 = new Thread(new import_books_sample());
//		t1.start();
//		t2.start();
//		t1.join();
//		t2.join();

//		final Thread t1 = new Thread(new import_books());
//		final Thread t2 = new Thread(new import_books());
//		t1.start();
//		t2.start();
//		t1.join();
//		t2.join();

//		run(test1.class);
		run(import_books_sample.class);
		run(fulltext_search_books.class);
//		run(import_books.class);
//		run(jdbc_select_books.class);
//		run(get_books.class);

		db.shutdown();
	}
}