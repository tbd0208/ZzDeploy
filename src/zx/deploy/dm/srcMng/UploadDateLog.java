package zx.deploy.dm.srcMng;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Date;

import zx.deploy.config.Config;
import zz.tool.eclipse.ProjectFile;
import zz.util.StoredMap;

public class UploadDateLog{
	
	static private StoredMap storedMap;
	
	static{
		try{
			storedMap = new StoredMap(Config.MYWEB_LOG_PRE_PATH+"uploadDate.log");
			Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE,-365); // 최근 365일 내용만 남김 
			long limitTime = c.getTime().getTime();
			for(Object key : storedMap.keySet().toArray()){
				
				String[] split = ((String)storedMap.get((String)key)).split(",");
				long time = Long.parseLong(split[0]);
				if(time < limitTime) {
					System.out.println("UploadDateLog remove : " + storedMap.remove((String)key));
				}
			}
			storedMap.store();
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
	static public Object getDate(ProjectFile projectFile) {
		return storedMap.get(projectFile.getPath());
	}
	
	static public Object getDate(File file) {
		return storedMap.get(file.getPath().substring(Config.LOCAL_PROJECT_PRE_PATH.length()).replaceAll("\\\\","/"));
	}
	
	static public void put(String path){
		storedMap.put(path,new Date().getTime());
		storedMap.store();
	}
	
	
}
