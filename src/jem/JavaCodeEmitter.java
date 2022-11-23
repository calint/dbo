package jem;

import java.io.PrintWriter;
import java.util.ArrayList;

import db.Db;
import db.DbClass;
import db.DbField;
import db.DbObject;
import db.DbRelation;

public final class JavaCodeEmitter {
	final Db db;
	final ArrayList<JavaCodeElem> elems = new ArrayList<JavaCodeElem>();

	public JavaCodeEmitter(final Db db) {
		this.db = db;
	}

	private void add(final JavaCodeElem el) {
		elems.add(el);
	}

	public static String getPackageNameForClass(final Class<?> cls) {
		final String nm = cls.getName();
		final String pk = nm.substring(0, nm.lastIndexOf('.'));
		return pk;
	}

	public static String getClassNameAfterPackageForClass(final Class<?> cls) {
		final String nm = cls.getName();
		final String nm2 = nm.substring(nm.lastIndexOf('.') + 1);
		return nm2;
	}

	public void emit(final PrintWriter out, final Class<? extends DbObject> cls) throws Throwable {
		final DbClass dbc = db.getDbClassForJavaClass(cls);
		for (final DbField dbf : dbc.getDeclaredFields()) {
			final StringBuilder sb = new StringBuilder();
			sb.append(getPackageNameForClass(getClass())).append('.');
			sb.append(getClassNameAfterPackageForClass(dbf.getClass())).append("Elem");
			final String elemClsName = sb.toString();

			try {
				final JavaCodeElem jce = (JavaCodeElem) Class.forName(elemClsName).getConstructor(dbf.getClass())
						.newInstance(dbf);
				add(jce);
			} catch (Throwable t) {
				System.err.println(
						"cannot create JavaCodeElem of class '" + elemClsName + "' for java class " + t.getMessage());
			}
		}
		for (final DbRelation dbr : dbc.getDeclaredRelations()) {
			final StringBuilder sb = new StringBuilder();
			sb.append(getPackageNameForClass(getClass())).append('.');
			sb.append(getClassNameAfterPackageForClass(dbr.getClass())).append("Elem");
			final String elemClsName = sb.toString();

			try {
				final JavaCodeElem jce = (JavaCodeElem) Class.forName(elemClsName).getConstructor(dbr.getClass())
						.newInstance(dbr);
				add(jce);
			} catch (Throwable t) {
				System.err.println(
						"cannot create JavaCodeElem of class '" + elemClsName + "' for java class " + t.getMessage());
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

	public static String getSingulariesForPlurar(String s) {
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
