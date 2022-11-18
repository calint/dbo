package main;

import java.sql.Timestamp;

import db.DbObject;
import db.FldString;
import db.FldTimestamp;
import db.RelAgg;

public final class Book extends DbObject {
	public final static FldString name = new FldString(800);
	public final static FldString authors = new FldString(3000);
	public final static FldString publisher = new FldString(400);
	public final static FldTimestamp publishedDate = new FldTimestamp();
	public final static RelAgg data = new RelAgg(DataText.class);

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getName() {
		return getStr(name);
	}

	public void setName(String v) {
		set(name, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getAuthors() {
		return getStr(authors);
	}

	public void setAuthors(String v) {
		set(authors, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getPublisher() {
		return getStr(publisher);
	}

	public void setPublisher(String v) {
		set(publisher, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public Timestamp getPublishedDate() {
		return getTimestamp(publishedDate);
	}

	public void setPublishedDate(Timestamp v) {
		set(publishedDate, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public DataText getData(final boolean createIfNone) {
		return (DataText) data.get(this, createIfNone);
	}

	public void deleteData() {
		data.delete(this);
	}

}