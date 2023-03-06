package zz.projectDistribute.base;

import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import reflectFw.core.Renderable;

public class Res implements Renderable{
	
	public static Res OK(){
		return new Res().ok();
	}
	public static Res OK(Object data){
		return new Res().ok().data(data);
	}
	public static Res FAIL(String state,String msg){
		return new Res().fail(state).msg(msg);
	}
	
	public void render(Map<String, ?> arg0, HttpServletRequest arg1, HttpServletResponse res) throws Exception {
		try{
			Map<String, Object> map = new LinkedHashMap<String, Object>();
			map.put("state",state);
			if(msg!=null) map.put("msg",msg);
			if(data!=null) map.put("data",data);
			
			res.setContentType("application/json;charset=UTF-8");
			res.setHeader("Cache-Control", "no-cache");
			PrintWriter w = res.getWriter();
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.writeValue(w,map);
			w.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private String state;
	private String msg;
	private Object data;
	
	public Res ok(){
		this.state = "ok";
		return this;
	}
	public Res fail(String state){
		this.state = state;
		return this;
	}
	public Res msg(String msg){
		this.msg = msg;
		return this;
	}
	public Res data(Object data){
		this.data = data;
		return this;
	}

	@Override
	public String toString(){
		return "["+this.getClass().getSimpleName()+"]"+state+"."+(msg==null?"":":"+msg)+(data==null?"":("data:"+data));
	}
}
