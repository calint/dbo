package db.main;

import db.DbObject;
import db.FldInt;
import db.RelAgg;
import db.RelAggN;
import db.RelRef;
import db.RelRefN;
import db.FldString;

public final class User extends DbObject {
	public final static FldString name = new FldString();
	public final static FldString passhash = new FldString(32);
	public final static FldInt nlogins = new FldInt();
	public final static RelAggN files = new RelAggN(File.class);
	public final static RelAgg profilePic = new RelAgg(File.class);
	public final static RelRef groupPic = new RelRef(File.class);
	public final static RelRefN refFiles = new RelRefN(File.class);

	public String getName() {
		return getStr(name);
	}

	public void setName(String v) {
		set(name, v);
	}

	public String getPasshash() {
		return getStr(passhash);
	}

	public void setPasshash(String v) {
		set(passhash, v);
	}

	public int getNLogins() {
		return getInt(nlogins);
	}

	public void setNLogins(int v) {
		set(nlogins, v);
	}

	public File createFile() {
		return (File) files.create(this);
	}

	public File createProfilePic() {
		return (File) profilePic.create(this);
	}

	public void setGroupPic(File o) {
		groupPic.set(this, o);
	}

	public void addRefFile(File o) {
		refFiles.add(this, o);
	}
}