package zx.deploy.dm;

import static zx.deploy.base.Res.OK;

import java.util.Base64;

import zz.util.QQMap;

public class TestController{
	
	public static void main(String[] args){
//		System.out.println(Arrays.toString("a|b|".split("\\|",-1)));
		System.out.println(Base64.getEncoder().encodeToString("ihl:ihl123!".getBytes()));
//		System.out.println(new String(Base64.getDecoder().decode("ahands:ahands123!")));
	}
	
	public String text(String v,QQMap map) {
		
		try {
			
			throw new RuntimeException("TEST");
		}catch(Exception e) {
			throw e;
		}finally {
//			return v+" "+map;
		}
	}
	public Object jsp(QQMap map) {
		return map;
	}
	public Object echo(QQMap map) {
		return OK(map);
	}
}