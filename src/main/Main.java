package main;

import db.Db;
import db.test.Book;
import db.test.DataBinary;
import db.test.DataText;
import db.test.File;
import db.test.Game;
import db.test.TestObj;
import db.test.User;
import db.test.fulltext_search_books;
import db.test.import_books;
import db.test.import_games;
import db.test.test1;
import db.test.test2;

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

		if (tryJemCall(args))
			return;

//		db.init("jdbc:mysql://localhost:3306/testdb", "c", "password", 5);
		db.init("jdbc:mysql://localhost:3306/testdb?allowPublicKeyRetrieval=true&useSSL=false", "c", "password", 10);

//		Db.enable_log = false;

		new test1().run();
		new test2().run();
		new import_books().run();
//		new import_books("../csv-samples/books_data.csv").run();
		new fulltext_search_books().run();
		new import_games().run();
//		new import_games("../csv-samples/steam-games.csv").run();

//		run(import_books_sample.class);
//		run(import_books.class);
//		run(fulltext_search_books.class);
//		run(get_books.class);
//		run(jdbc_select_books.class);
//		run(print_column_types.class);
//		run(import_games_sample.class);
//		run(import_games.class);

		db.shutdown();
	}

//	private static void run(Class<? extends TestCase> cls) throws Throwable {
//		final TestCase r = cls.getConstructor().newInstance();
////		final Thread t = new Thread(r);
////		t.start();
////		t.join();
//		r.run();
//	}

	private static boolean tryJemCall(String[] args) throws Throwable {
		// ? so ugly
		boolean exit = false;
		int i = 0;
		while (true) {
			if (i == args.length)
				break;
			if (args[i].equals("-j")) {
				i++;
				if (!(args.length > i))
					throw new IllegalArgumentException(
							"expected a java class name after option -j at argument " + (i + 1));
				jem.Main.main(args[i]);
				exit = true;
			}
			i++;
		}
		return exit;
	}
}