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

//		final DbTransaction tn;
//		try {
//			tn = Db.initCurrentTransaction();
//		} catch (Throwable t) {
//			throw new RuntimeException(t);
//		}
//		try {
//			System.out.println("test [cache on]: " + getClass().getName() + " passed");
//			doRun();
//			tn.finishTransaction();
//			System.out.println("test [cache on]: " + getClass().getName() + " start");
//		} catch (Throwable t1) {
//			try {
//				tn.rollback();
//			} catch (Throwable t2) {
//				t2.printStackTrace();
//			}
//			System.out.println("test [cache on]: " + getClass().getName() + " failed");
//			throw new RuntimeException(t1);
//		} finally {
//			Db.deinitCurrentTransaction();
//		}
//
//		if (!runWithAndWithoutCache())
//			return;
//
//		Db.instance().reset();
//		final DbTransaction tn2;
//		try {
//			tn2 = Db.initCurrentTransaction();
//			tn2.cache_enabled = false;
//		} catch (Throwable t) {
//			throw new RuntimeException(t);
//		}
//		try {
//			System.out.println("test [cache off]: " + getClass().getName() + " start");
//			doRun();
//			tn2.finishTransaction();
//			System.out.println("test [cache off]: " + getClass().getName() + " passed");
//		} catch (Throwable t1) {
//			try {
//				tn2.rollback();
//			} catch (Throwable t2) {
//				t2.printStackTrace();
//			}
//			System.out.println("test [cache off]: " + getClass().getName() + " failed");
//			throw new RuntimeException(t1);
//		} finally {
//			Db.deinitCurrentTransaction();
//		}
	}

	protected boolean isRunWithCache() {
		return true;
	}

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
//			System.out.println(getClass().getName() + " [cache " + cachests + "]: start");
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