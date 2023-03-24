package zx.deploy.dm.srcMng;

import static zx.deploy.base.Res.OK;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import zx.deploy.config.Config;
import zz.file.FileSystemVisitor;
import zz.file.FileUtils;
import zz.util.QQMap;
import zz.util.StringUtils;

public class ProjectFileMngController{
	
	public static void main(String[] args){
		FileUtils.visit("C:\\MY_WORKSPACE\\AiMgr"
			,new FileSystemVisitor(){
				public boolean filterFile(File f){
					String name = f.getName();
					return name.charAt(0)!='.' && !name.equals("classes") && !name.equals("lib");
				}
				public boolean visitDirectory(File f) {
//					if(before!=null) map.put(before,i);
//					before = f.getPath();
//					i=0;
					System.out.println(f.getPath() + " : " +f.list().length);
					return true;
				}
				public boolean visitFile(File f) {
//					i++;
					return true;
				}
			}
		);
	}
	
	public Object getBookmarks() throws IOException {
		List<String> readLines = FileUtils.readLines(Config.BOOK_MARK_PATH);
		
		List<QQMap> data = new ArrayList<>();
		for (String line : readLines){
			String[] ws = line.split("\t");
			data.add(new QQMap("name",ws[0],"path",ws[1]));
		}
		return OK(data);
	}
	
//	public Object getProjects() {
//		return OK(Config.PROJECT_INFO_MAP);
//	}
	
	final static private String[] DF_DIRS = new String[] {
		"WebContent\\WEB-INF\\lib"
		,"WebContent\\WEB-INF\\classes"
		,"target"
	};
	final static private QQMap PJ_DIRS_MAP = new QQMap();
	static {
		PJ_DIRS_MAP.put("AiNf",new String[] {
			"WebContent\\sttc"
			,"WebContent\\sttc\\ad\\sellcar"
			,"WebContent\\images"
			,"WebContent\\img"
			,"WebContent\\search"
			,"WebContent\\html"
			,"WebContent\\fonts"
		});
		PJ_DIRS_MAP.put("AiFnt",new String[] {
			"WebContent\\sttc"
			,"WebContent\\sttc\\ad\\sellcar"
			,"WebContent\\images"
			,"WebContent\\banner"
			,"WebContent\\img"
			,"WebContent\\search"
			,"WebContent\\html"
			,"WebContent\\front"
			,"WebContent\\fonts"
		});
		PJ_DIRS_MAP.put("AiMgr",new String[] {
			"src\\com\\autoinside\\framework"
			,"src\\com\\tmile"
						
			,"WebContent\\images"
			,"WebContent\\ckeditor"
			,"WebContent\\daumeditor"
			,"WebContent\\editor"
			,"WebContent\\newRoadZipCode"
			,"WebContent\\WEB-INF\\admin_html"
			
			,"WebContent\\js\\carnote_jfupload"
			,"WebContent\\js\\carImage"
			,"WebContent\\js\\editor"
			,"WebContent\\js\\jfupload"
			,"WebContent\\js\\jstree"
			,"WebContent\\js\\layerslider"
			,"WebContent\\js\\upload"
			
			,"src\\com\\autoinside\\admin\\controller\\hermes"
			,"src\\com\\autoinside\\admin\\service\\hermes"
			,"WebContent\\WEB-INF\\view\\hermes"
			
			,"src\\com\\autoinside\\admin\\controller\\carnote"
			,"src\\com\\autoinside\\admin\\service\\carnote"
			,"WebContent\\WEB-INF\\view\\carnote"
			
			,"WebContent\\ai_upload"
		});
	}
	public Object getFolders(String path,Integer limitDays) {
		
		
		long timeInMillis = 0;
//		
		if(limitDays!=null) {
			Calendar cld = Calendar.getInstance();
			cld.add(Calendar.DATE,-limitDays);
			timeInMillis = cld.getTimeInMillis();
		}
		
		final long s = timeInMillis;
		
		List<QQMap> list = new ArrayList<>();
		
		list.add(toMapDir("<<default>>",path));
		String[] dirs = PJ_DIRS_MAP.val(StringUtils.getLastIndexOfString(path,'\\'));
		if(dirs!=null) for(String dir : dirs) {
			String subPath = path+"\\"+dir;
//			List<File> subDirList = new ArrayList<>(Arrays.asList(new File(subPath).listFiles()));
			List<File> subDirList = new ArrayList<>();
			subDirList.add(new File(subPath));
			int n = 0;
			int limitCnt = 0;
			for(int i=0;i<subDirList.size();i++) {
				File file = subDirList.get(i);
				File[] subFiles = file.listFiles();
				if(subFiles==null) continue;
				try {
					for(File f : subFiles){
						if(f.getName().charAt(0)=='.') continue;
						if(f.isDirectory()) subDirList.add(f);
						else {
							n++;
							if(f.lastModified() > s) limitCnt++;
						}
					}
				}catch(Exception e) {
					e.printStackTrace();
				}
			}
			list.add(toMapDir(dir,subPath).take("count",n>99?99:(n<10?"0"+n:n)).take("limitCount",limitCnt));
		}
		
		for(String f : DF_DIRS) list.add(toMapDir(f,path+"\\"+f));
				
		return OK(list);
	}
	
