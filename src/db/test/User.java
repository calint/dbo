package db.test;

import java.sql.Timestamp;
import java.util.List;

import db.DbObject;
import db.FldBoolean;
import db.FldDouble;
import db.FldFloat;
import db.FldInt;
import db.FldLong;
import db.FldString;
import db.FldTimestamp;
import db.Index;
import db.IndexFt;
import db.IndexRel;
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
	public final static FldLong lng = new FldLong();
	public final static FldFloat flt = new FldFloat();
	public final static FldDouble dbl = new FldDouble();
	public final static FldBoolean bool = new FldBoolean();
//	public final static FldTimestamp birthTime = new FldTimestamp(Timestamp.valueOf("1970-01-01 01:00:01"));
	public final static FldTimestamp birthTime = new FldTimestamp();
	public final static RelAggN files = new RelAggN(File.class);
	public final static RelAgg profilePic = new RelAgg(File.class);
	public final static RelRef groupPic = new RelRef(File.class);
	public final static RelRefN refFiles = new RelRefN(File.class);

	public final static Index ixName = new Index(name);
	public final static IndexFt ixFt = new IndexFt(name);
	public final static IndexRel ixGroupPic = new IndexRel(groupPic);

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getName() {
		return getStr(name);
	}

	public void setName(final String v) {
		set(name, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getPasshash() {
		return getStr(passhash);
	}

	public void setPasshash(final String v) {
		set(passhash, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public int getNlogins() {
		return getInt(nlogins);
	}

	public void setNlogins(final int v) {
		set(nlogins, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public long getLng() {
		return getLong(lng);
	}

	public void setLng(final long v) {
		set(lng, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public float getFlt() {
		return getFloat(flt);
	}

	public void setFlt(final float v) {
		set(flt, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public double getDbl() {
		return getDouble(dbl);
	}

	public void setDbl(final double v) {
		set(dbl, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public boolean isBool() {
		return getBoolean(bool);
	}

	public void setBool(final boolean v) {
		set(bool, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public Timestamp getBirthTime() {
		return getTimestamp(birthTime);
	}

	public void setBirthTime(final Timestamp v) {
		set(birthTime, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public File createFile(){
		return(File)files.create(this);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<File>getFiles(final Query qry,final Order ord,final Limit lmt){
		return(List<File>)(List)files.get(this,qry,ord,lmt);
	}

	public int getFilesCount(final Query qry){
		return files.getCount(this,qry);
	}

	public void deleteFile(final int id){
		files.delete(this,id);
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

	public void setGroupPic(final int id) {
		groupPic.set(this, id);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public void addRefFile(final int id) {
		refFiles.add(this, id);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<File> getRefFiles(final Query qry, final Order ord, final Limit lmt) {
		return (List<File>) (List) refFiles.get(this, qry, ord, lmt);
	}

	public int getRefFilesCount(final Query qry) {
		return refFiles.getCount(this, qry);
	}

	public void removeRefFile(final int id) {
		refFiles.remove(this, id);
	}
}