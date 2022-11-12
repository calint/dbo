package db.main;

import db.Db;
import db.DbTransaction;
import db.File;
import db.User;

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
//			User u = (User) t.create(User.class);
			User u = new User();
			File f = u.createFile();
			f.setName("file1");
			u.setName("hello 'name' name");
			u.setNLogins(1);
			u.update();
//			u.delete();
			////////////////////////////////////////////////////////////
			t.commit();
			System.out.println(t);
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