package db.test;

import db.Db;
import db.DbTransaction;

public abstract class TestCase implements Runnable {
	public final void run() {
		Db.instance().reset();
		if (isRunWithCache())
			runWithCache(true);
		Db.instance().reset();
		if (isRunWithoutCache())
			runWithCache(false);
	}

	/** override and return true if test should not run with cache on */
	protected boolean isRunWithCache() {
		return true;
	}

	/** override and return true if test should not run with cache off */
	protected boolean isRunWithoutCache() {
		return true;
	}

	private void runWithCache(final boolean cacheon) {
		final DbTransaction tn;
		final String cachests = cacheon ? "on" : "off";
		try {
			tn = Db.initCurrentTransaction();
			tn.cache_enabled = cacheon;
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		try {
			doRun();
			tn.finishTransaction();
			System.out.println(getClass().getName() + " [cache " + cachests + "]: passed");
		} catch (Throwable t1) {
			try {
				tn.rollback();
			} catch (Throwable t2) {
				t2.printStackTrace();
			}
			System.out.println(getClass().getName() + " [cache " + cachests + "]: failed");
			throw new RuntimeException(t1);
		} finally {
			Db.deinitCurrentTransaction();
		}
	}

	public abstract void doRun() throws Throwable;
}