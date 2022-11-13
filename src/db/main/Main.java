package db.main;

import java.util.List;

import db.Db;
import db.DbObject;
import db.DbTransaction;
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
			File fp = u.createProfilePic();
			File fg = (File) t.create(File.class);
			u.setGroupPic(fg);
			fp.setName("profile pic");
//			if(1==1)throw new RuntimeException();
//			u.setName("hello 'name' name");
			u.setNLogins(3);
			f.setName("file1");
			u.addRefFile(f);
			u.addRefFile(fg);
			t.flush();
			final Query q = new Query().append(File.name, Query.OP_EQ, "'file1'").and(File.name, Query.OP_EQ, "'file1'");
			final List<DbObject> ls = t.get(File.class, q);
			for (final DbObject o : ls)
				System.out.println(o);
			for (final DbObject o : t.get(User.class, null))
				System.out.println(o);
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
