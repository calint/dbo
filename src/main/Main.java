package main;

import java.sql.Timestamp;
import java.util.List;

import db.Db;
import db.DbObject;
import db.DbTransaction;
import db.Limit;
import db.Order;
import db.Query;

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

		Thread t1 = new ReqThread();
//		Thread t2=new ReqThread();
//		Thread t3=new ReqThread();
		t1.start();
//		t2.start();
//		t3.start(); 
		t1.join();
//		t2.join();
//		t3.join();

		db.deinitConnectionPool();
	}
}

//class ReqThread extends Thread {
//	@Override
//	public void run() {
//		final DbTransaction tn = Db.initCurrentTransaction();
//		try {
//			// ---- - - --- -- -------- -- - - -- -- -- -- - -- --- - --- --- - -- -- -- --
//			final Statement stmt = tn.getJdbcStatement();
//			final String sql = "select t1.* from User as t1 limit 0,1";
//			System.out.println(sql);
//			final ResultSet rs = stmt.executeQuery(sql);
//			final ResultSetMetaData rsm = rs.getMetaData();
//			final int ncols = rsm.getColumnCount();
//			for (int i = 0; i < ncols; i++) {
//				System.out.println(rsm.getColumnName(i + 1) + " " + rsm.getColumnTypeName(i + 1) + " "
//						+ rsm.getColumnClassName(i + 1));
//			}
//			rs.close();
//			// ---- - - --- -- -------- -- - - -- -- -- -- - -- --- - --- --- - -- -- -- --
//			tn.finishTransaction();
//		} catch (Throwable t1) {
//			tn.rollback();
//			throw new RuntimeException(t1);
//		} finally {
//			Db.deinitCurrentTransaction();
//		}
//	}
//}

//// import-200000-book
//class ReqThread extends Thread {
//	@Override
//	public void run() {
//		final DbTransaction tn = Db.initCurrentTransaction();
//		try {
//			// ---- - - --- -- -------- -- - - -- -- -- -- - -- --- - --- --- - -- -- -- --
//			Db.log_enable = false;
//
//			// sanity check
//			FileReader in = new FileReader("../cvs-samples/books_data.csv");
//			CsvReader csv = new CsvReader(in, ',', '"');
//			List<String> ls = csv.nextRecord();// read headers
//			int i = 2; // skip headers
//			System.out.println("bounds check");
//			while (true) {
//				ls = csv.nextRecord();
//				if (ls == null)
//					break;
//				final String name = ls.get(0);
//				if (name.length() > Book.name.getSize())
//					throw new RuntimeException("record " + i + " has size of name " + name.length()
//							+ " but field length is " + Book.name.getSize());
//
//				final String authors = ls.get(2);
//				if (authors.length() > Book.authors.getSize())
//					throw new RuntimeException("record " + i + " has size of authors " + authors.length()
//							+ " but field length is " + Book.authors.getSize());
//
//				final String publisher = ls.get(5);
//				if (publisher.length() > Book.publisher.getSize())
//					throw new RuntimeException("record " + i + " has size of publisher " + publisher.length()
//							+ " but field length is " + Book.publisher.getSize());
//
//				if (++i % 100 == 0)
//					System.out.println(i);
//			}
//			in.close();
//			System.out.println("bounds check done");
//
//			// import
//			System.out.println("import");
//			in = new FileReader("../cvs-samples/books_data.csv");
//			csv = new CsvReader(in, ',', '"');
//			ls = csv.nextRecord();// read headers
//			i = 2; // skip headers
//			while (true) {
//				ls = csv.nextRecord();
//				if (ls == null)
//					break;
//				final Book o = (Book) tn.create(Book.class);
//				o.setName(ls.get(0));
//				o.setAuthors(ls.get(2));
//				o.setPublisher(ls.get(5));
//				final DataText d = o.getData(true);
//				d.setData(ls.get(1));
//				if (++i % 100 == 0) {
//					System.out.println(i);
//					tn.commit();
//				}
//			}
//			in.close();
//			System.out.println("import done. finnish transaction");
//			// ---- - - --- -- -------- -- - - -- -- -- -- - -- --- - --- --- - -- -- -- --
//			tn.finishTransaction();
//		} catch (Throwable t1) {
//			tn.rollback();
//			throw new RuntimeException(t1);
//		} finally {
//			Db.deinitCurrentTransaction();
//		}
//	}
//}