	public Object getRecentFiles(String path,Integer limitDays) {
//		long ss = System.currentTimeMillis();
//		limitDays = 30;
		List<QQMap> list = new ArrayList<>();
		long timeInMillis = 0;
//		
		if(limitDays!=null) {
			Calendar cld = Calendar.getInstance();
			cld.add(Calendar.DATE,-limitDays);
			timeInMillis = cld.getTimeInMillis();
		}
		
		String pn = getProjectName(path);
		HashSet<String> filterPaths = new HashSet<>();
		String[] pjDirs = PJ_DIRS_MAP.val(pn);
		if(pjDirs!=null) for(String ff : pjDirs) filterPaths.add(Config.LOCAL_PROJECT_PRE_PATH+pn+"\\"+ff);
		for(String ff : DF_DIRS) filterPaths.add(Config.LOCAL_PROJECT_PRE_PATH+pn+"\\"+ff);
		filterPaths.remove(path);
		
		final long s = timeInMillis;
		FileUtils.visit(path,new FileSystemVisitor(){
				List<QQMap> subList;
				QQMap map;
				String currentDir = null;
				public boolean visitDirectory(File f) {
					if(f.getName().charAt(0)=='.') return false;
					String path = f.getPath();
					if(filterPaths.contains(path)) return false;
					map = null;
					currentDir = path;
					return true;
				}
				public boolean filterFile(File f){
					if(f.lastModified() > s) {
						String fileName = f.getName();
						return fileName.charAt(0)!='.' && !fileName.endsWith(".sql");
					}
					return false;
				}
				public boolean visitFile(File f) {
					if(map==null) {
						map = new QQMap();
						String subPath = currentDir.substring(path.length());
						map.put("name",subPath);
						map.put("subList",subList = new ArrayList<>());
						list.add(map);
					}
					subList.add(toMapFile(f).take("note",UploadDateLog.getDate(f)));
					return true;
				}
			}
		);
		return OK(list);
	}
	
	private QQMap toMapFile(File f) {
		return new QQMap("name",f.getName(),"path",f.getPath(),"lastModified",f.lastModified(),"size",f.length());
	}
	private QQMap toMapDir(String name,String path) {
		return new QQMap("name",name,"path",path,"isDirectory",true);
	}
	private String getProjectName(String path) {
		int a = Config.LOCAL_PROJECT_PRE_PATH.length();
		int b = path.indexOf('\\',a+1);
		if(a<b) return path.substring(a,b);
		return path.substring(a);
	}
	public Object projectFileMng_init() throws IOException {
		return OK();
	}
	
	public Object getLastFile() {
		return OK();
	}
}
