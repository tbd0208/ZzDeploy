package zx.deploy.config;

import static zx.deploy.config.DistributeLevel.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import zz.server.ftp.*;
import zz.util.QQMap;

public class Config {

	/* 로컬 설정 */
	static final public String 
		MYWEB_LOG_PRE_PATH // 로그패스
		,SERVER_BACKUP_PRE_PATH // 백업위치
		,BOOK_MARK_PATH
	;
	
	/* 로컬 프로젝트 정보 */
	final public static String	
		LOCAL_PROJECT_PRE_PATH
		,WORKINGSET_XML_PATH
	;
	
	static final public QQMap PROJECT_INFO_MAP = new QQMap();
	static{
//		Map configMap = new HashMap<String, Object>((Map<String, Object>) new Yaml().load(new FileReader("D:\\MY_WKS\\ZzDeploy\\res\\srcMng.yml")));
//		System.out.println(configMap);
		
		LoaderOptions loaderOptions = new LoaderOptions();
		SrcMngConfig config = null;
		URL resource = Config.class.getClassLoader().getResource("../srcMng.yml");
		try{
			config = (SrcMngConfig)new Yaml(new Constructor(SrcMngConfig.class,loaderOptions)).load(new FileReader(resource.getPath()));
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}
		
		MYWEB_LOG_PRE_PATH = config.MYWEB_LOG_PRE_PATH;
		SERVER_BACKUP_PRE_PATH = config.SERVER_BACKUP_PRE_PATH;
		BOOK_MARK_PATH = config.BOOK_MARK_PATH;
		LOCAL_PROJECT_PRE_PATH = config.LOCAL_PROJECT_PRE_PATH;
		WORKINGSET_XML_PATH = config.WORKINGSET_XML_PATH;
/*
		ProjectInfo - 오토인사이드,자차
			DistributeInfo
				WebFtpClient
					ServerInfo - 
						개발계,운영계
						WEB(정적파일),WAS(batch,동적파일)
						ServerGroup - AI(오토인사이드 서버),SC(자차 서버)
*/
//		final String localProjectNames
		final String // FTP Server Group
			AI = "AI", // 오토인사이드 서버
	//		SVR_GP_AH = "AH",
			SC = "SC" // 자차 서버
		;
		
		final String // FTP Prepath
			AI_WEB_PATH = "/data/src/web/", // 오토인사이드 서버 웹
			AI_WAS_PATH = "/data/src/was/", // 오토인사이드 서버 와스
			AI_BAT_PATH = "/data/src/", // 오토인사이드 서버 배치
			SC_ONE_PATH = "/DATA/src/was/" // 자차
		;
		
		final WebFtpClientInfo 
			AI_DEV_ONE00 = new WebFtpClientInfo("AI_DEV_ONE00","192.168.1.150 ".trim()),
			AI_OPE_WAS01 = new WebFtpClientInfo("AI_OPE_WAS01","172.30.0.8    ".trim()),
			AI_OPE_WAS02 = new WebFtpClientInfo("AI_OPE_WAS02","172.30.0.10   ".trim()),
			AI_OPE_WEB01 = new WebFtpClientInfo("AI_OPE_WEB01","117.52.102.69 ".trim()),
			AI_OPE_WEB02 = new WebFtpClientInfo("AI_OPE_WEB02","117.52.102.72 ".trim()),
			SC_OPE_ONE00 = new WebFtpClientInfo("SC_OPE_ONE00","192.168.254.32".trim())
//			AH_OPE_ONE00 = new WebFtpClientInfo("AH_OPE_ONE00","192.168.254.24"),
		;
		
		String[] batAccount = config.login.get("BAT");
		String[] wasAccount = config.login.get("WAS");
//		System.out.println(config);
//		System.out.println(config.login.get("BAT")[0]);
		
		final WebFtpClient[] 
			AI_DEV_BAT = {new ApacheWebFtpClient(AI_DEV_ONE00,batAccount[0],batAccount[1])},
			AI_OPE_BAT = {new ApacheWebFtpClient(AI_OPE_WAS02,batAccount[0],batAccount[1])},
			AI_DEV_ONE = {new ApacheWebFtpClient(AI_DEV_ONE00,wasAccount[0],wasAccount[1])},
			AI_OPE_WEB = {new ApacheWebFtpClient(AI_OPE_WEB01,wasAccount[0],wasAccount[1]),new ApacheWebFtpClient(AI_OPE_WEB02,wasAccount[0],wasAccount[1])},
			AI_OPE_WAS = {new ApacheWebFtpClient(AI_OPE_WAS01,wasAccount[0],wasAccount[1]),new ApacheWebFtpClient(AI_OPE_WAS02,wasAccount[0],wasAccount[1])},
			SC_OPE_ONE = {new SecretWebFtpClient(SC_OPE_ONE00,wasAccount[0],wasAccount[1])}
		;
		
		final DistributeInfo 
			DI_SC_ONE = new DistributeInfo(SC,SC_ONE_PATH).put(OPE,SC_OPE_ONE),
			DI_AI_BAT = new DistributeInfo(AI,AI_BAT_PATH).put(OPE,AI_OPE_BAT).put(DEV,AI_DEV_BAT),
			DI_AI_WEB = new DistributeInfo(AI,AI_WEB_PATH).put(OPE,AI_OPE_WEB).put(DEV,AI_DEV_ONE),
			DI_AI_WAS = new DistributeInfo(AI,AI_WAS_PATH).put(OPE,AI_OPE_WAS).put(DEV,AI_DEV_ONE),
			DI_AI_WEBP= new DistributeInfo(AI,AI_WEB_PATH).put(OPE,AI_OPE_WEB).put(DEV,AI_DEV_ONE).put(STG,AI_OPE_WEB),
			DI_AI_WASP= new DistributeInfo(AI,AI_WAS_PATH).put(OPE,AI_OPE_WAS).put(DEV,AI_DEV_ONE).put(STG,AI_OPE_WAS)
		;
		
		final String classes = "/WEB-INF/classes";
		newProjectInfo("소매전산","AhRtc","rcar",  classes,DI_AI_WEBP,DI_AI_WASP).setServerProjectName(STG,"rcarp");
		newProjectInfo("오사","AiNf", "aifntp",classes,DI_AI_WEBP,DI_AI_WASP).setServerProjectName(STG,"aifntp_p");
		newProjectInfo("오사ON","AiOn", "aion",  classes,DI_AI_WEBP,DI_AI_WASP).setServerProjectName(STG,"aion_p");
		newProjectInfo("오사Auc","AiFnt","aiauc", classes,DI_AI_WEB, DI_AI_WAS);
		newProjectInfo("오사B/O","AiMgr","aimgr", classes,DI_AI_WEB, DI_AI_WAS);
		newProjectInfo("비인증","Ncert","ncert", classes,DI_AI_WEB, DI_AI_WAS);
		newProjectInfo("오토핸즈IR","AhFnt","ahir",  classes,DI_AI_WEB, DI_AI_WAS);
		newProjectInfo("배치","AiBch","batch", "",null, DI_AI_BAT);
		newProjectInfo("자차","SCAR02","scar", classes,null, DI_SC_ONE);
//		newProjectInfo("AiFnt","aifnt"	,classes	,DI_AI_WEB,DI_AI_WAS);
//		newProjectInfo("AiMcb","oshop"	,classes	,DI_AI_WEB,DI_AI_WAS);
//		newProjectInfo("Carnote","carnote"	,classes,DI_AI_WEB,DI_AI_WAS);
	}
	
