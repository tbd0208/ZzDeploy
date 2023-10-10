package zx.deploy.config;

import java.util.HashMap;
import java.util.Map;

import zz.server.ftp.WebFtpClient;

public class DistributeInfo {
	
	private String serverGroupName;
	private String prePath;
	private WebFtpClient[][] list = new WebFtpClient[DistributeLevel.values().length][];
	
	public DistributeInfo(String serverGroupName,String svrPrePath,Map<String,WebFtpClient[]> WebFtpClients) {
		this.serverGroupName = serverGroupName;
		this.prePath = svrPrePath;
		WebFtpClients.forEach((k,v)->{
			list[DistributeLevel.valueOf(k).ordinal()] = v;
		});
	}
	
	public String getServerGroupName() {
		return serverGroupName;
	}
	public String getPrePath() {
		return prePath;
	}

//	public DistributeInfo put(DistributeLevel distributeLevel, WebFtpClient...webFtpClients) {
//		list[distributeLevel.ordinal()] = webFtpClients;
//		return this;
//	}
	public WebFtpClient[] get(DistributeLevel distributeLevel) {
		return list[distributeLevel.ordinal()];
	}
	
	public Object getDstrMap() {
		HashMap<Object, Object> o = new HashMap<>();
		for(int i = 0;i<list.length;i++){
			if(list[i]==null) continue;
			WebFtpClient[] webFtpClients = list[i];
			HashMap<Object, Object>[] as = new HashMap[webFtpClients.length];
			for(int j = 0;j<webFtpClients.length;j++){
				HashMap<Object, Object> a = new HashMap<>();
				a.put("ip",webFtpClients[j].getIp());
				as[j] = a;
			}
			o.put(DistributeLevel.values()[i],as);
		}
		return o;
	}
	
}
