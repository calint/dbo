package db;

//CREATE TABLE db_User(id INT PRIMARY KEY AUTO_INCREMENT,name varchar(20) default '',passhash varchar(255) default '',nlogins int default 0,file int);

final public class User extends DbObject {
	public final static StringField name = new StringField();
	public final static StringField passhash = new StringField();
	public final static IntField nlogins = new IntField();
	public final static RelAggN files = new RelAggN(File.class);
	public final static RelAgg profilepic = new RelAgg(File.class);
	public final static RelRef grouppic = new RelRef(File.class);

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

	public File createFile() throws Throwable {
		return (File) files.create(this);
	}

	public File createProfilePic() throws Throwable {
		return (File) profilepic.create(this);
	}

	public void setGroupPic(File f) throws Throwable {
		grouppic.set(this, f);
	}
}