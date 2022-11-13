package db.main;

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
			File f = u.createFile();
			f.setName("user file 1");
			f = u.createFile();
			f.setName("user file 2");
			f = u.createFile();
			f.setName("user file 3");

			File fp = u.createProfilePic();
			File fg = (File) t.create(File.class);
			u.setGroupPic(fg);
			fp.setName("profile pic");
//			if(1==1)throw new RuntimeException();
			u.setName("hello 'name' name");
			u.setNLogins(3);
			u.addRefFile(f);
			u.addRefFile(fg);
			t.flush();

			final Query qry = new Query(User.files);
			qry.and(User.class, Query.EQ, 1);
//			final Order ord = new Order(File.id);
//			final Limit lmt = new Limit(1, 2);
//			final List<DbObject> ls = t.get(File.class, qry, null, null);
			final List<DbObject> ls = t.get(File.class, null, null, null);
//			final List<DbObject> ls = t.get(File.class, qry, ord, lmt);
			for (final DbObject o : ls) {
				System.out.println(o);
			}
//
//			for (final DbObject o : t.get(User.class, new Query(User.nlogins, Query.GT, 1)
//					.and(User.nlogins, Query.LTE, 3).and(User.groupPic, Query.EQ, 3),
//					new Order(User.name).append(User.nlogins, false), null)) {
//				System.out.println(o);
//			}

			////////////////////////////////////////////////////////////
			t.commit();
//			System.out.println(t);
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
