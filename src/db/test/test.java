package db.test;

import java.sql.Timestamp;
import java.util.List;

import db.Db;
import db.DbObject;
import db.DbTransaction;
import db.Limit;
import db.Order;
import db.Query;

public class test extends TestCase {
	@Override
	public void doRun() throws Throwable {
		final DbTransaction tn = Db.currentTransaction();
//		tn.cache_enabled = false;
		File f;
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

		tn.commit(); // must do for fulltext index to update.
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
		int id = 0;
		id = u.getProfilePicId();
		File ff = u.getProfilePic(false);
		u.addRefFile(f.id());
		u.deleteProfilePic(); // ? bug? this deletes the file but there is a dangling ref in RelAgg
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

		u.setNlogins(3);

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
	}
}