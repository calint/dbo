package db;
// CREATE TABLE db_File(id INT PRIMARY KEY AUTO_INCREMENT,name varchar(20) default '');

final public class File extends DbObject {
	public final static StringField name = new StringField();

	public String getName() {
		return getStr(name);
	}

	public void setName(String v) {
		set(name, v);
	}
}