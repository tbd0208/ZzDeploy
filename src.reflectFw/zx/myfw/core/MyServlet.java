package zx.myfw.core;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class MyServlet extends HttpServlet{
	
	private Map<String,ReflectClassInfo> urlMappingInfoMap = new HashMap<>();
	private Map<String,Method> urlMappingMethodMap = new HashMap<>();
	private Map<Class,Object> typeBeanMap = new HashMap<>();
	
	@Override
	public void init(ServletConfig config) throws ServletException{
		super.init(config);
		
		try{
			
			final javassist.ClassPool pool = javassist.ClassPool.getDefault(); 
			final String defaultPackage = getInitParameter("urlMappingPackage");
			final String postFix = getInitParameter("urlMappingTypePostFix");
			final PackageClassFinder classFinder = new PackageClassFinder(defaultPackage);
			List<Class<?>> find = classFinder.find();
			
			for (Class<?> class1 : find){
				String name = class1.getSimpleName();
				if(!name.endsWith(postFix)) continue;
				
				pool.insertClassPath(new javassist.ClassClassPath(class1));
				javassist.CtClass ctClass = pool.get(class1.getName());
				ReflectClassInfo info = new ReflectClassInfo();
				
				info.type = class1;
				
				final String targetName = (""+name.charAt(0)).toLowerCase()+name.substring(1,name.length()-postFix.length());
				final Method[] methods = info.type.getDeclaredMethods();
				for (Method method : methods){
					if(!Modifier.isPublic(method.getModifiers()) || Modifier.isStatic(method.getModifiers()) ) continue;
					String url = targetName+"/"+method.getName();
					urlMappingMethodMap.put(url,method);
					urlMappingInfoMap.put(url,info);
					
					javassist.CtMethod ctMethod = ctClass.getDeclaredMethod(method.getName());
					System.out.println(url+" - "+class1.getName()+"."+method.getName()+"("+ctClass.getSimpleName()+".java:"+(ctMethod.getMethodInfo().getLineNumber(0)-1)+")");
				}
			}
		}catch (javassist.NotFoundException e){
			e.printStackTrace();
		}catch (IOException e){
			e.printStackTrace();
		}
	}
	
	@Override
	protected void service(HttpServletRequest request,HttpServletResponse response) throws ServletException,IOException{

		String remoteAddr = request.getRequestURI();
		Parameter[] parameters = null;
		Object[] parameterValues = null;
		try{
			String urlBody = remoteAddr.substring(1,remoteAddr.lastIndexOf('.'));
			ReflectClassInfo info = urlMappingInfoMap.get(urlBody);
			if(info==null) throw new Exception("404 : " + urlBody);
			if(info.intance==null) {
				
				// 생성자 및 Class를 이용한 의존성 주입
				for(Constructor<?> constructor : info.type.getConstructors()){
					if( Modifier.isPublic(constructor.getModifiers()) ) {
						Class<?>[] parameterTypes = constructor.getParameterTypes();
						Object[] cparameterValues = new Object[parameterTypes.length];
						for(int i = 0;i<parameterTypes.length;i++){
							Class<?> class1 = parameterTypes[i];
							Object instance = typeBeanMap.get(class1);
							if(instance==null) {
								cparameterValues[i] = class1.newInstance();
								typeBeanMap.put(class1,cparameterValues[i]);
							}else {
								cparameterValues[i] = instance;
							}
						}
						info.intance = constructor.newInstance(cparameterValues);
//						Parameter[] cps = constructor.getParameters();
//						Object[] cvs = new Object[cps.length];
//						for(Parameter parameter : cps){
//							cvs[i++] = parameter.getClass().newInstance();
//						}
						break;
					}
					
				}
//				info.intance = info.type.newInstance();
			}
			
			Method method = urlMappingMethodMap.get(urlBody);
			
			parameters = method.getParameters();
			parameterValues = new Object[parameters.length];
			
			Map<String,Object> parameterMap = normalizeMap(request.getParameterMap());
			
			for (int i = 0; i < parameters.length; i++){
				Parameter p = parameters[i];
				Class<?> type = p.getType();
				
				if(Map.class.isAssignableFrom(type)) {
					parameterValues[i] = type.getDeclaredConstructor(Map.class).newInstance(parameterMap);
				}
				else{
					Object v = parameterMap.get(p.getName());
					if(String.class==type) {
						parameterValues[i] = v;
					}else if(v==null) {
						
					}else if(type==Integer.class){
						parameterValues[i] = Integer.parseInt((String)v);
					}else if(Boolean.class==type) {
						parameterValues[i] = Boolean.valueOf((String)v);
					}else if(type.isEnum()){
						parameterValues[i] = Enum.valueOf((Class<Enum>)type,(String)v);
					}else {
						parameterValues[i] = v;
					}
				}
			}
			
//			RequestContextHolder.set(request);
			Object invoke  = method.invoke(info.intance,parameterValues);
//			Class<?> resultClass = invoke.getClass();
			
			if(Object.class==method.getReturnType()) {
				if(invoke!=null&&Renderable.class.isAssignableFrom(invoke.getClass())) {
					((Renderable)invoke).render(parameterMap,request,response);
				}else {
					PrintWriter writer = response.getWriter();
					writer.append(""+invoke);
				}
			}else if(Void.class==method.getReturnType()) {
				includeJsp(urlBody,request,response);
			}else if(Map.class.isAssignableFrom(method.getReturnType()) || Map.class.isAssignableFrom(invoke.getClass())){
				Map map = (Map)invoke;
				for(Object k : map.keySet()) request.setAttribute(""+k,map.get(k));
				includeJsp(urlBody,request,response);
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
	
	private void includeJsp(String urlKey,HttpServletRequest request,HttpServletResponse response) throws ServletException, IOException{
		String action = "";
		String dmName = "";
		
		int s = urlKey.lastIndexOf('/');
		int e = urlKey.length();
		action = urlKey.substring(s+1,e);
		
		e = s;
		s = urlKey.lastIndexOf('/',s-1);
		dmName = urlKey.substring(s+1,e);
		
		response.setContentType("text/html");
		response.setCharacterEncoding("utf-8");
		
		request.getRequestDispatcher("/"+dmName+"/"+dmName+"-"+action+".jsp").include(request,response);
	}

	private Map<String,Object> normalizeMap(Map<String,String[]> map){
		Map<String,Object> r = new HashMap<>();
		map.forEach((k,vs)->{
			if(vs!=null) r.put(k,(vs.length<2?vs[0]:Arrays.asList(vs)));
		});
		return r;
	}
}