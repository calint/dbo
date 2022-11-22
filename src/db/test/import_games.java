package db.test;

import java.io.FileReader;
import java.util.List;

import csv.CsvReader;
import db.Db;
import db.DbTransaction;

// import games
public class import_games extends TestCase {
	@Override
	protected boolean isRunWithoutCache() {
		return false;
	}

	protected String getFilePath() {
		return "../cvs-samples/steam-games.csv";
	}
	
	@Override
	public void doRun() throws Throwable {
		final DbTransaction tn = Db.currentTransaction();
		tn.cache_enabled = false;
		Db.log_enable = false;

		final FileReader in = new FileReader(getFilePath());
		final CsvReader csv = new CsvReader(in, ';', '"');
		List<String> ls = csv.nextRecord();// read headers
		int i = 2;
		while (true) {
			ls = csv.nextRecord();
			if (ls == null)
				break;
//			System.out.println(ls);
			final Game o = (Game) tn.create(Game.class);
			o.setName(ls.get(1));
			o.setDescription(ls.get(2));
			if (++i % 100 == 0) {
				System.out.println(i);
//				tn.commit();
			}
		}
		in.close();
	}
}