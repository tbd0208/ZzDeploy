package zz.projectDistribute.config;

public enum DistributeLevel {
	
	DEV, STG, OPE;
	
	static public DistributeLevel[] values(String v) {
		return "ALL".equals(v)?values():new DistributeLevel[]{valueOf(v)};
	}
}