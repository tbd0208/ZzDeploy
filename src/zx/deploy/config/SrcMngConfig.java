package zx.deploy.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import org.yaml.snakeyaml.Yaml;

import zz.server.ftp.ApacheWebFtpClient;
import zz.server.ftp.SecretWebFtpClient;

public class SrcMngConfig{
	
	public static void main(String[] args){
		try{
//			LoaderOptions loaderOptions = new LoaderOptions();
//			TypeDescription td = new TypeDescription(SrcMngConfig.class,"ServerInfo");
//			topologyDescription.addPropertyParameters(arg0,arg1);
//			td.addPropertyParameters("loginInfo",arg1);
//			Constructor constructor = new Constructor(td,loaderOptions);
//			new Constructor(arg0,arg1,arg2)
			SrcMngConfig config = (SrcMngConfig)new Yaml().loadAs(new FileReader("D:\\MY_WKS\\ZzDeploy\\WebApp\\WEB-INF\\srcMng.yml"),SrcMngConfig.class);
			System.out.println(config.deploy[0]);
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}
	}
	
//	public String[][] loginInfo;
	public ApacheWebFtpClient[][] ftpWebFtpClientsGroup;
	public SecretWebFtpClient[][] sftpWebFtpClientsGroup;
	public DistributeInfo[] deploy;
	public List<Object[]> projectInfo;
	
//	public Map<String,WebFtpClientInfo> WebFtpClientInfos;
	
	public String 
		MYWEB_LOG_PRE_PATH,
		SERVER_BACKUP_PRE_PATH,
		BOOK_MARK_PATH,
		
		LOCAL_PROJECT_PRE_PATH,
		WORKINGSET_XML_PATH
	;
	
//	public Object[] tmpInfo;
}
