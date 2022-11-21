package db.test;

import db.Db;
import db.DbTransaction;

public class refn_orphans extends TestCase {
	@Override
	public void doRun() throws Throwable {
		final DbTransaction tn = Db.currentTransaction();

		final User u = (User) tn.create(User.class);

		final File f1 = (File) tn.create(File.class);
		u.addRefFile(f1.id());

		tn.delete(f1);
		
		// leaves orphan in RelRefN table User_refFiles
	}
}