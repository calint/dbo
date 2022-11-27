package main;

import db.Db;
import db.test.Book;
import db.test.DataBinary;
import db.test.DataText;
import db.test.File;
import db.test.Game;
import db.test.TestCase;
import db.test.TestObj;
import db.test.User;
import db.test.import_books;

public final class Main {
	public static final void main(String[] args) throws Throwable {
		Class.forName("com.mysql.jdbc.Driver"); // ? necessary in java 1.5
		Db.initInstance();
		Db db = Db.instance();
//		db.update_referring = false;

		db.register(User.class);
		db.register(File.class);
		db.register(DataBinary.class);
		db.register(DataText.class);
		db.register(Book.class);
		db.register(Game.class);
		db.register(TestObj.class);

		if (tryJemCall(db, args))
			return;

//		db.init("jdbc:mysql://localhost:3306/testdb", "c", "password", 5);
		db.init("jdbc:mysql://localhost:3306/testdb?allowPublicKeyRetrieval=true&useSSL=false", "c", "password", 10);

		Db.log_enable = false;
//		run(test1.class);
//		run(test2.class);
//		run(import_books_sample.class);
		run(import_books.class);
//		run(fulltext_search_books.class);
//		run(get_books.class);
//		run(jdbc_select_books.class);
//		run(print_column_types.class);

		db.shutdown();
	}

	private static void run(Class<? extends TestCase> cls) throws Throwable {
		final TestCase r = cls.getConstructor().newInstance();
//		final Thread t = new Thread(r);
//		t.start();
//		t.join();
		r.run();
	}

	private static boolean tryJemCall(Db db, String[] args) throws Throwable {
		// ? so ugly
		boolean exit = false;
		int i = 0;
		while (true) {
			if (i == args.length)
				break;
			if (args[i].equals("-j")) {
				if (!(args.length > i + 1))
					throw new IllegalArgumentException("expected a java class name after option -j at argument " + i);
				i++;
				jem.Main.main(db, args[i]);
				exit = true;
			}
			i++;
		}
		return exit;
	}
}