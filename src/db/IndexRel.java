package db;

public class IndexRel extends Index {
	final private DbRelation rel;

	public IndexRel(final DbRelation r) { // ? handles only ref and agg
		rel = r;
	}

	@Override
	void init(final DbClass c) {
		if (rel.relFld == null)
			throw new RuntimeException("relation " + rel.name + " in class " + cls.getName()
					+ " can not be indexed. Is relation type RefN?");

		fields.add(rel.relFld);
	}

}
