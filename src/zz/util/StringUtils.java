package zz.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	/**
	 * "/1/2/{lastSimpleName}.ext"
	 */
	final static public String geUrltLastSimpleName(String url){
		int lastIndexOf = url.lastIndexOf(".");
		if(lastIndexOf==-1) return url;
		return url.substring(url.lastIndexOf("/",lastIndexOf)+1,lastIndexOf);
	}

	/**
	 * "{a->A}bc"
	 */
	final static public String toLowerCaseAtFirst(String v){
		return Character.toLowerCase(v.charAt(0))+v.substring(1);
	}

	/**
	 * "{STR}ING" size:3
	 */
	final static public String cutLastString(String v,int size){
		return v.substring(0,v.length()-size);
	}

	/**
	 * "{STR}ING" find : I
	 */
	final static public String cutLastIndexOfString(String v,String findString){
		return v.substring(0,v.lastIndexOf(findString));
	}

	/**
	 * "{STR}ING" find : I
	 */
	final static public String cutLastIndexOfString(String v,char findChar){
		return v.substring(0,v.lastIndexOf(findChar));
	}
	
	/**
	 * "STR{ING}" find : R
	 */
	final static public String getLastIndexOfString(String v,String findString){
		return v.substring(v.lastIndexOf(findString)+1);
	}
	/**
	 * "STR{ING}" find : R
	 */
	final static public String getLastIndexOfString(String v,char findChar){
		return v.substring(v.lastIndexOf(findChar)+1);
	}

	/**
	 * "{STR}ING" size:3
	 */
	final static public String getLastString(String v,int size){
		return v.substring(v.length()-size);
	}

	/**
	 * "STRING{001}" size : 3
	 */
	final static public int getNumbering(String v,int numberingSize){
		return Integer.parseInt(getLastString(v,numberingSize));
	}

	/**
	 * "STRING.{extension}
	 */
	final static public String extension(String v){
		return v.substring(v.lastIndexOf('.')+1);
	}

	/**
	 * v<>(null||spaces)
	 */
	final static public boolean isValid(String v){
		return v!=null&&!v.trim().isEmpty();
	}

	final static public boolean isEmpty(String v){
		return v==null||v.isEmpty();
	}

	final static public String nvl(Object o){
		return o==null?"":""+o;
	}

	final static public String nvl(Object o,Object o2){
		return ""+(o==null?o2:o);
	}

	final static public String to(InputStream inputStream) throws IOException{
		StringBuilder sb = new StringBuilder();
		byte[] b = new byte[4096];
		for(int n;(n = inputStream.read(b))!=-1;){
			sb.append(new String(b,0,n,"UTF-8"));
		}
		return sb.toString();
	}

	final static public int countChar(String name,char searchChar){
		int count = 0;
		for(char c : name.toCharArray()) if(searchChar==c) count++;
		return count;
	}
	final static public int countCharRepeatedFrist(String s,char c){
		int count=0;
		int length = s.length();
		while(count<length && s.charAt(count)==c) count++;
		return count;
	}

	public static String splitToWrapJoin(String v,String splitRegx, String frontStr,String tailStr,String joinStr){
		return frontStr+v.replaceAll(splitRegx,tailStr+joinStr+frontStr)+tailStr;
	}

	public static String zf(int i,int n){
		return String.format("%0"+n+"d", i);
	}
	
	public static String chgTail(String t,String beforeTail,String afterTail){
		return t.substring(0,t.length()-beforeTail.length())+afterTail;
	}
	
	
	public static String replaceWithDollar(String s, Map<String,?> map) {
		StringBuilder sb = new StringBuilder();
		Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
		Matcher matcher = pattern.matcher(s);
		int n = 0;
		while(matcher.find()){
			String group = matcher.group(1);
			Object v = map.get(group);
			sb.append(s, n, matcher.start());
			sb.append(v);
			n = matcher.end();
		}
		sb.append(s,n,s.length());
		return sb.toString();
	}
}
