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
	public void doRun() throws Throwable {
		final DbTransaction tn = Db.currentTransaction();
		Db.log_enable = false;

		// sanity check
		FileReader in = new FileReader("../cvs-samples/books_data.csv");
		CsvReader csv = new CsvReader(in);
		List<String> ls = csv.nextRecord();// read headers
		int i = 2; // skip headers
		System.out.println("bounds check");
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
		System.out.println("bounds check done.");

		// import
		System.out.println("import");
		in = new FileReader("../cvs-samples/books_data.csv");
		csv = new CsvReader(in);
		ls = csv.nextRecord();// read headers
		i = 2; // skip headers
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
			if (++i % 100 == 0) {
				System.out.println(i);
				tn.commit();
			}
		}
		in.close();
		System.out.println("import done. finnish transaction.");
	}
}