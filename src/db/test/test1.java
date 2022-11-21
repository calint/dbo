package db.test;

import db.Db;
import db.DbTransaction;
import db.Query;

public class test1 extends TestCase {
	@Override
	public void doRun() throws Throwable {
		final DbTransaction tn = Db.currentTransaction();

		final User u1 = (User) tn.create(User.class);
		u1.setName("user name");
		tn.create(User.class);
		tn.create(User.class);
		int n;
		n = tn.getCount(User.class, new Query(User.name, Query.EQ, "user name"));
		if (n != 1)
			throw new RuntimeException("expected 1. got " + n);

		n = tn.getCount(User.class, null);
		if (n != 3)
			throw new RuntimeException("expected 3" + " got " + n);

		final File f1 = u1.createFile();
		final File f2 = u1.createFile();
		n = u1.getFilesCount(null);
		if (n != 2)
			throw new RuntimeException("expected 2" + " got " + n);

		f1.setName("user file");
		n = tn.getCount(File.class, new Query(File.name, Query.EQ, "user file"));
		if (n != 1)
			throw new RuntimeException("expected 1. got " + n);

		final Query q = new Query(User.name, Query.EQ, "user name").and(User.files).and(File.name, Query.EQ,
				"user file");
		n = tn.getCount(File.class, q);
		if (n != 1)
			throw new RuntimeException("expected 1. got " + n);

		tn.delete(f1);
		n = u1.getFilesCount(null);
		if (n != 1)
			throw new RuntimeException("expected 2" + " got " + n);

		u1.deleteFile(f2.id());
		n = u1.getFilesCount(null);
		if (n != 0)
			throw new RuntimeException("expected 0" + " got " + n);

		final File f3 = (File) tn.create(File.class);
		n = u1.getRefFilesCount(null);
		if (n != 0)
			throw new RuntimeException("expected 0" + " got " + n);

		u1.addRefFile(f3.id());
		n = u1.getRefFilesCount(null);
		if (n != 1)
			throw new RuntimeException("expected 1" + " got " + n);

		tn.delete(f3);
		n = u1.getRefFilesCount(null);
		if (n != 0)
			throw new RuntimeException("expected 0" + " got " + n);

//		// cleanup
//		tn.delete(u1);
//		tn.delete(u2);
//		tn.delete(u3);
	}
}