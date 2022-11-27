package db.test;

import java.io.FileReader;
import java.util.List;

import csv.CsvReader;
import db.Db;
import db.DbTransaction;

// import-200k-book
//   download csv at https://www.kaggle.com/datasets/mohamedbakhet/amazon-books-reviews
public class import_books extends TestCase {
	@Override
	protected boolean isRunWithoutCache() {
		return false;
	}

	protected String getFilePath() {
		return "../cvs-samples/books_data.csv";
	}

	@Override
	public void doRun() throws Throwable {
		final DbTransaction tn = Db.currentTransaction();
//		Db.log_enable = false;
//		System.out.println("cache_enabled=" + tn.cache_enabled);

		final String filePath = getFilePath();
		// sanity check
		FileReader in = new FileReader(filePath);
		CsvReader csv = new CsvReader(in);
		List<String> ls = csv.nextRecord();// read headers
		int i = 2; // skip headers
		System.out.println("bounds check file '" + filePath + "'");
		while (true) {
			ls = csv.nextRecord();
			if (ls == null)
				break;
			final String name = ls.get(0);
			if (name.length() > Book.name.getSize())
				throw new RuntimeException("record " + i + " has size of name " + name.length()
						+ " but field length is " + Book.name.getSize());

			final String authors = ls.get(2);
			if (authors.length() > Book.authors.getSize())
				throw new RuntimeException("record " + i + " has size of authors " + authors.length()
						+ " but field length is " + Book.authors.getSize());

			final String publisher = ls.get(5);
			if (publisher.length() > Book.publisher.getSize())
				throw new RuntimeException("record " + i + " has size of publisher " + publisher.length()
						+ " but field length is " + Book.publisher.getSize());

			if (++i % 100 == 0)
				System.out.println(i);
		}
		in.close();
		System.out.println("bounds check done. importing " + (i - 2) + " books from " + filePath + "'");
		in = new FileReader(filePath);
		csv = new CsvReader(in);
		ls = csv.nextRecord();// read headers
		i = 2; // skip headers
		final StringBuilder sb = new StringBuilder(1000);
		while (true) {
			ls = csv.nextRecord();
			if (ls == null)
				break;
			final Book o = (Book) tn.create(Book.class);
			o.setName(ls.get(0));
			o.setAuthors(ls.get(2));
			o.setPublisher(ls.get(5));
			final DataText d = o.getData(true);
			d.setData(ls.get(1));

			sb.setLength(0);
			sb.append(o.getName()).append(" ").append(o.getAuthors()).append(" ").append(o.getPublisher());
			d.setMeta(sb.toString());

			if (++i % 100 == 0) {
				System.out.println(i);
				tn.commit();
			}
		}
		in.close();
		System.out.println("import done. " + (i - 2) + " books imported.");
	}
}