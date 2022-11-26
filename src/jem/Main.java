package jem;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import db.Db;
import db.DbObject;

/**
 * Generates accessors for a java class that can be copied and pasted into the
 * source.
 */
public final class Main {
	@SuppressWarnings("unchecked")
	public final static void main(final Db db, final String clsName) throws Throwable {
		final Class<? extends DbObject> cls = (Class<? extends DbObject>) Class.forName(clsName);
		final JavaCodeEmitter jce = new JavaCodeEmitter(db);
		final PrintWriter out = new PrintWriter(new OutputStreamWriter(System.out));// ? param to call
		jce.emit(out, cls);
	}
}
