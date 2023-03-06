package zz.projectDistribute.dm;

import java.io.IOException;
import java.util.Properties;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class LinuxCmdUtil{

	static public void sendCommend(String ip,String user,String pw,String cmd) throws JSchException,IOException{
		JSch js = new JSch();
		Session session = js.getSession(user,ip);

		Properties config = new Properties();
		config.put("StrictHostKeyChecking","no");
		session.setConfig(config);

		session.setPassword(pw);
		session.setTimeout(1000);
		session.connect();

		ChannelExec channel = (ChannelExec)session.openChannel("exec");
		channel.setCommand(cmd);
		channel.setErrStream(System.err);
		channel.connect();

		channel.disconnect();
		session.disconnect();
	}
}
