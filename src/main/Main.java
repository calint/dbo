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
		Db.instance().register(User.class);
		Db.instance().register(File.class);
		Db.instance().register(Data.class);
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
		DbTransaction t;
		try {
			t = Db.initCurrentTransaction();
		} catch (Throwable t1) {
			throw new RuntimeException(t1);
		}
		try {
			////////////////////////////////////////////////////////////
			User u = (User) t.create(User.class);
			u.setName("hello 'name' name");

			File f;
			int id = 0;
			f = u.createFile();
			f.setName("user file 1");
			f.setCreatedTs(Timestamp.valueOf("2022-11-14 02:27:12"));
			u.deleteFile(f.getId()); // ? relation should check that file id belongs to object?

			f = (File) t.create(File.class);
			f.setName("user file 2");
			u.addRefFile(f.getId());

			f = (File) t.create(File.class);
			f.setName("user file 3");
			u.addRefFile(f.getId());
//			u.removeRefFile(f);

			f = u.getProfilePic(false);
			f = u.getProfilePic(true);
			id = u.getProfilePicId();
			File ff = u.getProfilePic(false);
			u.addRefFile(f.getId());
			u.deleteProfilePic(); // ? bug! this deletes the file but it still exists in the RefN table
			f.setName("profile pic");
			id = u.getProfilePicId();
			ff = u.getProfilePic(false);

			f = (File) t.create(File.class);
			f.setName("a standalone file");
			id = u.getGroupPicId();
			u.setGroupPic(f.getId());
			u.setGroupPic(0);
			u.setGroupPic(f.getId());
			ff = u.getGroupPic(); // ? bug! without cache this returns a different instance of file with same id
			id = u.getGroupPicId();
//			if(1==1)throw new RuntimeException();

			Data d = (Data) t.create(Data.class);
			d.setData(new byte[] { 0, 10, 22, 13 });

			d = (Data) f.getData(true);
			d.setData(new byte[] { 0, 1, 2, 1 });

			u.setNLogins(3);

			final Query qry = new Query(User.class, 1).and(User.refFiles).and(File.name, Query.LIKE, "user file %");
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
			final List<DbObject> ls = t.get(File.class, qry, ord, lmt);
//			final List<DbObject> ls = t.get(File.class, null, null, null);
			for (final DbObject o : ls) {
				final File fo = (File) o;
//				Timestamp ts = fo.getCreatedTs();
//				if (ts != null)
//					System.out.println(o.getId() + " " + ts);
				System.out.println(fo);
			}

			////////////////////////////////////////////////////////////
			t.commit();
		} catch (Throwable t1) {
			try {
				t.rollback();
			} catch (Throwable t2) {
				t2.printStackTrace();
			}
			throw new RuntimeException(t1);
		} finally {
			Db.deinitCurrentTransaction();
		}
	}
}
