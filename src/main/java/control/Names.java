package control;

import java.io.File;
import java.util.LinkedList;
import java.io.IOException;

public class Names{
	LinkedList<String> names = new LinkedList<String>();
	int currindex = 0;
	public Names() throws IOException{
		File tmpfile = File.createTempFile("volkano",".avi");
		names.add(tmpfile.getAbsolutePath());
	}
	public String get(int index){
		return names.get(index);
	}
	public String next() throws IOException{
		File tmpfile = File.createTempFile("volkano",".avi");
		names.add(tmpfile.getAbsolutePath());
		++currindex;
		return names.get(currindex-1);
	}
}
