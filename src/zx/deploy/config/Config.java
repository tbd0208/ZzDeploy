package zx.deploy.config;

import static zx.deploy.config.DistributeLevel.STG;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import zz.server.ftp.ApacheWebFtpClient;
import zz.server.ftp.WebFtpClient;
import zz.server.ftp.WebFtpClientInfo;
import zz.util.QQMap;

public class Config {

	/* ### 로컬 설정 */
	static final public String 
		MYWEB_LOG_PRE_PATH // 로그패스
		,SERVER_BACKUP_PRE_PATH // 백업위치
		,BOOK_MARK_PATH // 북마크 패스 현재 안씀
	;
	
	/* ### 로컬 프로젝트 정보 */
	final public static String	
		LOCAL_PROJECT_PRE_PATH // 로컬 프로젝트 폴더 경로
		,WORKINGSET_XML_PATH // 이클립스 워킹셋 경로
	;
	
	static final public QQMap PROJECT_INFO_MAP = new QQMap();
	static{
		SrcMngConfig config = null;
		try{
			config = new Yaml().loadAs(new FileReader(Config.class.getClassLoader().getResource("../srcMng.yml").getPath()),SrcMngConfig.class);
			HashMap<String,List<String>> ftpConfig = new Yaml().load(new FileReader(Config.class.getClassLoader().getResource("../ftp.yml.p").getPath()));
			List<String> wsAdmFtpInfo = ftpConfig.get("WSADM");
//			List<String> aiBatFtpInfo = ftpConfig.get("AIBAT");
			for(WebFtpClient[] webFtpClients : config.ftpWebFtpClientsGroup){
				for(WebFtpClient webFtpClient : webFtpClients){
					webFtpClient.login(wsAdmFtpInfo.get(0),wsAdmFtpInfo.get(1));
				}
			}
			for(WebFtpClient[] webFtpClients : config.sftpWebFtpClientsGroup){
				for(WebFtpClient webFtpClient : webFtpClients){
					webFtpClient.login(wsAdmFtpInfo.get(0),wsAdmFtpInfo.get(1));
				}
			}
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}
		
		MYWEB_LOG_PRE_PATH = config.MYWEB_LOG_PRE_PATH;
		SERVER_BACKUP_PRE_PATH = config.SERVER_BACKUP_PRE_PATH;
		BOOK_MARK_PATH = config.BOOK_MARK_PATH;
		LOCAL_PROJECT_PRE_PATH = config.LOCAL_PROJECT_PRE_PATH;
		WORKINGSET_XML_PATH = config.WORKINGSET_XML_PATH;
		
		config.projectInfo.forEach(d->{
			ProjectInfo projectInfo = newProjectInfo((String)d[6],(String)d[0],(String)d[1],(String)d[2],(String)d[3],(DistributeInfo)d[4],(DistributeInfo)d[5]);
			
			System.out.println(projectInfo.name+" : ");
			projectInfo.localSourceDeployMap = new HashMap<>();
			setClassPathInfo(projectInfo);
			setWstCommonComponent(projectInfo);
		});
	}
	
	static private ProjectInfo newProjectInfo(
			String projectNicName
			,String projectName
			,String ftpProjectName
			,String ftpProjectNameStg
			,String ftpClassesFolder
			,DistributeInfo web
			,DistributeInfo was
	){
		ProjectInfo projectInfo = new ProjectInfo(projectNicName,projectName,ftpProjectName,ftpClassesFolder,web,was);
		PROJECT_INFO_MAP.put(projectName,projectInfo);
		if(ftpProjectNameStg!=null) projectInfo.setServerProjectName(STG,ftpProjectNameStg);
		return projectInfo;
	}
	
	private static void setClassPathInfo(ProjectInfo projectInfo){
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
		
		System.out.println("[EclipseClasspath] outputFile:"+outputFile+", srcPaths:"+srcPaths);
		for(String srcPath : srcPaths) projectInfo.localSourceDeployMap.put(srcPath,outputFile);
		projectInfo.localClassFolder = outputFile;
	}
	
	private static void setWstCommonComponent(ProjectInfo projectInfo){
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
		
		System.out.println("[EclipseWstCommon] webRootFolder : "+webRootFolder+", sourceDeployMap : "+sourceDeployMap);
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