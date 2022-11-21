package db.test;

import java.util.List;

import db.Db;
import db.DbObject;
import db.DbTransaction;
import db.Query;

public class test1 extends TestCase {
	@Override
	public void doRun() throws Throwable {
		final DbTransaction tn = Db.currentTransaction();
		tn.cache_enabled = false;

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
			throw new RuntimeException("expected 2 got " + n);

		u1.deleteFile(f2.id());
		n = u1.getFilesCount(null);
		if (n != 0)
			throw new RuntimeException("expected 0 got " + n);

		final File f3 = (File) tn.create(File.class);
		n = u1.getRefFilesCount(null);
		if (n != 0)
			throw new RuntimeException("expected 0 got " + n);

		u1.addRefFile(f3.id());
		n = u1.getRefFilesCount(null);
		if (n != 1)
			throw new RuntimeException("expected 1 got " + n);

		tn.delete(f3);
		n = u1.getRefFilesCount(null);
		if (n != 0)
			throw new RuntimeException("expected 0 got " + n);

		final File ref = u1.getGroupPic();
		if (ref != null)
			throw new RuntimeException("expected null got " + ref);

		final File f4 = (File) tn.create(File.class);
		u1.setGroupPic(f4.id());
		final File ref2 = u1.getGroupPic();
		if (tn.cache_enabled && f2 != f4)
			throw new RuntimeException("expected same instance. is cache off? ");
		if (!tn.cache_enabled && f2 == f4)
			throw new RuntimeException("expected different instances. is cache on?");

		final Book b1 = (Book) tn.create(Book.class);
		final DataText d = b1.getData(false);
		if (d != null)
			throw new RuntimeException("expected null got " + d);

		final DataText dt1 = b1.getData(true);
		dt1.setData("book data fulltext indexed");

		tn.commit(); // mysql does fulltext index after commit

		final List<DbObject> ls1 = tn.get(DataText.class, new Query(DataText.ft, "+fulltext +indexed"), null, null);
		if (ls1.size() != 1)
			throw new RuntimeException("expected 1 results got " + ls1.size());

		final Query q1 = new Query(DataText.ft, "+fulltext -indexed").and(Book.data).and(Book.class, b1.id());
		final List<DbObject> ls2 = tn.get(DataText.class, q1, null, null);
		if (ls2.size() != 0)
			throw new RuntimeException("expected 0 results got " + ls1.size());

		final File f5 = (File) tn.create(File.class);
		final DataBinary bin1 = f5.getData(true);
		final byte[] ba1 = new byte[] { 1, 2, 3, 4, 5 };
		bin1.setData(ba1);

		if (tn.cache_enabled)
			tn.commit(); // flush cache

		final DataBinary bin2 = f5.getData(true);
//		bin1.setData(null); // ? if not set JVM sometimes uses same instance and test fails ...
		if (bin1 == bin2) {
			// note: it seems that JVM reuses instances and bin1 == bin2 might be true
			if (bin1.getData() == bin2.getData()) {
				throw new RuntimeException("didn't expect this");
			}
		}

		final byte[] ba2 = bin2.getData();
		if (ba1.length != ba2.length)
			throw new RuntimeException("expected same length on the arrays");

		for (int i = 0; i < ba1.length; i++) {
			if (ba1[i] != ba2[i]) {
				throw new RuntimeException("gotten byte array does not match set array");
			}
		}
	}
}