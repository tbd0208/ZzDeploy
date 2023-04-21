package zx.deploy.dm.srcMng;

import static zx.deploy.base.Res.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import zx.deploy.config.Config;
import zx.deploy.config.DistributeInfo;
import zx.deploy.config.DistributeLevel;
import zx.deploy.config.DsRule;
import zx.deploy.config.ProjectInfo;
import zx.deploy.util.QQFileLogger;
import zz.server.ftp.WebFtpClient;
import zz.tool.eclipse.ProjectFile;
import zz.tool.eclipse.Workingset;
import zz.util.QQMap;
import zz.util.StringUtils;

public class SrcMngController{
	
	final private Logger sMngLog = QQFileLogger.getLogger(Config.MYWEB_LOG_PRE_PATH+"srcMng.log",true);
	final private WorkingsetMng wsMng = new WorkingsetMng(Config.WORKINGSET_XML_PATH);
	private final UploadDateLog uploadDateLog;
	
	public SrcMngController(UploadDateLog uploadDateLog){
		this.uploadDateLog = uploadDateLog;
		wsMng.init();
	}
	
	public QQMap main(){
		return new QQMap("projectInfoMap",Config.PROJECT_INFO_MAP);
	}
	
	public QQMap main_vue(){
		return new QQMap("projectInfoMap",Config.PROJECT_INFO_MAP);
	}
	
	
	public Object test22(String v) {
		return OK(v);
	}
	
	/*
	 *  Workingset Group
	 *    Workingsets
	 *      ProjectFiles
	 */
	public Object workingset_init() {
		wsMng.init();
		return OK();
	}
	public Object getWorkingsetGroupNames(){
		return OK(wsMng.workingsetsGroupMap.keySet());
	}
	public Object getWorkingsetGroup(String v){
		List<Workingset> workingsetGroup = wsMng.getWorkingsetGroup(v);
		for (Workingset workingset : workingsetGroup){
			List<ProjectFile> projectFiles = workingset.getProjectFiles();
			for (ProjectFile projectFile : projectFiles){
				Object date = uploadDateLog.getDate(projectFile);
				projectFile.setNote(date==null?"":String.valueOf(date)); // set 
			}
		}
		return OK(workingsetGroup);
	}
	public Object getWorkingsets(){
		wsMng.init();
		return OK(wsMng.workingsetsMap);
	}
	public Object getWorkingset(String id){
		wsMng.init();
		return OK(wsMng.workingsetsMap.get(id));
	}
	
	/**
	Workingset file url -> LocalPath(java->class),ServerPath,DistributeInfo
	DistributeInfo(distributeLevel) -> WebFtpClients
	do upload(LocalPath,ServerPath,WebFtpClients)
		ProjectInfo(webFolder,outputFile,sourceDeployMap) 
	 */
	public Object upload(String distributeLevel,String path,String group,Boolean hasUpload,Boolean hasBackup,Boolean makeDirectory){
		
		Map<String, Object> res = new LinkedHashMap<>();
		
		try{
			Object[] a = genUploadPathInfo3(path);
			ProjectInfo projectInfo = (ProjectInfo)a[0];
			DistributeInfo distributeInfo = (DistributeInfo)a[1];
			String localPath = (String)a[2];
			String packagePath = (String)a[3];
			String projectPath = (String)a[4]; 
			
			final QQMap distributeLevelsRes = new QQMap();
			final StringBuilder logMsg = new StringBuilder().append(distributeInfo.getServerGroupName()).append('/').append(distributeLevel);
					
			logMsg.append('[');
			
			for(DistributeLevel dLevel : DistributeLevel.values(distributeLevel)){
				
				WebFtpClient[] webFtpClients = distributeInfo.get(dLevel);
				if(webFtpClients==null||webFtpClients.length==0) continue;
				
				final boolean isOpe = dLevel==DistributeLevel.OPE;
				final String ftpPath = distributeInfo.getPrePath() + projectInfo.getServerProjectName(dLevel) + packagePath;
				
				final QQMap dRes = distributeLevelsRes.sub(dLevel.toString()).take("ftpPath",ftpPath);
				final QQMap servers = dRes.sub("servers");
				
				for(WebFtpClient webFtpClient : webFtpClients){
					QQMap subMap = servers.sub(webFtpClient.getServerName());
					String status = "INIT";
					String backupMsg = null;
					String backupPath = null;
					String sendMsg = null;
					
					if(webFtpClient.isDisconnected() && !webFtpClient.connect()) status = "CONNET_FAIL";
					else{
						if(isOpe && hasBackup){
							backupPath = Config.SERVER_BACKUP_PRE_PATH + webFtpClient.getServerName() + ftpPath + "_BU";
							subMap.put("backupPath",backupPath);
							/* BACKUP */
							backupMsg = backupWithBefore(webFtpClient,ftpPath,backupPath);
							status = "BKUP";
						}
						if(hasUpload && (!hasBackup || backupMsg==null || backupMsg==WebFtpClient.DOWNLOAD_OK || backupMsg==WebFtpClient.DOWNLOAD_FAIL_NOT_OVERWITE || backupMsg==WebFtpClient.DOWNLOAD_FAIL_NO_EXIT)){
							/* UPLOAD */
							sendMsg = send(makeDirectory,webFtpClient,localPath,ftpPath);
							status = "SEND";
						}
	//					webFtpClient.disconnect();
					}
					
					backupMsg = backupMsg==null?"NONE":backupMsg;
					sendMsg = sendMsg==null?"NONE":sendMsg;
					
//					subMap.put("ftpPath",ftpPath);
					subMap.put("backupMsg",backupMsg==null?"NONE":backupMsg);
					subMap.put("sendMsg",sendMsg);
					subMap.put("status",status);
					logMsg.append(sendMsg);
				}
				
//				logMsg(dLevel,path,group,distributeInfo.getServerGroupName(),servers,webFtpClients);
				if(isOpe) uploadDateLog.put(projectPath);
			}
			
			logMsg.append(']').append('[').append(group).append("] ").append(path);
			sMngLog.info(logMsg.toString());
			
			/* UPLOAD */
			res.put("distributeLevels",distributeLevelsRes);
			res.put("localPath",localPath);
						
		}catch (Exception e){
			if("checkJavaCompile".equals(e.getMessage())) res.put("checkJavaCompile",false);
			else e.printStackTrace();
		}
		
		return OK(res);
	}
	
