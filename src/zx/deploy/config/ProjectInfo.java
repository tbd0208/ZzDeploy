package zx.deploy.config;

import java.util.Map;

import zz.server.ftp.WebFtpClient;

public final class ProjectInfo{
	
	public String name;
	public String nicName;
	public String localPath;
	
	public String localWebFolder;
	public String localClassFolder;
	public Map<String,String> localSourceDeployMap;
	
	final private String severProjectName;
	final private String[] severProjectNames = new String[DistributeLevel.values().length];
	final public String severClassesFolder;
	final public DistributeInfo was;
	final public DistributeInfo web;
//	final public DistributeLevel[] levels;
	
	public ProjectInfo(String projectNicName,String projectName,String severProjectName,String severClassesFolder,DistributeInfo web,DistributeInfo was) {
		this.nicName = projectNicName;
		this.name = projectName;
		this.severProjectName = severProjectName;
		this.severClassesFolder = severClassesFolder;
		this.was = was;
		this.web = web;
	}
	
	public void setServerProjectName(DistributeLevel distributeLevel,String severProjectName) {
		this.severProjectNames[distributeLevel.ordinal()] = severProjectName;
	}
	public String getServerProjectName(DistributeLevel distributeLevel) {
		String name =  this.severProjectNames[distributeLevel.ordinal()];
		if(name == null) return this.severProjectName;
		return name;
	}
	public String getServerProjectName() {
		return this.severProjectName;
	}
	public String getStringServerProjectNames() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.severProjectName);
		DistributeLevel[] levels = DistributeLevel.values();
		for(int i=0;i<levels.length;i++){
			String name =  this.severProjectNames[levels[i].ordinal()];
			if(name != null) sb.append('/').append(name);
		}
		return sb.toString();
	}
	public String[] getServerProjectNames() {
		return this.severProjectNames;
	}
	
	public boolean hasWeb() {
		return web!=null && was!=web;
	}
	
	public String getSimpleProjectInfo() {
		StringBuilder sb = new StringBuilder();
//		DistributeLevel[] levels = DistributeLevel.values();
		WebFtpClient[] clients = null;
		
		// 프로젝트 네임
		sb.append('[').append(name).append(']').append(" | ");
		
		// 서버 폴더
		sb.append("WAS : ").append(was.getPrePath()).append(" | ");
		// IP 주소
		clients = was.get(DistributeLevel.DEV);
		if(clients!=null && clients.length>0) for(int j=0;j<clients.length;j++) sb.append(clients[j].getIp()).append(" | ");
		clients = was.get(DistributeLevel.OPE);
		if(clients!=null && clients.length>0) for(int j=0;j<clients.length;j++) sb.append(clients[j].getIp()).append(" | ");
		
		if(hasWeb()) {
			// 서버 폴더
			sb.append("WEB : ").append(web.getPrePath()).append(" | ");
			// IP 주소
			clients = web.get(DistributeLevel.DEV);
			if(clients!=null && clients.length>0) for(int j=0;j<clients.length;j++) sb.append(clients[j].getIp()).append(" | ");
			clients = web.get(DistributeLevel.OPE);
			if(clients!=null && clients.length>0) for(int j=0;j<clients.length;j++) sb.append(clients[j].getIp()).append(" | ");
		}
		return sb.toString();
	}
	
	@Override
	public String toString(){
		return getSimpleProjectInfo();
	}
}