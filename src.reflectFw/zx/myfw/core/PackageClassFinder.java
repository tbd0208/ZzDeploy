package zx.myfw.core;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

public class PackageClassFinder{
	
	private List<Class<?>> list = new ArrayList<>();
	private String packageName;
	
	public PackageClassFinder(String packageName){
		this.packageName = packageName;
	}
	
	public List<Class<?>> find() throws IOException{
		Enumeration<java.net.URL> resources =  this.getClass().getClassLoader().getResources(packageName.replace('.','/')); // TODO : Not found packageName
		while(resources.hasMoreElements()) findClasses(new File(resources.nextElement().getFile()),packageName);
		return list;
	}
	
	private void findClasses(File directory,String packageName){
		File[] files = directory.listFiles();
		for (File file : files){
			String name = file.getName();
			if(file.isDirectory()) findClasses(file,packageName+"."+name);
			else if(name.endsWith(".class")) {
				try{
					list.add(Class.forName(packageName+'.'+name.substring(0,name.length()-6)));
				}catch (ClassNotFoundException e){
					e.initCause(new Exception(name));
					e.printStackTrace();
				}
			}
		}
	}
	
}