	static private ProjectInfo newProjectInfo(
			String projectNicName
			,String projectName
			,String ftpProjectName 
			,String ftpClassesFolder
			,DistributeInfo web
			,DistributeInfo was
	){
		ProjectInfo projectInfo = new ProjectInfo(projectNicName,projectName,ftpProjectName,ftpClassesFolder,web,was);
		projectInfo.localPath = LOCAL_PROJECT_PRE_PATH+projectName;
		projectInfo.localSourceDeployMap = new HashMap<>();
		loadClassPathInfo(projectInfo);
		loadWstCommonComponent(projectInfo);
		PROJECT_INFO_MAP.put(projectName,projectInfo);
		return projectInfo;
	}
	
	private static void loadClassPathInfo(ProjectInfo projectInfo){
		Document doc = getDoc(projectInfo.localPath+"/.classpath");
		if(doc==null) return ;
		
		List<String> srcPaths = new ArrayList<>();
		String outputFile = null;
		
		NodeList childNodes = doc.getDocumentElement().getElementsByTagName("classpathentry");
		for(int i=0;i<childNodes.getLength();i++){
			Node item = childNodes.item(i);
			NamedNodeMap attributes = item.getAttributes();
			String kind = attributes.getNamedItem("kind").getNodeValue();
			switch(kind){
			case "src": srcPaths.add("/"+attributes.getNamedItem("path").getNodeValue()); break;
			case "output": outputFile = "/"+attributes.getNamedItem("path").getNodeValue(); break;
			}
		}
		
		System.out.println("[EclipseClasspath]"+projectInfo.name+".srcPaths:"+srcPaths+",.outputFile:"+outputFile);
		for(String srcPath : srcPaths) projectInfo.localSourceDeployMap.put(srcPath,outputFile);
		projectInfo.localClassFolder = outputFile;
	}
	
