package zz.projectDistribute;

import java.net.URLEncoder;
import java.util.Random;

public class Zz{
	public static void main(String[] args){
		System.out.println(System.currentTimeMillis());
		System.out.println(new Random(System.currentTimeMillis()).nextInt());
		
		System.out.println(URLEncoder.encode("AAF1MX1v6&%2BgvqzoXpB3pOmDW"));
	}
}
