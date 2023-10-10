package zx.deploy.dm.srcMng;

import static zx.deploy.base.Res.*;

import java.io.IOException;

import com.jcraft.jsch.JSchException;

import zx.deploy.config.Config;
import zx.deploy.config.DistributeLevel;
import zx.deploy.config.ProjectInfo;
import zz.server.cmd.ClientCmdUtil;
import zz.server.ftp.WebFtpClient;

public class SvrCmdController{

	public Object touch(String distributeLevel,String project) throws JSchException, IOException{
		ProjectInfo projectInfo = Config.PROJECT_INFO_MAP.val(project);
		for(DistributeLevel d : DistributeLevel.values(distributeLevel)){
			String serverProjectName = projectInfo.getServerProjectName(d);
			String cmd = "find /data/src/was/" + serverProjectName +  "/WEB-INF -name '*.jsp' | xargs touch";
			WebFtpClient[] webFtpClients = projectInfo.was.get(d);
			if(webFtpClients==null) continue;
			for(WebFtpClient webFtpClient : webFtpClients){
				ClientCmdUtil.sendCommend(webFtpClient.getIp(),"wsadm","ai1234ai",cmd);
			}
		}
		return OK("touch:"+distributeLevel + ", " + project);
	}
}