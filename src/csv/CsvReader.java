package csv;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/** reads CSV file. note: it does not handle empty trailing lines */
public final class CsvReader {
	private char columnSeparator = ',';
	private char stringDelim = '"';
	private BufferedReader reader;

	public CsvReader(Reader reader) {
		this(reader, ',', '"');
	}

	public CsvReader(Reader reader, char columnSeparator, char stringDelim) {
		this.reader = new BufferedReader(reader);
		this.columnSeparator = columnSeparator;
		this.stringDelim = stringDelim;
	}

	public List<String> nextRecord() throws IOException {
		final ArrayList<String> ls = new ArrayList<String>();
		final StringBuilder sb = new StringBuilder();
		boolean inString = false;
		while (true) {
			final int chi = reader.read();
			if (chi == -1) {
				if (ls.isEmpty())
					return null;
				throw new RuntimeException("unexpected end of stream");// ? break? return ls?
			}
			final char ch = (char) chi;
			if (inString) {
				if (ch == stringDelim) {// example ... "the quote ""hello"" ", ...
					reader.mark(2); // ? probably 1
					final int nxtChr = reader.read();
					if (nxtChr == -1)
						throw new RuntimeException("unexpected end of stream");
					if ((char) nxtChr == stringDelim) {
						sb.append(stringDelim);
						continue;
					}
					inString = false;
					reader.reset();
					continue;
				}
				sb.append(ch);
				continue;
			}
			if (ch == columnSeparator) {
				ls.add(sb.toString());
				sb.setLength(0);
				continue;
			}
			if (ch == stringDelim) {
				inString = true;
				continue;
			}
			if (ch == '\r') {
				continue;
			}
			if (ch == '\n') {
				ls.add(sb.toString());
				sb.setLength(0);
				break;
			}
			sb.append(ch);
		}
		return ls;
	}

//	public static void main(String[] args) throws Throwable {
//		CsvReader csv = new CsvReader(new FileReader(new File("/home/c/Downloads/sample-books.csv")), true, ',');
//		List<String> ls = csv.nextRecord();
//		System.out.println(ls);
//		while (true) {
//			ls = csv.nextRecord();
//			if (ls == null)
//				break;
//			System.out.println(ls);
//		}
//	}

}
