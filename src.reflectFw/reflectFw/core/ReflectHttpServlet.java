package reflectFw.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

@SuppressWarnings("serial")
public class ReflectHttpServlet extends HttpServlet{
	
	private Map<String,ReflectClassInfo> urlMappingInfoMap = new HashMap<>();
	private Map<String,Method> methodMap = new HashMap<>();
	
	@Override
	public void init(ServletConfig config) throws ServletException{
		try{
			ClassPool pool = ClassPool.getDefault();
			
			super.init(config);
			String defaultPackage = getInitParameter("defaultPackage");
			String controllerPostFix = getInitParameter("controllerPostFix");
			
			PackageClassFinder classFinder = new PackageClassFinder(defaultPackage);
			List<Class<?>> classes = classFinder.find();
			for (Class<?> class1 : classes){
				String name = class1.getSimpleName();
				if(!name.endsWith(controllerPostFix)) continue;
				
				pool.insertClassPath(new ClassClassPath(class1));
				CtClass ctClass = pool.get(class1.getName());
				ReflectClassInfo info = new ReflectClassInfo();
				String targetName = (""+name.charAt(0)).toLowerCase()+name.substring(1,name.length()-controllerPostFix.length());
				
				Method[] methods = class1.getDeclaredMethods();
				for (Method method : methods){
					if(!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers()) ) continue;
					String url = targetName+"/"+method.getName();
					methodMap.put(url,method);
					urlMappingInfoMap.put(url,info);
					
					CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName());
					System.out.println(url+" - "+class1.getName()+"."+method.getName()+"("+ctClass.getSimpleName()+".java:"+(ctMethod.getMethodInfo().getLineNumber(0)-1)+")");
				}
				info.type = class1;
			}
		}catch (NotFoundException e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void service(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{

		String remoteAddr = request.getRequestURI();
		Parameter[] parameters = null;
		Object[] parameterValeus = null;
		try{
			String urlBody = remoteAddr.substring(1,remoteAddr.lastIndexOf('.'));
			ReflectClassInfo info = urlMappingInfoMap.get(urlBody);
			if(info==null) throw new Exception(urlBody);
			if(info.intance==null) info.intance = info.type.newInstance();
			
			Method method = methodMap.get(urlBody);
			
			parameters = method.getParameters();
			parameterValeus = new Object[parameters.length];
			
			Map<String,Object> parameterMap = normalizeMap(request.getParameterMap());
			
			for (int i = 0; i < parameters.length; i++){
				Parameter p = parameters[i];
				Class<?> type = p.getType();
				
				if(Map.class.isAssignableFrom(type)) {
					parameterValeus[i] = type.getDeclaredConstructor(Map.class).newInstance(parameterMap);
				}
				else{
					Object v = parameterMap.get(p.getName());
					if(String.class==type) {
						parameterValeus[i] = v;
					}else if(v==null) {
						
					}else if(type==Integer.class){
						parameterValeus[i] = Integer.parseInt((String)v);
					}else if(Boolean.class==type) {
						parameterValeus[i] = Boolean.valueOf((String)v);
					}else if(type.isEnum()){
						parameterValeus[i] = Enum.valueOf((Class<Enum>)type,(String)v);
					}else {
						parameterValeus[i] = v;
					}
				}
			}
			
//			RequestContextHolder.set(request);
			Object invoke  = method.invoke(info.intance,parameterValeus);
//			Class<?> resultClass = invoke.getClass();
			
			if(Object.class==method.getReturnType()) {
				if(invoke!=null&&Renderable.class.isAssignableFrom(invoke.getClass())) {
					((Renderable)invoke).render(parameterMap,request,response);
				}else {
					PrintWriter writer = response.getWriter();
					writer.append(""+invoke);
				}
			}else if(Void.class==method.getReturnType()) {
				includeJsp(urlBody,remoteAddr,request,response);
			}else if(Map.class.isAssignableFrom(method.getReturnType()) || Map.class.isAssignableFrom(invoke.getClass())){
				Map map = (Map)invoke;
				for(Object k : map.keySet()) request.setAttribute(""+k,map.get(k));
				includeJsp(urlBody,remoteAddr,request,response);
			}else if(Renderable.class.isAssignableFrom(method.getReturnType()) || Renderable.class.isAssignableFrom(invoke.getClass())) {
				((Renderable)invoke).render(parameterMap,request,response);
			}else {
				PrintWriter writer = response.getWriter();
				writer.append(""+invoke);
			}
			
		}catch(IllegalArgumentException e) {
			System.out.println(Arrays.toString(parameters));
			throw e;
		}catch (IllegalAccessException | InvocationTargetException e){
			e.printStackTrace();
			response.sendError(505,e.getCause().getMessage());
		}catch (Exception e){
			System.err.println(remoteAddr);
			e.printStackTrace();
		}
	}
	
	private void includeJsp(String urlKey,String remoteAddr,HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		String action = "";
		String dmName = "";
		
		int i=remoteAddr.length(),s=0,e=urlKey.length()+1;
		for(;i>0;) if(remoteAddr.charAt(--i)=='/'){s=i;break;};
		action = remoteAddr.substring(s+1,e);
		
		e = s;
		for(;i>0;) if(remoteAddr.charAt(--i)=='/'){s=i;break;};
		dmName = remoteAddr.substring(s+1,e);
		
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		
		RequestDispatcher requestDispatcher = request.getRequestDispatcher("/"+dmName+"/"+dmName+"-"+action+".jsp");
		requestDispatcher.include(request,response);
	}

	private Map<String,Object> normalizeMap(Map<String,String[]> map){
		Map<String,Object> r = new HashMap<>();
		for(String k : map.keySet()) {
			String[] vs = (String[])map.get(k);
			if(vs!=null) r.put(k,(vs.length<2?vs[0]:Arrays.asList(vs)));
		}
		return r;
	}
}