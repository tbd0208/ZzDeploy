package zx.deploy.base;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import zx.myfw.core.Renderable;

public class Res implements Renderable{
	
	static private final ObjectMapper JSON_MAPPER = new ObjectMapper();
	
	static public Res OK(){
		Res r =  new Res();
		r.state = "ok";
		return r;
	}
	static public Res OK(Object data){
		Res r =  new Res();
		r.state = "ok";
		r.data = data;
		return r;
	}
	static public Res FAIL(String state,String msg){
		Res r =  new Res();
		r.state = state;
		r.msg = msg;
		return r;
	}
	
	private String state;
	private String msg;
	private Object data;
	
	public void render(Map<String, ?> arg0, HttpServletRequest arg1, HttpServletResponse res) throws Exception {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("state",state);
		if(msg!=null) map.put("msg",msg);
		if(data!=null) map.put("data",data);
		
		res.setContentType("application/json;charset=UTF-8");
		res.setHeader("Cache-Control", "no-cache");
		
		JSON_MAPPER.writeValue(res.getWriter(),map);
//		w.close();
	}

	@Override
	public String toString(){
		return "["+this.getClass().getSimpleName()+"]"+state+"."+(msg==null?"":":"+msg)+(data==null?"":("data:"+data));
	}
}
