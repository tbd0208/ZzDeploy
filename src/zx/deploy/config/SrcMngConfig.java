package zx.deploy.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import zz.server.ftp.ApacheWebFtpClient;
import zz.server.ftp.SecretWebFtpClient;
import zz.server.ftp.WebFtpClientInfo;

public class SrcMngConfig{
	
	public static void main(String[] args){
		LoaderOptions loaderOptions = new LoaderOptions();
		SrcMngConfig config = null;
		try{
			config = (SrcMngConfig)new Yaml(new Constructor(SrcMngConfig.class,loaderOptions)).load(new FileReader("D:\\MY_WKS\\ZzDeploy\\WebApp\\WEB-INF\\srcMng.yml"));
		}catch (FileNotFoundException e){
			e.printStackTrace();
		}
		System.out.println(config.WebFtpClientInfos[0]);
	}
	
	public String[][] loginInfo;
	public WebFtpClientInfo[] WebFtpClientInfos;
	public ApacheWebFtpClient[][] ftpWebFtpClient;
	public SecretWebFtpClient[][] sftpWebFtpClient;
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
}
