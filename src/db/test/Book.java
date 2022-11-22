package db.test;

import java.sql.Timestamp;

import db.DbObject;
import db.FldString;
import db.FldTimestamp;
import db.IndexRel;
import db.RelAgg;

public final class Book extends DbObject {
	public final static FldString name = new FldString(800);
	public final static FldString authors = new FldString(3000);
	public final static FldString publisher = new FldString(400);
	public final static FldTimestamp publishedDate = new FldTimestamp();
	public final static RelAgg data = new RelAgg(DataText.class);

	// optimizes Book join with DataText when full text query
	public final static IndexRel ixRelData = new IndexRel(data);

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getName() {
		return getStr(name);
	}

	public void setName(final String v) {
		set(name, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getAuthors() {
		return getStr(authors);
	}

	public void setAuthors(final String v) {
		set(authors, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public String getPublisher() {
		return getStr(publisher);
	}

	public void setPublisher(final String v) {
		set(publisher, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public Timestamp getPublishedDate() {
		return getTimestamp(publishedDate);
	}

	public void setPublishedDate(final Timestamp v) {
		set(publishedDate, v);
	}

	// ---- - - - - - ---- -- --- - -- - -- - -- -- - -- - - - -- - - --- - -
	public int getDataId() {
		return data.getId(this);
	}

	public DataText getData(final boolean createIfNone) {
		return (DataText) data.get(this, createIfNone);
	}

	public void deleteData() {
		data.delete(this);
	}

}