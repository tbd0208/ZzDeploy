package zz.projectDistribute.util;

import java.io.IOException;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;

public class QQFileLogger extends Logger{
	
	protected QQFileLogger() {
		super("SpFileLogger");
		this.level = Level.INFO;
		this.additive = true;
		this.parent = Logger.getRootLogger();
		this.repository = LogManager.getLoggerRepository();
	}

	public static Logger getLogger(String path,boolean print){
		System.out.println("SpFileLogger : " + path + " " + print);
		Logger logger = new QQFileLogger();
		try{
			Layout layout = new PatternLayout("%d{yyMMdd} %m%n");
			QQFileAppender spFileAppender = new QQFileAppender(layout,path);
			
			logger.addAppender(spFileAppender);
		}catch(SecurityException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
		return logger;
	}
}
