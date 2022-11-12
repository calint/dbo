package db;

//CREATE TABLE db_User(id INT PRIMARY KEY AUTO_INCREMENT,name varchar(20) default '',passhash varchar(255) default '',nlogins int default 0,file int);

final public class User extends DbObject {
	public final static StringField name = new StringField();
	public final static StringField passhash = new StringField();
	public final static IntField nlogins = new IntField();
	public final static RelAgg1 file = new RelAgg1(File.class);

	public User() throws Throwable {

	}

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
		return (File) file.create(this);
	}
}