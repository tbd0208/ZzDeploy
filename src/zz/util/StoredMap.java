package zz.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.regex.Pattern;

public class StoredMap{
	
	private final Map<String,Object> map;
	private String storePath;
	
	public StoredMap(String storePath) throws FileNotFoundException{
		this(storePath,new LinkedHashMap<>());
	}
	public StoredMap(String storePath,Map<String,Object> map) throws FileNotFoundException{
		this.storePath = storePath;
		this.map = map;
		readStringMap(storePath,map);
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
	
	public void store(){
		try{
			BufferedWriter w = new BufferedWriter(new FileWriter(storePath,false));
			
			/*for (Map.Entry<String,?> ent : map.entrySet()){
				w.append(ent.getKey()).append('\t').append(String.valueOf(ent.getValue()));
				w.newLine();
			}*/
			
			map.forEach((k,v)->{
				try{
					w.append(k).append('\t').append(String.valueOf(v));
					w.newLine();
				}catch (IOException e){
					e.printStackTrace();
				}
			});
			
			w.flush();
			w.close();
		}catch (IOException e){
			e.printStackTrace();
		}
	}

	private static void readStringMap(String filePath,Map<String,Object> o) throws FileNotFoundException{
		Scanner r = new Scanner(new File(filePath));
		r.useDelimiter(Pattern.compile("\t|\r\n"));
		while(r.hasNext()) o.put(r.next(),r.next());
		r.close();
	}
	
//	@Override
//	protected void finalize() throws Throwable{
//		System.out.println("TEST finalize");
//		this.store();
//	}
	
}
