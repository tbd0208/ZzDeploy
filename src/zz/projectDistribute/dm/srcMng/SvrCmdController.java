package zz.projectDistribute.dm.srcMng;

import java.io.IOException;

import com.jcraft.jsch.JSchException;

import zz.projectDistribute.config.Config;
import zz.projectDistribute.config.DistributeLevel;
import zz.projectDistribute.config.ProjectInfo;
import zz.projectDistribute.dm.LinuxCmdUtil;
import zz.projectDistribute.util.io.ftp.WebFtpClient;

import static zz.projectDistribute.base.Res.*;

public class SvrCmdController{

	public Object touch(String distributeLevel,String project) throws JSchException, IOException{
		ProjectInfo projectInfo = Config.PROJECT_INFO_MAP.val(project);
		for(DistributeLevel d : DistributeLevel.values(distributeLevel)){
			String serverProjectName = projectInfo.getServerProjectName(d);
			String cmd = "find /data/src/was/" + serverProjectName +  "/WEB-INF -name '*.jsp' | xargs touch";
			WebFtpClient[] webFtpClients = projectInfo.was.get(d);
			if(webFtpClients==null) continue;
			for(WebFtpClient webFtpClient : webFtpClients){
				LinuxCmdUtil.sendCommend(webFtpClient.getIp(),"wsadm","ai1234ai",cmd);
			}
		}
		return OK().data("touch:"+distributeLevel + ", " + project);
	}
}