	private Object[] genUploadPathInfo3(String path) throws Exception {
		/*
		 *  1. URL분해 : [/]projectName/소스폴더/패키지패스
		 *  원본파일 : localPath/소스폴더
		 *  배포파일 : localPath/배포폴더
		 *  서버파일 : 
		 */
		
		String localPath = null;
		String projectName = null;
		String projectBody = null;
		
		// check window file path
		if(path.charAt(1)==':') {
			// window path ex) C:\test.txt
			localPath = path; 
			path = localPath.substring(Config.LOCAL_PROJECT_PRE_PATH.length());
			projectName = path.substring(0,path.indexOf('\\',1)); // 프로젝트명
			projectBody = path.substring(projectName.length()).replaceAll("\\\\","/");
		}else{
			// eclipse project path
			localPath = Config.LOCAL_PROJECT_PRE_PATH+path;
			projectName = path.substring(0,path.indexOf('/',1)); // 프로젝트명
			projectBody = path.substring(projectName.length()).replaceAll("\\\\","/").replaceAll("//","/");
			if(projectName.startsWith("/")) projectName=projectName.substring(1);
		}
		
		// 2. 프로젝트명 확인 및 프로젝트 정보 구하기
		ProjectInfo projectInfo = Config.PROJECT_INFO_MAP.val(projectName); // 프로젝트정보
		if(projectInfo==null) throw new Exception("Bad projecName : " + projectName + ", not in " + Config.PROJECT_INFO_MAP.keySet());
		
		// 3. 소스폴더 및 배포폴더 획득
		final String sourceFolder = findKeyStartsWith(projectInfo.localSourceDeployMap,projectBody); // 소스폴더(src)
		final String deployFolder = projectInfo.localSourceDeployMap.get(sourceFolder); // 배포폴더 (WebContent(jsp,html):/, src(java,xml):/WEB-INF/classes)
		String packagePath = projectBody.substring(sourceFolder.length()); // 패케이지패스 - 소스폴더제거
		
		if(projectBody.endsWith(DsRule.EXTENSION_JAVA)){
			String javaPath = localPath; 
			packagePath = StringUtils.chgTail(packagePath,DsRule.EXTENSION_JAVA,DsRule.EXTENSION_CLASS); // .java -> .class 
			localPath = Config.LOCAL_PROJECT_PRE_PATH + projectName + projectInfo.localClassFolder + packagePath;
			if(!checkJavaCompile(javaPath,localPath)) throw new Exception("checkJavaCompile");
		}
		
//		localPath = localPath.replaceAll("\\\\","/");
		
		final boolean isWebContents = deployFolder.equals("/"); // 배포폴더가 root인경우 ) 
		final boolean isStatic = isWebContents && !packagePath.startsWith("/WEB-INF") && !DsRule.checkDynamicFileExtension(projectBody); // 정적(웹)파일(html,js,css) 또는 동적파일(jsp,java,xml)
		final DistributeInfo distributeInfo = projectInfo.hasWeb()&&isStatic?projectInfo.web:projectInfo.was;
		
		return new Object[]{
				projectInfo
				,distributeInfo
				,localPath
				,(isWebContents?"":projectInfo.severClassesFolder) + packagePath // ftp
				,projectName+projectBody
		};
	}
	