	private static void loadWstCommonComponent(ProjectInfo projectInfo){
		Document doc = getDoc(projectInfo.localPath+"/.settings/org.eclipse.wst.common.component");
		if(doc==null) return ;
		
		Map<String,String> sourceDeployMap = projectInfo.localSourceDeployMap;  
		String webRootFolder = null;
		
//		String defaultWebFolder = "";
		NodeList childNodes = doc.getDocumentElement().getElementsByTagName("wb-resource");
		for(int i=0;i<childNodes.getLength();i++){
			Node item = childNodes.item(i);
			NamedNodeMap attributes = item.getAttributes();
			String sourcePath = attributes.getNamedItem("source-path").getNodeValue();
			String deployPath = attributes.getNamedItem("deploy-path").getNodeValue();
			
			if(deployPath.equals("/")){
				Node tag = attributes.getNamedItem("tag");
				if(tag!=null && tag.getNodeValue().equals("defaultRootSource")) webRootFolder = sourcePath;
			}
//			if(deployPath.getNodeValue().equals("/")) defaultWebFolder = sourcePath.getNodeValue();
			sourceDeployMap.put(sourcePath,deployPath);
		}
		
		/*childNodes = doc.getDocumentElement().getElementsByTagName("property");
		for(int i=0;i<childNodes.getLength();i++){
			Node item = childNodes.item(i);
			NamedNodeMap attributes = item.getAttributes();
			Node name = attributes.getNamedItem("name");
			if(name.getNodeValue().equals("java-output-path")){
//				javaOutputPath = attributes.getNamedItem("value").getNodeValue();
//				System.out.println(javaOutputPath);
			}
		}*/
		
		if(webRootFolder==null) webRootFolder = "/";
		projectInfo.localWebFolder = webRootFolder;
		
		System.out.println("[EclipseWstCommonComponent]"+projectInfo.name+":.sourceDeployMap:"+sourceDeployMap+",.webRootFolder:"+webRootFolder);
	}
	
	private static Document getDoc(String path){
		File f = new File(path);
		if(!f.isFile()) return null; 
		try {
			return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(f);
		}catch (Exception e){
			e.printStackTrace();
		}
		return null;
	}
}