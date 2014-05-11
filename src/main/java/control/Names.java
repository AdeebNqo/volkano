package control;

import java.io.File;
import java.util.LinkedList;
import java.io.IOException;
import java.security.SecureRandom;
import java.math.BigInteger;

public class Names{
	LinkedList<String> names = new LinkedList<String>();
	int currindex = 0;
	private SecureRandom random = new SecureRandom();
	private String tempdir = System.getProperty("java.io.tmpdir");
	
	public Names() throws IOException{
		String filename = tempdir+(File.separator)+nextSessionId()+".avi";
		names.add(filename);
		++currindex;
	}
	public String get(int index){
		return names.get(index);
	}
	public String next() throws IOException{
		String filename = tempdir+(File.separator)+nextSessionId()+".avi";
		names.add(filename);
		++currindex;
		return names.get(currindex-1);
	}
	public String nextSessionId(){
	    return new BigInteger(130, random).toString(32);
	}
}
