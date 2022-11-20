package main;

import db.Db;
import db.test.Book;
import db.test.DataBinary;
import db.test.DataText;
import db.test.File;
import db.test.Game;
import db.test.User;
import db.test.print_column_types;

public class Main {
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

//		final Thread t = new Thread(new import_books());
//		final Thread t = new Thread(new jdbc_select_books());
//		final Thread t = new Thread(new get_books());
//		final Thread t = new Thread(new fulltext_search_books());
//		final Thread t = new Thread(new delete_books());
//		final Thread t = new Thread(new import_games());
//		final Thread t = new Thread(new test());
		final Thread t = new Thread(new print_column_types());
		t.start();
		t.join();

		db.deinitConnectionPool();
	}
}