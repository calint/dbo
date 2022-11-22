package jem;

import java.io.PrintWriter;
import java.util.ArrayList;

import db.Db;
import db.DbClass;
import db.DbField;
import db.DbObject;
import db.DbRelation;
import db.FldBoolean;
import db.FldDouble;
import db.FldFloat;
import db.FldInt;
import db.FldLong;
import db.FldString;
import db.FldTimestamp;
import db.RelAgg;
import db.RelAggN;
import db.RelRef;
import db.RelRefN;

public final class JavaCodeEmitter {
	final Db db;
	final ArrayList<JavaCodeElem> elems = new ArrayList<JavaCodeElem>();

	public JavaCodeEmitter(final Db db) {
		this.db = db;
	}

	public void add(final JavaCodeElem el) {
		elems.add(el);
	}

	public void emit(final PrintWriter out, final Class<? extends DbObject> cls) throws Throwable {
		final DbClass dbc = db.getDbClassForJavaClass(cls);
		for (final DbField dbf : dbc.getDeclaredFields()) {
			if (dbf instanceof FldString) {
				final FldString f = (FldString) dbf;
				add(new FldStringElem(f));
				continue;
			}
			if (dbf instanceof FldInt) {
				final FldInt f = (FldInt) dbf;
				add(new FldIntElem(f));
				continue;
			}
			if (dbf instanceof FldLong) {
				final FldLong f = (FldLong) dbf;
				add(new FldLongElem(f));
				continue;
			}
			if (dbf instanceof FldFloat) {
				final FldFloat f = (FldFloat) dbf;
				add(new FldFloatElem(f));
				continue;
			}
			if (dbf instanceof FldDouble) {
				final FldDouble f = (FldDouble) dbf;
				add(new FldDoubleElem(f));
				continue;
			}
			if (dbf instanceof FldBoolean) {
				final FldBoolean f = (FldBoolean) dbf;
				add(new FldBooleanElem(f));
				continue;
			}
			if (dbf instanceof FldTimestamp) {
				final FldTimestamp f = (FldTimestamp) dbf;
				add(new FldTimestampElem(f));
				continue;
			}
		}
		for (final DbRelation dbr : dbc.getDeclaredRelations()) {
			if (dbr instanceof RelRef) {
				final RelRef r = (RelRef) dbr;
				add(new RelRefElem(r));
				continue;
			}
			if (dbr instanceof RelAgg) {
				final RelAgg r = (RelAgg) dbr;
				add(new RelAggElem(r));
				continue;
			}
			if (dbr instanceof RelRefN) {
				final RelRefN r = (RelRefN) dbr;
				add(new RelRefNElem(r));
				continue;
			}
			if (dbr instanceof RelAggN) {
				final RelAggN r = (RelAggN) dbr;
				add(new RelAggNElem(r));
				continue;
			}
		}

		out.println("//****************************************************");
		out.println(cls.getName());
		out.println("//****************************************************");
		for (final JavaCodeElem e : elems) {
			e.emit(out);
			out.flush();
		}
		out.println("//****************************************************");
		out.flush();
	}

	static String getSingulariesForPlurar(String s) {
		final StringBuilder sb = new StringBuilder();
		sb.append(s);
		if (s.endsWith("ies")) {
			sb.setLength(sb.length() - 3);
			sb.append('y');
			return sb.toString();
		}
		if (s.endsWith("s")) {
			sb.setLength(sb.length() - 1);
		}
		return sb.toString(); // ? lookup in dictionary?
	}
}
