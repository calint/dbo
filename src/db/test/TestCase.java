package db.test;

import db.Db;
import db.DbTransaction;

public abstract class TestCase implements Runnable {
	public final void run() {
		final boolean rst = isResetDatabase();

		if (rst)
			Db.instance().reset();

		if (isRunWithCache())
			doTest(true);

		if (rst)
			Db.instance().reset();

		if (isRunWithoutCache())
			doTest(false);
	}

	/** @return true to reset database before tests */
	protected boolean isResetDatabase() {
		return true;
	}

	/** @return true to run test with cache on */
	protected boolean isRunWithCache() {
		return true;
	}

	/** @return true to run test with cache off */
	protected boolean isRunWithoutCache() {
		return true;
	}

	private void doTest(final boolean cacheon) {
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