package db;

public final class Limit {
	private int offset;
	private int rowCount;

	public Limit(int offset, int rowCount) {
		this.offset = offset;
		this.rowCount = rowCount;
	}

	void sql_to(final StringBuilder sb) {
		sb.append(" limit ").append(offset).append(',').append(rowCount);
	}
}
