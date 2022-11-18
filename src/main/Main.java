package main;

import java.io.FileReader;
import java.util.List;

import csv.CsvReader;
import db.Db;
import db.DbObject;
import db.DbTransaction;

public class Main {
	public static final void main(String[] args) throws Throwable {
		Db.initInstance();
		Db.instance().register(User.class);
		Db.instance().register(File.class);
		Db.instance().register(Data.class);
		Db.instance().register(Book.class);
		Db.instance().register(Game.class);
		Db.instance().init("jdbc:mysql://localhost:3306/testdb", "c", "password", 5);

		Thread t1 = new ReqThread();
//		Thread t2=new ReqThread();
//		Thread t3=new ReqThread();
		t1.start();
//		t2.start();
//		t3.start(); 
		t1.join();
//		t2.join();
//		t3.join();

		Db.instance().deinitConnectionPool();
	}
}

class ReqThread extends Thread {
	@Override
	public void run() {
		final DbTransaction tn = Db.initCurrentTransaction();
		try {
			////////////////////////////////////////////////////////////
			// import batch, disable cache
			Db.currentTransaction().cache_enabled = false;
//			Db.log_enable = false;

			int i = 0;
			while (true) {
				List<DbObject> ls = tn.get(Game.class, null, null, null);
				System.out.println(ls.size());
				if (i++ % 100 == 0)
					System.out.println(i);
				if (i == 1000)
					break;
			}
			////////////////////////////////////////////////////////////
			tn.finishTransaction();
		} catch (Throwable t1) {
			tn.rollback();
			throw new RuntimeException(t1);
		} finally {
			Db.deinitCurrentTransaction();
		}
	}

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

//	@Override
//	public void run() {
//		final DbTransaction tn = Db.initCurrentTransaction();
//		try {
//			////////////////////////////////////////////////////////////
//			// import batch, disable cache
//			Db.currentTransaction().cache_enabled = false;
////			Db.log_enable = false;
//
//			final FileReader in=new FileReader(new java.io.File("/home/c/Downloads/sample-books.csv"));
//			final CsvReader csv = new CsvReader(in);
//			List<String> ls = csv.nextRecord();// read headers
////			System.out.println(ls);
////			int i = 0;
//			while (true) {
//				ls = csv.nextRecord();
//				if (ls == null)
//					break;
////				System.out.println(ls);
//				Book book = (Book) tn.create(Book.class);
//				book.setName(ls.get(1));
//				book.setDescription(ls.get(8));
////				if (i++ % 100 == 0)
////					System.out.println(i);
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

	//
//	@Override
//	public void run() {
//		DbTransaction tn;
//		try {
//			tn = Db.initCurrentTransaction();
//		} catch (Throwable t1) {
//			throw new RuntimeException(t1);
//		}
//		try {
//			////////////////////////////////////////////////////////////
////			Db.currentTransaction().cache_enabled = false;
//			File f;
//			int id = 0;
//			List<DbObject> ls;
//
//			User u = (User) tn.create(User.class);
//			u.setName("hello 'name' name");
//
//			f = u.createFile();
//			f.setName("user file 1");
//			f.setCreatedTs(Timestamp.valueOf("2022-11-14 02:27:12"));
//			u.deleteFile(f.id()); // ? relation should check that file id belongs to object?
//
//			f = u.createFile();
//			f.setName("user file 11");
//			f.setCreatedTs(Timestamp.valueOf("2022-11-14 02:27:12"));
//
////			final List<File> fls = u.getFiles(null, null, null);
////			for (final File o : fls) {
////				System.out.println(o);
////			}
//
//			f = (File) tn.create(File.class);
//			f.setName("user refs this file");
//			u.addRefFile(f.id());
//
//			f = (File) tn.create(File.class);
//			f.setName("file 3");
//			u.addRefFile(f.id());
//
//			f = (File) tn.create(File.class);
//			f.setName("stand alone file");
//			u.addRefFile(f.id());
//
////			u.deleteFile(f.id()); // causes exception
//			
////			ls = u.getRefFiles(new Query(File.name, Query.EQ, "user file 2"), null, null);
////			for (final DbObject o : ls) {
////				final File fo = (File) o;
////				System.out.println(fo);
////			}
//
//			f = u.getProfilePic(true);
//			f.setName("profile pic");
//
////			u.deleteFromDb();
//
////			u.removeRefFile(f);
//
//			f = u.getProfilePic(false);
//			f = u.getProfilePic(true);
//			id = u.getProfilePicId();
//			File ff = u.getProfilePic(false);
//			u.addRefFile(f.id());
//			u.deleteProfilePic(); // ? bug! this deletes the file but there is orphan entry in RefN table
//			f.setName("profile pic");
//			id = u.getProfilePicId();
//			ff = u.getProfilePic(false);
//
//			f = (File) tn.create(File.class);
//			f.setName("a standalone file");
//			id = u.getGroupPicId();
//			u.setGroupPic(f.id());
//			u.setGroupPic(0);
//			u.setGroupPic(f.id());
//			ff = u.getGroupPic();
//			if (f != ff)
//				throw new RuntimeException("cache issue: " + f + " is not same instance as " + ff);
//			id = u.getGroupPicId();
////			u.setGroupPic(0);
//			tn.delete(f);
////			f.deleteFromDb(); // ? groupPicId now refers to a deleted object
//			
//			Data d;
//			
//			d = (Data) f.getData(true);
//			d.setData(new byte[] { 0, 1, 2, 1 });
//
//			d = (Data) tn.create(Data.class);
//			d.setData(new byte[] { 0, 0xa, 0xb, 0xc });
//
//			u.setNLogins(3);
//
//			final Query qry = new Query(User.class, 1).and(User.refFiles).and(File.name, Query.LIKE, "user file %");
////			System.out.println(qry.toString());
////			final Query qry = new Query(User.class, 1).and(User.files);
////			final Query qry = new Query(User.class, 1).and(User.profilePic);
////			final Query qry = new Query(User.class, 1).and(User.groupPic);
////			final Query qry = new Query(File.created_ts, Query.GTE, Timestamp.valueOf("2022-11-14 00:00:00"));
//
////			final Order ord = null;
//			final Order ord = new Order(File.created_ts, false);
////			final Order ord = new Order(File.name, false);
////			final Order ord = new Order(File.class);
//
//			final Limit lmt = null;
////			final Limit lmt = new Limit(0, 2);
//			ls = tn.get(File.class, qry, ord, lmt);
////			final List<DbObject> ls = t.get(File.class, null, null, null);
//			for (final DbObject o : ls) {
//				final File fo = (File) o;
////				Timestamp ts = fo.getCreatedTs();
////				if (ts != null)
////					System.out.println(o.getId() + " " + ts);
//				System.out.println(fo);
//			}
//
////			u.deleteFromDb();
////			
////			for (DbObject o : Db.currentTransaction().get(File.class, null, null, null)) {
////				o.deleteFromDb();
////			}
////
////			for (DbObject o : Db.currentTransaction().get(Data.class, null, null, null)) {
////				o.deleteFromDb();
////			}
//
//			////////////////////////////////////////////////////////////
//			tn.commit();
//		} catch (Throwable t1) {
//			try {
//				tn.rollback();
//			} catch (Throwable t2) {
//				t2.printStackTrace();
//			}
//			throw new RuntimeException(t1);
//		} finally {
//			Db.deinitCurrentTransaction();
//		}
//	}
}
