package zz.projectDistribute.util.io.ftp;

import java.io.IOException;
import java.io.InputStream;

public interface WebFtpClient {
	
	static public final int RESULT_READY = 0;
	static public final int RESULT_FAIL_READ_PROP = 10;
	static public final int RESULT_FAIL_CONNECT = 11;
	static public final int RESULT_FAIL_LOGIN = 12;
	
	static public final String DOWNLOAD_OK = "DOWNLOAD_OK";
	static public final String DOWNLOAD_FAIL_NO_EXIT = "DOWNLOAD_FAIL_NO_EXIT";
	static public final String DOWNLOAD_FAIL_NOT_OVERWITE = "DOWNLOAD_FAIL_NOT_OVERWITE";
	static public final String DOWNLOAD_FAIL_MKDIRS = "DOWNLOAD_FAIL_MKDIRS";
	static public final String DOWNLOAD_FAIL_CREATEFILE = "DOWNLOAD_FAIL_CREATEFILE";

	String download(String ftpPath,String backupPath,boolean doOverwrite) throws IOException;

	boolean connect();
	void disconnect();
	boolean isDisconnected();
	
	boolean send(String ftpPath,InputStream fileInputStream, boolean makeDirectory);
	void downloads(String string,String string2,boolean b) throws IOException;

	String getServerName();
	String getIp();
}