package db;

public class IndexRel extends Index {
	private DbRelation rel;

	public IndexRel(final DbRelation r) {
		rel = r;
//		// check supported relations
//		if (r instanceof RelAggN || r instanceof RelRefN) {
//			throw new RuntimeException("relation " + r + " not supported");
//		}
	}

	@Override
	void init(final DbClass c) {
		fields.add(rel.relFld);
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder(128);
		sb.append(name);// .append(" on ").append(tableName).append('(');
		return sb.toString();
	}

}
