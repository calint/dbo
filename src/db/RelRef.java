package db;

import java.util.List;

public final class RelRef extends DbRelation {

	public RelRef(final Class<? extends DbObject> toCls) {
		super(toCls);
	}

	@Override
	void init(final DbClass c) {
		relFld = new FldRel();
		relFld.name = name;
		c.declaredFields.add(relFld);
	}

	public void set(final DbObject ths, final int trgId) {
		ths.set(relFld, trgId);
	}

//	public void remove(final DbObject ths) {
//		set(ths, 0);
//	}

	/** @returns 0 if id is null */
	public int getId(final DbObject ths) {
		final Object objId = ths.fieldValues.get(relFld);
		if (objId == null)
			return 0;
		return ((Integer) objId).intValue();
	}

	public DbObject get(final DbObject ths) {
		final int id = getId(ths);
		if (id == 0)
			return null;
		final List<? extends DbObject> ls = Db.currentTransaction().get(toCls, new Query(toCls, id), null, null);
//		if (ls.isEmpty())
//			throw new RuntimeException("didnt't expect empty result for id=" + id);
		if (ls.isEmpty())
			return null;
		return ls.get(0);
	}
	
	boolean cascadeDeleteNeeded() {
		return false;
	}

}
