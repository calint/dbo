package db.test;

import db.Db;
import db.DbTransaction;

public abstract class TestCase implements Runnable {
	public final void run() {
		final DbTransaction tn;
		try {
			tn = Db.initCurrentTransaction();
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		try {
			doRun();
			tn.finishTransaction();
			System.out.println("test: " + getClass().getName() + " passed");
		} catch (Throwable t1) {
			try {
				tn.rollback();
			} catch (Throwable t2) {
				t2.printStackTrace();
			}
			System.out.println("test: " + getClass().getName() + " failed");
			throw new RuntimeException(t1);
		} finally {
			Db.deinitCurrentTransaction();
		}
	}

	public abstract void doRun() throws Throwable;
}