//// jdbc-select-book-10req-1M
//class ReqThread extends Thread {
//	@Override
//	public void run() {
//		final DbTransaction tn = Db.initCurrentTransaction();
//		try {
//			// ---- - - --- -- -------- -- - - -- -- -- -- - -- --- - --- --- - -- -- -- --
//			final Statement stmt = tn.getJdbcStatement();
//			final int nreq = 10;
//			int i = 0;
//			final String sql = "select t1.* from Book as t1 limit 0,1000000";
//			while (true) {
//				System.out.println(sql);
//				final ResultSet rs = stmt.executeQuery(sql);
//				while (rs.next()) {
//					final String desc = rs.getString(2);
////					System.out.println(desc);
//				}
//				rs.close();
//				i++;
//				System.out.println("requests: " + i);
//				if (i == nreq)
//					break;
//			}
//			// ---- - - --- -- -------- -- - - -- -- -- -- - -- --- - --- --- - -- -- -- --
//			tn.finishTransaction();
//		} catch (Throwable t1) {
//			tn.rollback();
//			throw new RuntimeException(t1);
//		} finally {
//			Db.deinitCurrentTransaction();
//		}
//	}
//}

//// get-book-10req-1M
//class ReqThread extends Thread {
//	@Override
//	public void run() {
//		final DbTransaction tn = Db.initCurrentTransaction();
//		try {
//			// ---- - - --- -- -------- -- - - -- -- -- -- - -- --- - --- --- - -- -- -- --
//			tn.cache_enabled = false;
//			final int nreq = 10;
//			int i = 0;
//			while (true) {
//				final List<DbObject> ls = tn.get(Book.class, null, null, new Limit(0, 1000000));
////				for (final DbObject o : ls) {
////					final Book bo = (Book) o;
////					System.out.println(bo.getName());
////				}
//				System.out.println("objects retrieved: " + ls.size());
//				i++;
//				System.out.println("requests: " + i);
//				if (i == nreq)
//					break;
//			}
//			// ---- - - --- -- -------- -- - - -- -- -- -- - -- --- - --- --- - -- -- -- --
//			tn.finishTransaction();
//		} catch (Throwable t1) {
//			tn.rollback();
//			throw new RuntimeException(t1);
//		} finally {
//			Db.deinitCurrentTransaction();
//		}
//	}
//}

// import games
//class ReqThread extends Thread {
//	@Override
//	public void run() {
//		final DbTransaction tn = Db.initCurrentTransaction();
//		try {
//			////////////////////////////////////////////////////////////
//			// import batch, disable cache
//			Db.currentTransaction().cache_enabled = false;
////			Db.log_enable = false;
//
//			final FileReader in = new FileReader(new java.io.File("/home/c/Downloads/steam-games.csv"));
////			final FileReader in = new FileReader(new java.io.File("/home/c/Downloads/prob.csv"));
//			final CsvReader csv = new CsvReader(in, ';', '"');
//			List<String> ls = csv.nextRecord();// read headers
////			System.out.println(ls);
//			int i = 0;
//			while (true) {
//				ls = csv.nextRecord();
//				if (ls == null)
//					break;
////				System.out.println(ls);
//				final Game o = (Game) tn.create(Game.class);
//				o.setName(ls.get(1));
//				o.setDescription(ls.get(2));
//				if (i++ % 100 == 0)
//					System.out.println(i);
////				tn.commit();
//			}
//			in.close();
//			////////////////////////////////////////////////////////////
//			tn.finishTransaction();
//		} catch (Throwable t1) {
//			tn.rollback();
//			throw new RuntimeException(t1);
//		} finally {
//			Db.deinitCurrentTransaction();
//		}
//	}
//}

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
			////////////////////////////////////////////////////////////
//			Db.currentTransaction().cache_enabled = false;
			File f;
			int id = 0;
			List<DbObject> ls;
			Query q;

			User u = (User) tn.create(User.class);
			u.setName("hello 'name' name"); // ! bug generated: update User set name='hello
			u.setFlt(1.2f);
			u.setDbl(1.2);
			u.setBool(true);
			q = new Query(User.bool, Query.EQ, true);
			ls = tn.get(User.class, q, null, null);
			for (final DbObject o : ls) {
				System.out.println(o);
			}
			q = new Query(User.ixFt, "name");
			ls = tn.get(User.class, q, null, null);
			for (final DbObject o : ls) {
				System.out.println(o);
			}
			q = new Query(User.class, 1).and(User.ixFt, "name");
			ls = tn.get(User.class, q, null, null);
			for (final DbObject o : ls) {
				System.out.println(o);
			}

			q = new Query(User.birthTime, Query.GT, Timestamp.valueOf("1970-01-01 00:00:00"));
			ls = tn.get(User.class, q, null, null);
			for (final DbObject o : ls) {
				System.out.println(o);
			}

			f = u.createFile();
			f.setName("user file 1");
			f.setCreatedTs(Timestamp.valueOf("2022-11-14 02:27:12"));
			u.deleteFile(f.id()); // ? relation should check that file id belongs to object?

			f = u.createFile();
			f.setName("user file 11");
			f.setCreatedTs(Timestamp.valueOf("2022-11-14 02:27:12"));

