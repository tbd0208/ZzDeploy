package zz.projectDistribute.util;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.log4j.Appender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorHandler;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;

public class QQFileAppender implements Appender
{
//	protected boolean fileAppend;
//	protected String fileName;
//	protected boolean bufferedIO;
//	protected int bufferSize;
	
//	protected boolean immediateFlush;
//	protected String encoding;
//	protected QuietWriter qw;
	
	private Layout layout;
	
	protected Filter headFilter;
	protected Filter tailFilter;

	private String name;

	public QQFileAppender(Layout layout, String fileName, boolean append) throws IOException{
		this.layout = layout;
		this.name = fileName;
	}

	public QQFileAppender(Layout layout, String filename) throws IOException{
		this(layout,filename,true);
	}

	@Override
	public void addFilter(Filter newFilter){
		if(this.headFilter==null){
			this.headFilter = (this.tailFilter = newFilter);
		}else{
			this.tailFilter.setNext(newFilter);
			this.tailFilter = newFilter;
		}
	}

	@Override
	public Filter getFilter(){
		return this.headFilter;
	}

	@Override
	public void clearFilters(){
		this.headFilter = (this.tailFilter = null);
	}

	@Override
	public void close(){
//		try{
//			w.close();
//		}catch(IOException e){
//			e.printStackTrace();
//		}
	}

	@Override
	public void doAppend(LoggingEvent paramLoggingEvent){
		String v = this.layout.format(paramLoggingEvent);
		try{
			Writer w = new OutputStreamWriter(new FileOutputStream(this.name,true),"UTF-8");
			w.append(v);
			w.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}


	@Override
	public void setErrorHandler(ErrorHandler paramErrorHandler){
		
	}

	@Override
	public ErrorHandler getErrorHandler(){
		return null;
	}

	@Override
	public void setLayout(Layout paramLayout){
		this.layout = paramLayout;
	}

	@Override
	public Layout getLayout(){
		return this.layout;
	}

	@Override
	public void setName(String name){
		this.name = name;
	}
	

	@Override
	public String getName(){
		return this.name;
	}

	@Override
	public boolean requiresLayout(){
		return true;
	}
}