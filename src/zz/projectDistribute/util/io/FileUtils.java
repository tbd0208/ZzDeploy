package zz.projectDistribute.util.io;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.io.Util;

import zz.projectDistribute.util.Visitor;

public class FileUtils {

	static public FileFilter fileFilter = new FileFilter() {
		public boolean accept(File f){
			return f.isFile();
		}
	};
	
	/**
	 * visit
	 */
	/*static public void visitFiles(String path,Visitor<File> visitor){
		visitFiles(new File(path),visitor);
	}
	static public void visitFiles(File parentFolder,Visitor<File> visitor){
		for(File file : parentFolder.listFiles()){
			if(visitor.visite(file)) continue;
			return ;
		}
	}*/
	
	/**
	 * deepVisit
	 */
	/*static public void deepVisitFiles(String path,Visitor<File> visitor){
		deepVisitFiles(new File(path),visitor);
	}
	static public void deepVisitFiles(File pFile,Visitor<File> visitor){
		if(pFile.listFiles()==null) return ;
		for(File file : pFile.listFiles()){
			if(file.isDirectory()){
				deepVisitFiles(file,visitor);
			}else{
				if(visitor.visite(file)) continue;
				return ;
			}
		}
	}*/
	/*static public void deepVisitAll(String path,Visitor<File> fileVisitor,Visitor<File> directoryVisitor){
		deepVisitFiles(new File(path),fileVisitor,directoryVisitor);
	}
	static public void deepVisitAll(String path,Visitor<File> visitor){
		deepVisitFiles(new File(path),visitor,visitor);
	}
	static public void deepVisitFiles(File pFile,Visitor<File> visitor,Visitor<File> directoryVisitor){
		if(pFile.listFiles()==null) return ;
		File[] listFiles = pFile.listFiles();
		int i = 0;
		for(;i<listFiles.length;i++){
			File file = listFiles[i];
			if(file.isDirectory()) break;
			if(!visitor.visite(file)) return;
		}
		for(;i<listFiles.length;i++){
			File file = listFiles[i];
			if(directoryVisitor.visite(file)) deepVisitFiles(file,visitor,directoryVisitor);
		}
	}*/
	
	static public void visit(String path,FileSystemVisitor visitor){
		visit(new File(path),visitor);
	}
	static public void visit(File pFile,FileSystemVisitor visitor){
		if(!visitor.visitDirectory(pFile)) return ;
		File[] listFiles = pFile.listFiles();
		if(listFiles==null || listFiles.length==0) return ;
		List<File> dirList = new ArrayList<>();
		for (int i=0;i<listFiles.length; i++){
			File file = listFiles[i];
			if(file.isFile()) {
				if(visitor.filterFile(file)) if(!visitor.visitFile(file)) return;
			}else if(file.isDirectory()) {
				 dirList.add(file);
			}
		}
		for(File file : dirList){
			visit(file,visitor);
		}
	}

	/**
	 * save
	 */
	static public void save(String path,InputStream inputStream) throws IOException{
		FileOutputStream fileOutputStream = new FileOutputStream(new File(path));
		Util.copyStream(inputStream,fileOutputStream);
		fileOutputStream.close();
	}
	
	/**
	 * search
	 */
	static public void searchOfContent(String fileName,Visitor<String> visitor) throws IOException {
		BufferedReader reader=new BufferedReader(new FileReader(fileName));
		String line=null;
		while((line=reader.readLine())!=null && visitor.visite(line)) ;
		reader.close();
	}
	/*static public List<File> searchFileListEqual(String path,final String keyword, FileConverter<?> fileConverTer) throws IOException {
		final List<File> searchList = new ArrayList<>();
		deepVisitFiles(path,new Visitor<File>() {
			public boolean visite(File t){
				String name = t.getName();
				if(!name.equals(keyword)) return true;
				searchList.add(t);
				return false;
			}
		});
		return searchList;
	}*/
	
	/**
	 * readLines
	 */
	static public List<String> readLines(String fileName) throws IOException {
		return readLines(fileName,Charset.defaultCharset().toString());
	}
	static public List<String> readLines(String fileName,String charsetName) throws IOException {
		return readLines(fileName,charsetName,null);
	}
	static public List<String> readLines(String fileName,String charsetName,String trimRegExp) throws IOException {
		BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(fileName),charsetName));
		List<String> list = new ArrayList<String>();
		String line=null;
		if(trimRegExp==null) while((line=reader.readLine())!=null) list.add(line);
		else while((line=reader.readLine())!=null) {
			line = line.replaceAll(trimRegExp,"");
			if(line.isEmpty()) continue;
			list.add(line); 
		}
		reader.close();
		return list;
	}
	
	/**
	 * toString
	 */
	static public String toString(String fileName){
		return toString(new File(fileName));
	}
	
	static public String toString(File file){
		return toString(file,"UTF-8");
	}
	
	static public String toString(String fileName,String charsetName){
		return toString(new File(fileName),charsetName);
	}
	static public String toString(File file,String charsetName){
		try(
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),charsetName))
			){
			
			String line=null;
			StringBuilder stringBuilder=new StringBuilder();
			String ls=System.getProperty("line.separator");
			while((line=reader.readLine())!=null){
				stringBuilder.append(line);
				stringBuilder.append(ls);
			}
			try{
				reader.close();
			}catch(IOException e){
				e.printStackTrace();
			}
			return stringBuilder.toString();
			
		}catch(IOException e){
			throw new RuntimeException(e);
		}
	}
	/*public static Map<String,String> toStringFiles(String path,final String charsetName,boolean deep){
		
		File root = new File(path);
		
		final HashMap<String,String> map = new HashMap<>();
		Visitor<File> visitor = new Visitor<File>() {
			public boolean visite(File t){
				String name = t.getName();
				String body = FileUtils.toString(t,charsetName);
				map.put(name,body);
				return true;
			}
		};
		
		if(deep){
			visitFiles(root,visitor);
		}else{
			deepVisitFiles(root,visitor);
		}
		return map;
	}
	public static Map<String,String[]> toStringFiles(String path,final String charsetName,final String split,boolean deep){
		
		File root = new File(path);
		
		final HashMap<String,String[]> map = new HashMap<>();
		Visitor<File> visitor = new Visitor<File>() {
			public boolean visite(File t){
				String name = t.getName();
				String[] body = FileUtils.toString(t,charsetName).split(split);
				map.put(name,body);
				return true;
			}
		};
		
		if(deep){
			visitFiles(root,visitor);
		}else{
			deepVisitFiles(root,visitor);
		}
		return map;
	}*/
	public static String toStringForFolderStructure(String folder){
		return toStringForFolderStructure(new File(folder));
	}
	public static String toStringForFolderStructure(File folder){
		final StringBuilder sb = new StringBuilder();
		(new Visitor<File>() {
			int level = 0;
			public boolean visite(File t){
				for(File sub : t.listFiles()){
					
					for(int i=0;i<level;i++) sb.append('-'); 
					sb.append(sub.getPath());
					sb.append('\n');
					
					if(sub.isDirectory()){
						level++;
						this.visite(sub);
						level--;
					}
				}
				
				return true;
			}
		}).visite(folder);
		return sb.toString();
	}
	
}