	/**
	 * url에 해당 하는 파일을 프로젝트서버에서 같은 경로로 _COPY를 붙여 다운받음 
	 */
	public String makeBackupFile(String path) throws IOException{
		
		String msgs = "";
		try{
			Object[] a = genUploadPathInfo3(path);
			ProjectInfo projectInfo = (ProjectInfo)a[0];
			DistributeInfo distributeInfo = (DistributeInfo)a[1];
//			String localPath = (String)a[2];
			String packagePath = (String)a[3];
			
			WebFtpClient[] webFtpClients = distributeInfo.get(DistributeLevel.OPE);
			
			String ftpPath = distributeInfo.getPrePath() + projectInfo.getServerProjectName(DistributeLevel.OPE) + packagePath;
			
			for(int i=0;i<webFtpClients.length;i++){
				
				WebFtpClient webFtpClient = webFtpClients[i];
				
//				if(webFtpClient.isDisconnected() && !webFtpClient.connect()) return "CONNET_FAIL";
				if(!webFtpClient.connect()) throw new RuntimeException();
				
				String backupPath = Config.SERVER_BACKUP_PRE_PATH + webFtpClient.getServerName() + ftpPath + "_BU";
				String backupMsg = backupWithBefore(webFtpClient,ftpPath,backupPath,BTF_TIME);
				
				webFtpClient.disconnect();
				
				msgs += "["+backupMsg+"]";
			}
			
		}catch (Exception e){
			e.printStackTrace();
		}
		return msgs;
	}
	
	/**
	 * url에 해당 하는 파일을 프로젝트서버에서 같은 경로로 _COPY를 붙여 다운받음 
	 */
	public String downFromServer(String path,String distributeLevel) throws IOException{
		
		String msgs = "";
		try{
			Object[] a = genUploadPathInfo3(path);
			ProjectInfo projectInfo = (ProjectInfo)a[0];
			DistributeInfo distributeInfo = (DistributeInfo)a[1];
			String localPath = (String)a[2];
			String packagePath = (String)a[3];
			
			DistributeLevel[] distributeLevels = DistributeLevel.values(distributeLevel);
			for(DistributeLevel d : distributeLevels){
				
				WebFtpClient[] webFtpClients = distributeInfo.get(d);
				if(webFtpClients==null||webFtpClients.length==0) continue;
				
				String ftpPath = distributeInfo.getPrePath() + projectInfo.getServerProjectName(d) + packagePath;
				
				for(int i=0;i<webFtpClients.length;i++){
					
					WebFtpClient webFtpClient = webFtpClients[i];
//					if(webFtpClient.isDisconnected() && !webFtpClient.connect()) return "CONNET_FAIL";
					if(!webFtpClient.connect()) throw new RuntimeException();
					
					String msg = null;
					try {
						msg = webFtpClient.download(ftpPath,localPath+"_COPY_"+d.name()+"0"+i,true);
					} catch (IOException e1) {
						msg = e1.getMessage();
					}
					
					webFtpClient.disconnect();
					
					msgs += "["+msg+"]";
				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return msgs;
	}
	
	private boolean checkJavaCompile(String javaPath,String classPath){
		File javaFile = new File(javaPath);
		File classFile = new File(classPath);
		return javaFile.lastModified()<classFile.lastModified();
	}
		
	private String send(boolean makeDirectory,WebFtpClient webFtpClient,String localPath,String ftpPath){
		try {
			return webFtpClient.send(ftpPath,new FileInputStream(localPath),makeDirectory)?"O":"X";
		} catch (FileNotFoundException e1) {
			return e1.getMessage();
		}
	}
	
	static final String BTF_DAY = "yyyy-MM-dd";
	static final String BTF_TIME = "yyyy-MM-dd HHmmss";
	
	private String backupWithBefore(WebFtpClient webFtpClient,String ftpPath,String backupPath) {
		return backupWithBefore(webFtpClient,ftpPath,backupPath,BTF_DAY);
	}
	private String backupWithBefore(WebFtpClient webFtpClient,String ftpPath,String backupPath,String btf){
		try {
			File backupFile = new File(backupPath);
			if(backupFile.exists()){
				// 최근 백업파일 이름변경 
				File backupFileBackup = new File(backupFile.getPath()+"_"+new SimpleDateFormat(btf).format(backupFile.lastModified()));
				backupFile.renameTo(backupFileBackup); // 중복명이 있는 경우 백업무시/이름변경불가
			}
			return webFtpClient.download(ftpPath,backupPath,true);
		} catch (IOException e1) {
			return e1.getMessage();
		}
	}
	
	static private String findKeyStartsWith(Map<String,String> map, String v) {
		for(String source : map.keySet()) if(v.startsWith(source)) return source;
		return null;
	}
}