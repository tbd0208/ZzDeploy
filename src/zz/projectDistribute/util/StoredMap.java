package zz.projectDistribute.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class StoredMap{
	
	private Map<String,Object> map = new HashMap<>();
	private String storePath;
	
	public StoredMap(String storePath){
		this.storePath = storePath;
		readStringMap(storePath,map);
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
		storeStringMap(storePath,map);
	}

	private static void storeStringMap(String filePath,Map<String,?> o){
		try{
			BufferedWriter w = new BufferedWriter(new FileWriter(filePath,false));
			for (Map.Entry<String,?> ent : o.entrySet()){
				w.append(ent.getKey()).append('\t').append(String.valueOf(ent.getValue()));
				w.newLine();
			}
			w.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	private static Map<String, Object> readStringMap(String filePath,Map<String,Object> o){
		try{
			Scanner r = new Scanner(new File(filePath));
			r.useDelimiter(Pattern.compile("\t|\r\n"));
			while(r.hasNext()) {
				String key = r.next();
				String value = r.next();
				o.put(key,value);
			}
			r.close();
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}
		return o;
	}

	public Set<String> keySet(){
		return map.keySet();
	}
	
}
