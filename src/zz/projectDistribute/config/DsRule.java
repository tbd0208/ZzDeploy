package zz.projectDistribute.config;

public class DsRule{
	// 확장자 모음
	static public final String 
		EXTENSION_CLASS = ".class", EXTENSION_JAVA = ".java", EXTENSION_JSP = ".jsp"
		,EXTENSION_XML = ".xml"
		,EXTENSION_PROPERTIES = ".properties"
		,EXTENSION_TLD = ".tld"
		,EXTENSION_SH = ".sh"
		,EXTENSION_JAR = ".jar"
	;
	
	/*
	 * URL확장자로 동적파일 구분
	 */
	static public boolean checkDynamicFileExtension(String url){
		return url.endsWith(EXTENSION_JAVA) || url.endsWith(EXTENSION_JSP) || url.endsWith(EXTENSION_XML) || url.endsWith(EXTENSION_PROPERTIES) || url.endsWith(EXTENSION_TLD);
	}
}
