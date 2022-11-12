package db.main;

import db.Db;
import db.File;
import db.User;

public class Main {
	public static final void main(String[] args)throws Throwable{		
		Db.instance().register(User.class);
		Db.instance().register(File.class);
		Db.instance().initConnectionPool("jdbc:mysql://localhost:3306/testdb","c","password",5);
		
		
		Thread t1=new ReqThread();
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
