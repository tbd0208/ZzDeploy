package reflectFw.core;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface Renderable{
	void render(Map<String, ?> arg0,HttpServletRequest arg1,HttpServletResponse res) throws Exception;
}