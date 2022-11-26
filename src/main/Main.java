package main;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import db.Db;
import db.DbTransaction;
import db.Query;
import db.test.Book;
import db.test.DataBinary;
import db.test.DataText;
import db.test.File;
import db.test.Game;
import db.test.TestCase;
import db.test.User;
import db.test.test1;

public final class Main {
	private static void run(Class<? extends TestCase> cls) throws Throwable {
		final TestCase r = cls.getConstructor().newInstance();
//		final Thread t = new Thread(r);
//		t.start();
//		t.join();
		r.run();
	}

//	@SuppressWarnings("unchecked")
//	public static Class<? extends DbObject>[] getClasses() {
//		final Class<?>[] a = new Class<?>[] { User.class, File.class, DataBinary.class, DataText.class, Book.class,
//				Game.class };
//		return (Class<? extends DbObject>[]) a;
//	}

	public static final void main(String[] args) throws Throwable {
		Class.forName("com.mysql.jdbc.Driver"); // ? for running in java 1.5
		Db.initInstance();
		Db db = Db.instance();
		db.register(User.class);
		db.register(File.class);
		db.register(DataBinary.class);
		db.register(DataText.class);
		db.register(Book.class);
		db.register(Game.class);
		db.register(TestObj.class);

		if (tryJemCall(db, args))
			return;

//		final Class<? extends DbObject>[] clsa = getClasses();
//		for (int i = 0; i < clsa.length; i++) {
//			db.register(clsa[i]);
//		}

//		db.init("jdbc:mysql://localhost:3306/testdb", "c", "password", 5);
//		db.init("jdbc:mysql://localhost:3306/testdb?autoReconnect=true&useSSL=false", "c", "password", 5);
		db.init("jdbc:mysql://localhost:3306/testdb?allowPublicKeyRetrieval=true&useSSL=false", "c", "password", 10);
//		db.init("jdbc:mysql://localhost:3306/testdb?verifyServerCertificate=false&useSSL=true", "c", "password", 5);

//		System.out.println(JavaCodeEmitter.getSingulariesForPlurar("categories"));

		Db.initCurrentTransaction();
		try {
			/////////////////////////////////////////////
			final DbTransaction tn = Db.currentTransaction();
			final ArrayList<String> ls = new ArrayList<String>();
			ls.add("hello");
			ls.add("world");
			final String chs = "12345678901234567890123456789012";
			final TestObj to = (TestObj) tn.create(TestObj.class);
			final Query qid = new Query(TestObj.class, to.id());
			to.setMd5(chs);
			to.setList(ls);
			Db.currentTransaction().commit();
			final TestObj to2 = (TestObj) tn.get(TestObj.class, qid, null, null).get(0);
			final List<String> ls2 = to2.getList();
			if (ls.size() != ls2.size())
				throw new RuntimeException();
			for (int i = 0; i < ls2.size(); i++) {
				if (!ls.get(i).equals(ls2.get(i)))
					throw new RuntimeException();
			}
			final String s = to.getMd5();
			if (!chs.equals(s))
				throw new RuntimeException();
			tn.commit();
			final TestObj to3 = (TestObj) tn.get(TestObj.class, qid, null, null).get(0);
			to3.setList(null);
			if (to3.getList() != null)
				throw new RuntimeException();
			Db.currentTransaction().commit();
			final TestObj to4 = (TestObj) tn.get(TestObj.class, qid, null, null).get(0);
			if (to4.getList() != null)
				throw new RuntimeException();

			final Timestamp ts = Timestamp.valueOf("2022-11-26 14:07:00");
			to4.setDateTime(ts);
			tn.commit();
			final TestObj to5 = (TestObj) tn.get(TestObj.class, qid, null, null).get(0);
			if (!to5.getDateTime().equals(ts))
				throw new RuntimeException();

			// min value from https://dev.mysql.com/doc/refman/8.0/en/datetime.html
//			final Timestamp ts2 = Timestamp.valueOf("1000-01-01 00:00:00.000000");
			
			final Timestamp ts2 = Timestamp.valueOf("0001-01-01 00:00:00.000000");
			to4.setDateTime(ts2);
			tn.commit();
			final TestObj to6 = (TestObj) tn.get(TestObj.class, qid, null, null).get(0);
			if (!to6.getDateTime().equals(ts2))
				throw new RuntimeException();

			// max value from https://dev.mysql.com/doc/refman/8.0/en/datetime.html
//			final Timestamp ts3 = Timestamp.valueOf("9999-12-31 23:59:59.999999"); // overflows
			
			final Timestamp ts3 = Timestamp.valueOf("9999-12-31 23:59:59");
			to4.setDateTime(ts3);
			tn.commit();
			final TestObj to7 = (TestObj) tn.get(TestObj.class, qid, null, null).get(0);
			if (!to7.getDateTime().equals(ts3))
				throw new RuntimeException();

			
			Db.currentTransaction().delete(to4);
			/////////////////////////////////////////////
			Db.currentTransaction().finishTransaction();
		} catch (Throwable t) {
			Db.currentTransaction().rollback();
			t.printStackTrace();
		} finally {
			Db.deinitCurrentTransaction();
		}

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

//		Db.log_enable = false;
		run(test1.class);
//		run(import_books_sample.class);
//		run(fulltext_search_books.class);
//		run(import_books.class);
//		run(jdbc_select_books.class);
//		run(get_books.class);

		db.shutdown();
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