package zz.server.ftp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPConnectionClosedException;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class ApacheWebFtpClient implements WebFtpClient{
	
	private int result = RESULT_READY;
	
	private FTPClient client;
	private String name;
	private String ip;
	private String id;
	private String pw;
	
	public ApacheWebFtpClient(WebFtpClientInfo serverInfo,String id,String pw) {
		this.name = serverInfo.name;
		this.ip = serverInfo.ip;
		this.id = id;
		this.pw = pw;
	}
	
	public String getServerName(){
		return name;
	}
	
	public int getConnectResult(){
		return result;
	}
	
	public boolean isConnected(){
		return client.isConnected();
	}
	
	// ftp chmod
	private void ftpChmod(String path) throws IOException {
		client.sendCommand("SITE chmod 770 "+path);
	}
	
	public boolean connect(){
		try {
			client = new FTPClient();
			client.setRemoteVerificationEnabled(false); // FTP response 421 received. Server closed connection
			client.setControlEncoding("UTF-8");
			client.connect(getIp());
			int replyCode = client.getReplyCode();
			if (!FTPReply.isPositiveCompletion(replyCode)) {
				this.result = RESULT_FAIL_CONNECT;
				return false;
			}
			if (!client.login(id,pw)) {
				this.result = RESULT_FAIL_LOGIN;
				return false;
			}
			// after connection open
			client.setFileType(FTP.BINARY_FILE_TYPE);
			client.setDefaultTimeout(0);
			client.setSoTimeout(8000*3600); // 8h
//			client.setDataTimeout(360000);
//			client.addProtocolCommandListener(new PrintCommandListener(new PrintWriter(System.out),true));
//			}
			
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	public void disconnect(){
		if (client.isConnected()) {
			try{
				client.logout();
				client.disconnect();
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public boolean isDisconnected(){
		return client==null || !client.isAvailable() || !client.isConnected();
	}
	
//	public boolean sendForPath(InputStream inputStream, String path) {
//		return send(path,inputStream, false);
//	}
	public boolean send(final String remotePath, final InputStream is, boolean makeDirectory){
		if(isDisconnected()) if(!connect()) throw new RuntimeException("FAIL send. Fail connect");
		if(makeDirectory) makeWorkingFileDirectory(remotePath);
		try{
			boolean storeFile = false;
			try{
				storeFile = client.storeFile(remotePath,is);
				System.out.println("FTP.send01 : " + storeFile + " / " + remotePath);
			}catch(Exception e) {
//				e.printStackTrace();
				connect();
				storeFile = client.storeFile(remotePath,is);
				System.out.println("FTP.send02 : " + storeFile + " / " + remotePath);
			}
			if(storeFile){
				ftpChmod(remotePath);
				return true;
			}else{
				System.err.println(client.getReplyString());
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{is.close();}catch(IOException e){e.printStackTrace();}
		}
		return false;
	}
	
	public boolean delete(String fileName){
		try {
			return client.deleteFile(fileName);
		} catch (IOException e) {
//			this.e = e;
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean makeWorkingFileDirectory(String filePath){
		if(isDisconnected()) if(!connect()) throw new RuntimeException("FAIL send. Fail connect");
		return makeWorkingDirectory(filePath.substring(0,filePath.lastIndexOf('/')));
	}
	public boolean makeWorkingDirectory(String path){
		return _makeWorkingDirectory(path);
	}
	public boolean makeDirectory(String dir) throws IOException{
		if(isDisconnected()) if(!connect()) throw new RuntimeException("FAIL send. Fail connect");
		return client.makeDirectory(dir);
	}
	public boolean changeWorkingDirectory(String dir) throws IOException{
		if(isDisconnected()) if(!connect()) throw new RuntimeException("FAIL send. Fail connect");
		return client.changeWorkingDirectory(dir);
	}
	private boolean _makeWorkingDirectory(String path){
		if(isDisconnected()) if(!connect()) throw new RuntimeException("FAIL send. Fail connect");
		try {
			if(client.changeWorkingDirectory(path)) return true;
			String dir = path;
			ArrayList<String> newChildDirList = new ArrayList<String>(1);
			while(true){
				int index = dir.lastIndexOf('/');
				if(index<0) break;
				newChildDirList.add(dir.substring(index+1));
				dir = dir.substring(0,index);
				if(client.changeWorkingDirectory(dir)) break;
			}
			for (int i=newChildDirList.size()-1;i>=0;i--) {
				String newChildDir = newChildDirList.get(i);
				client.makeDirectory(newChildDir);
				client.changeWorkingDirectory(newChildDir);
				ftpChmod(newChildDir);
			}
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public String download(String src,String des,boolean overwrite) throws IOException{
		OutputStream outputStream1 = null;
		try {
			File f = new File(des);
			if(!overwrite && f.isFile()){
				return DOWNLOAD_FAIL_NOT_OVERWITE;
			}
			f.getParentFile().mkdirs();
			if(!f.createNewFile() && !overwrite) return DOWNLOAD_FAIL_CREATEFILE;
			outputStream1 = new BufferedOutputStream(new FileOutputStream(f));
			try{
				if(client.retrieveFile(src, outputStream1)) return DOWNLOAD_OK;
			}catch(FTPConnectionClosedException e) {
				connect();
				if(client.retrieveFile(src, outputStream1)) return DOWNLOAD_OK;
			}
			f.delete();
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(outputStream1!=null) try{outputStream1.close();}catch(IOException e){}
		}
		return DOWNLOAD_FAIL_NO_EXIT;
	}
	
	public String getModificationTime(String path) throws IOException{
		return client.getModificationTime(path);
	}
	
	public void downloads(String src,String des,boolean overwrite) throws IOException{
		FTPFile[] subFiles=client.listFiles(src);
		if(subFiles!=null&&subFiles.length>0){
			for(FTPFile aFile : subFiles){
				String currentFileName=aFile.getName();
				if(currentFileName.equals(".")||currentFileName.equals("..")){
					// skip parent directory and the directory itself
					continue;
				}

				if(aFile.isDirectory()){
					// create the directory in saveDir
					File newDir=new File(des+"/"+aFile.getName());
					boolean created=newDir.mkdirs();
					if(created){
						System.out.println("CREATED the directory: "+newDir);
					}else{
						System.out.println("COULD NOT create the directory: "+newDir);
					}
					// download the sub directory
					downloads(src+"/"+aFile.getName(),newDir.getPath(),overwrite);
				}else{
					// download the file
					download(src+"/"+aFile.getName(),des+"/"+aFile.getName(),overwrite);
				}
			}
		}
	}

	public String getIp(){
		return ip;
	}
}
