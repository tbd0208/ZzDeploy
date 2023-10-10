package zz.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class StoredMap{
	
	private final Map<String,Object> map;
	private final String storePath;
	
	public StoredMap(String storePath){
		this.storePath = storePath;
		this.map = new LinkedHashMap<>();
		load();
	}
	public StoredMap(String storePath,Map<String,Object> map){
		this.storePath = storePath;
		this.map = map;
		load();		
	}
	
	public Set<String> keySet(){
		return map.keySet();
	}
	public Object get(String k) {
		return map.get(k);
	}
	public Object put(String k,Object v) {
		return map.put(k,v);
	}
	public Object remove(String k) {
		return map.remove(k);
	}
	
	public void store() {
		try(
			PrintWriter w = new PrintWriter(new File(storePath));	
		){
			map.forEach((k,v)->{
				w.append(k).append('\t').append(String.valueOf(v)).append('\n');
			});
//			w.flush();
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	final private void load(){
		try(Scanner r = new Scanner(new File(storePath))){
			r.useDelimiter(Pattern.compile("\t|\n"));
			while(r.hasNext()) map.put(r.next(),r.next());
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
}
