package db.test;

import db.Db;
import db.DbTransaction;

/** wrapper for transaction */
public abstract class TestCase implements Runnable {
	public final void run() {
		DbTransaction tn;
		try {
			tn = Db.initCurrentTransaction();
		} catch (Throwable t1) {
			throw new RuntimeException(t1);
		}
		try {
			doRun();
			tn.commit();
		} catch (Throwable t1) {
			try {
				tn.rollback();
			} catch (Throwable t2) {
				t2.printStackTrace();
			}
			throw new RuntimeException(t1);
		} finally {
			Db.deinitCurrentTransaction();
		}
	}

	public abstract void doRun() throws Throwable;
}