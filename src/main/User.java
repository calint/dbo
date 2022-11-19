package main;

import java.util.List;

import db.DbObject;
import db.FldBoolean;
import db.FldDouble;
import db.FldFloat;
import db.FldInt;
import db.FldString;
import db.FldTimestamp;
import db.Limit;
import db.Order;
import db.Query;
import db.RelAgg;
import db.RelAggN;
import db.RelRef;
import db.RelRefN;

public final class User extends DbObject {
	public final static FldString name = new FldString();
	public final static FldString passhash = new FldString(32);
	public final static FldInt nlogins = new FldInt();
	public final static FldFloat flt = new FldFloat();
	public final static FldDouble dbl = new FldDouble();
	public final static FldBoolean bool = new FldBoolean();
//	public final static FldTimestamp birthTime = new FldTimestamp(Timestamp.valueOf("1970-01-01 01:00:01"));
	public final static FldTimestamp birthTime = new FldTimestamp();
	public final static RelAggN files = new RelAggN(File.class);
	public final static RelAgg profilePic = new RelAgg(File.class);
	public final static RelRef groupPic = new RelRef(File.class);
	public final static RelRefN refFiles = new RelRefN(File.class);

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getName() {
		return getStr(name);
	}

	public void setName(String v) {
		set(name, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getPasshash() {
		return getStr(passhash);
	}

	public void setPasshash(String v) {
		set(passhash, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public int getNLogins() {
		return getInt(nlogins);
	}

	public void setNLogins(int v) {
		set(nlogins, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public float getFlt() {
		return getFloat(flt);
	}

	public void setFlt(float v) {
		set(flt, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public double getDbl() {
		return getDouble(dbl);
	}

	public void setDbl(double v) {
		set(dbl, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public boolean getBool() {
		return getBoolean(bool);
	}

	public void setBool(boolean v) {
		set(bool, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public File createFile() {
		return (File) files.create(this);
	}

//	public List<DbObject> getFiles(final Query qry, final Order ord, final Limit lmt) {
//		return files.get(this, qry, ord, lmt);
//	}

	@SuppressWarnings({ "unchecked", "rawtypes" }) // ? uggly
	public List<File> getFiles(final Query qry, final Order ord, final Limit lmt) {
		return (List<File>) (List) files.get(this, qry, ord, lmt);
	}

	public void deleteFile(int id) {
		files.delete(this, id);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public int getProfilePicId() {
		return profilePic.getId(this);
	}

	public File getProfilePic(final boolean createIfNone) {
		return (File) profilePic.get(this, createIfNone);
	}

	public void deleteProfilePic() {
		profilePic.delete(this);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public int getGroupPicId() {
		return groupPic.getId(this);
	}

	public File getGroupPic() {
		return (File) groupPic.get(this);
	}

	public void setGroupPic(int id) {
		groupPic.set(this, id);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public void addRefFile(int id) {
		refFiles.add(this, id);
	}

	public List<DbObject> getRefFiles(final Query qry, final Order ord, final Limit lmt) {
		return refFiles.get(this, qry, ord, lmt);
	}

	public void removeRefFile(int id) {
		refFiles.remove(this, id);
	}

}