//			final List<File> fls = u.getFiles(null, null, null);
//			for (final File o : fls) {
//				System.out.println(o);
//			}

			f = (File) tn.create(File.class);
			f.setName("user refs this file");
			u.addRefFile(f.id());

			f = (File) tn.create(File.class);
			f.setName("file 3");
			u.addRefFile(f.id());

			f = (File) tn.create(File.class);
			f.setName("stand alone file");
			u.addRefFile(f.id());

//			u.deleteFile(f.id()); // causes exception

//			ls = u.getRefFiles(new Query(File.name, Query.EQ, "user file 2"), null, null);
//			for (final DbObject o : ls) {
//				final File fo = (File) o;
//				System.out.println(fo);
//			}

			f = u.getProfilePic(true);
			f.setName("profile pic");

//			u.deleteFromDb();

//			u.removeRefFile(f);

			f = u.getProfilePic(false);
			f = u.getProfilePic(true);
			id = u.getProfilePicId();
			File ff = u.getProfilePic(false);
			u.addRefFile(f.id());
			u.deleteProfilePic(); // ? bug! this deletes the file but there is orphan entry in RefN table
			f.setName("profile pic");
			id = u.getProfilePicId();
			ff = u.getProfilePic(false);

			f = (File) tn.create(File.class);
			f.setName("a standalone file");
			id = u.getGroupPicId();
			u.setGroupPic(f.id());
			u.setGroupPic(0);
			u.setGroupPic(f.id());
			ff = u.getGroupPic();
			if (f != ff)
				throw new RuntimeException("cache issue: " + f + " is not same instance as " + ff);
			id = u.getGroupPicId();
//			u.setGroupPic(0);
			tn.delete(f);
//			f.deleteFromDb(); // ? groupPicId now refers to a deleted object

			DataBinary d;

			d = (DataBinary) f.getData(true);
			d.setData(new byte[] { 0, 1, 2, 1 });

			d = (DataBinary) tn.create(DataBinary.class);
			d.setData(new byte[] { 0, 0xa, 0xb, 0xc });

			u.setNLogins(3);

			final Query qry = new Query(User.class, 1).and(User.refFiles).and(File.name, Query.LIKE, "user refs %");
//			System.out.println(qry.toString());
//			final Query qry = new Query(User.class, 1).and(User.files);
//			final Query qry = new Query(User.class, 1).and(User.profilePic);
//			final Query qry = new Query(User.class, 1).and(User.groupPic);
//			final Query qry = new Query(File.created_ts, Query.GTE, Timestamp.valueOf("2022-11-14 00:00:00"));

//			final Order ord = null;
			final Order ord = new Order(File.created_ts, false);
//			final Order ord = new Order(File.name, false);
//			final Order ord = new Order(File.class);

			final Limit lmt = null;
//			final Limit lmt = new Limit(0, 2);
			ls = tn.get(File.class, qry, ord, lmt);
//			final List<DbObject> ls = t.get(File.class, null, null, null);
			for (final DbObject o : ls) {
				final File fo = (File) o;
//				Timestamp ts = fo.getCreatedTs();
//				if (ts != null)
//					System.out.println(o.getId() + " " + ts);
				System.out.println(fo);
			}

			final Query qry2 = new Query(User.flt, Query.EQ, 1.2f); // ? does not find user. >= works
			ls = tn.get(User.class, qry2, null, null);
			for (final DbObject o : ls) {
				final User uo = (User) o;
				System.out.println("  flt=" + uo.getFlt());
			}

			final Query qry3 = new Query(User.dbl, Query.EQ, 1.2);
			ls = tn.get(User.class, qry3, null, null);
			for (final DbObject o : ls) {
				final User uo = (User) o;
				System.out.println("  dbl=" + uo.getDbl());
			}

//			tn.delete(u);
//			
//			for (DbObject o : Db.currentTransaction().get(File.class, null, null, null)) {
//				o.deleteFromDb();
//			}
//
//			for (DbObject o : Db.currentTransaction().get(Data.class, null, null, null)) {
//				o.deleteFromDb();
//			}

			////////////////////////////////////////////////////////////
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