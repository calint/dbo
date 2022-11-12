package db;

import java.util.HashMap;

public class RelRef extends DbRelation {
	private Class<? extends DbObject> toCls;
	LongField relfld;
	
	public RelRef(Class<? extends DbObject> cls) {
		this.toCls = cls;
	}

	@Override
	void connect(final DbClass c, final HashMap<Class<? extends DbObject>, DbClass> jclsToDbCls) {
		relfld = new LongField();
		relfld.dbname = name;
		c.fields.add(relfld);
	}
	
	public void set(final DbObject ths,final DbObject trg) throws Throwable {
		ths.set(relfld, trg.getId());
		ths.updateDb();
	}
}
