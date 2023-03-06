package zz.projectDistribute.util.io.ftp;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Vector;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import zz.projectDistribute.util.io.StreamUtils;

public class SecretWebFtpClient implements WebFtpClient{
	
	private static final JSch jsch = new JSch();
	
	public static final int RESULT_READY = 0;
	public static final int RESULT_FAIL_READ_PROP = 10;
	public static final int RESULT_FAIL_CONNECT = 11;
	public static final int RESULT_FAIL_LOGIN = 12;
	
	private int result = RESULT_READY;
	
	private String name;
	private String ip;
	
	private Session session;
	private ChannelSftp channel;
	
	public SecretWebFtpClient(WebFtpClientInfo serverInfo,String id,String pw) {
		this.name = serverInfo.name;
		this.ip = serverInfo.ip;
		
		try {
			session = jsch.getSession(id,ip);
			session.setPassword(pw);
			session.setTimeout(10000);
			
			java.util.Properties config = new java.util.Properties();
			config.put("StrictHostKeyChecking", "no");
			session.setConfig(config);
		} catch (JSchException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public String getServerName(){
		return name;
	}
	
	public int getConnectResult(){
		return result;
	}
	
	public boolean isDisconnected(){
		return channel==null || !channel.isConnected();
	}
	
	public boolean connect(){
		try {
			if(!session.isConnected()) session.connect();
			disconnect();
			
			channel = (ChannelSftp)session.openChannel("sftp");
			channel.connect();
			
			return true;
		} catch (JSchException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void disconnect(){
		if(!isDisconnected()) {
			channel.quit();
		}
	}

	public boolean send(final String remotePath,final InputStream in, boolean makeDirectory){
		
		if(makeDirectory) makeWorkingFileDirectory(remotePath);
		try{
			return StreamUtils.write(in,channel.put(remotePath));
		}catch(SftpException e){
			e.printStackTrace();
		}
		return false;
	}
	
	public String download(String src,String des,boolean doOverwrite) throws IOException{
		File f = new File(des);
		if(f.isFile()){
			if(!doOverwrite) return DOWNLOAD_FAIL_NOT_OVERWITE;
		}else{
			f.getParentFile().mkdirs();
			if(!f.createNewFile()) return DOWNLOAD_FAIL_CREATEFILE;
		}
		
		try{
			InputStream in = channel.get(src);
			if(in==null) return DOWNLOAD_FAIL_NO_EXIT;
			
			if(StreamUtils.write(in,f)) return DOWNLOAD_OK;
		}catch(Exception e){
			return DOWNLOAD_FAIL_NO_EXIT;
		}
		
		return DOWNLOAD_FAIL_NO_EXIT;
	}
	public boolean makeWorkingFileDirectory(String filePath){
		return makeWorkingDirectory(filePath.substring(0,filePath.lastIndexOf('/')));
	}
	public boolean makeWorkingDirectory(String path){
		try {
			channel.mkdir(path);
			return true;
		} catch (SftpException e) {
			e.printStackTrace();
		}
		return false;
	}
	 
	public void downloads(String src,String des,boolean overwrite) throws IOException{
		
		Vector<ChannelSftp.LsEntry> subFiles = null;
		try{
			subFiles = channel.ls(src);
		}catch(SftpException e){
			e.printStackTrace();
		}
		
		if (subFiles != null && subFiles.size() > 0) {
			for (final ChannelSftp.LsEntry aFile : subFiles) {
				String currentFileName = aFile.getFilename();
				if (currentFileName.equals(".") || currentFileName.equals("..")) {
					// skip parent directory and the directory itself
					continue;
				}
	 
				if (aFile.getAttrs().isDir()){
					// create the directory in saveDir
					File newDir = new File(des+"/"+currentFileName);
					boolean created = newDir.mkdirs();
					if (created) {
						System.out.println("CREATED the directory: " + newDir);
					} else {
						System.out.println("COULD NOT create the directory: " + newDir);
					}
					// download the sub directory
					downloads(src+"/"+currentFileName, newDir.getPath(),overwrite);
				} else {
					// download the file
					download(src+"/"+currentFileName,des+"/"+currentFileName,overwrite);
				}
			}
		}
	}
	
	public String getIp(){
		return ip;
	}
}
