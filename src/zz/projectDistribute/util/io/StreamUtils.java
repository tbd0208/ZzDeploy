package zz.projectDistribute.util.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Pattern;

public class StreamUtils {
	
	static public boolean writeBuf(InputStream in, OutputStream out){
		BufferedReader in2 = new BufferedReader(new InputStreamReader(in,Charset.defaultCharset()));
		BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(out,Charset.defaultCharset()));
		try{
			String line = null;
			while((line = in2.readLine())!=null) out2.append(line).append(System.lineSeparator());
			return true;
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{out.close();}catch(Exception e){}
			try{in.close();}catch(Exception e){}
		}
		return false;
	}
	
	static public boolean write(InputStream in, OutputStream out){
		try{
			int a;
			while((a = in.read())!=-1) out.write(a);
			return true;
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try{out.close();}catch(Exception e){}
			try{in.close();}catch(Exception e){}
		}
		return false;
	}
	
	static public boolean write(InputStream in, File file){
		try{
			return write(in,new FileOutputStream(file));
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}
		return false;
	}

	public static String toString(InputStream inputStream,Charset cs){
		try{
			InputStreamReader reader = new InputStreamReader(inputStream,cs);
			int c;
			StringBuilder sb = new StringBuilder();
			while((c=reader.read()) != -1){
				sb.append((char)c);
			}
			reader.close();
			return sb.toString();
		}catch (IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
}
