package zz.projectDistribute.util;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("serial")
final public class QQMap extends LinkedHashMap<String,Object>{
	
	public QQMap() {
		super();
	}
	public QQMap(Map<String,Object> arg0) {
		super(arg0);
	}
	public QQMap(String k1,Object v1){
		this.put(k1,v1);
	}
	public QQMap(String k1,Object v1,String k2,Object v2){
		this.put(k1,v1);this.put(k2,v2);
	}
	public QQMap(String k1,Object v1,String k2,Object v2,String k3,Object v3){
		this.put(k1,v1);this.put(k2,v2);this.put(k3,v3);
	}
	public QQMap(String k1,Object v1,String k2,Object v2,String k3,Object v3,String k4,Object v4){
		this.put(k1,v1);this.put(k2,v2);this.put(k3,v3);this.put(k4,v4);
	}
	public QQMap(String k1,Object v1,String k2,Object v2,String k3,Object v3,String k4,Object v4,String k5,Object v5){
		this.put(k1,v1);this.put(k2,v2);this.put(k3,v3);this.put(k4,v4);this.put(k5,v5);
	}
	
	public boolean has(){
		return !super.isEmpty();
	}
	
	public Object setDefault(String k,Object defaultValue){
		Object v = this.get(k);
		if(v==null){
			this.put(k,defaultValue);
			return defaultValue;
		}
		return v;
	}
	public QQMap take(String key,Object v){
		this.put(key,v);
		return this;
	}
	public QQMap inSplit(String key,String split){
		Object v = this.get(key);
		if(v.getClass().isArray()) return this;
		this.put(key,Arrays.asList(((String)v).split(split)));
		return this;
	}
	
	public<V> V val(String k){
		return (V)super.get(k);
	}
	public String str(String k){
		return String.valueOf(super.get(k));
	}
	public int strToInt(String k){
		return Integer.parseInt(String.valueOf(super.get(k)));
	}
	
	
	/**
	 * Get sub map or new sub map
	 */
	public QQMap sub(String k){
		QQMap v = (QQMap)super.get(k);
		if(v==null) {
			v = new QQMap();
			super.put(k,v);
		}
		return v;
	}
	public QQMap getSub(String k){
		return (QQMap) super.get(k);
	}
	
}