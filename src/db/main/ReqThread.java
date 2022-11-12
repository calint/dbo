package db.main;

import db.Db;
import db.DbTransaction;

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
//			u.setName("hello 'name' name");
			u.setNLogins(3);
			f.setName("file1");
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