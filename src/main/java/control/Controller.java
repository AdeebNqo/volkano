/*

Zola Mahlaza (AdeebNqo)
http:github.com/AdeebNqo

“The problem with using C++ … is that there’s already a strong tendency in the language to require you to know everything before you can do anything.”

- Larry Wall

Class to controll access to dc hub and stream videos

*/
package control;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;
public class Controller{
	
	
	private String IP;
	private int port;

	private Socket hubConnection;
	private BufferedReader hubInput;
	private PrintWriter hubOutput;

	public Controller(String ip, int port){
		IP = ip; port = port;
	}
	/*
	
	Method for connecting to a dc hub

	*/
	public void connect(){
		hubConnection = new Socket(IP,port);
		hubInput = new InputStreamReader(hubConnection.getInputStream());
		hubOutput = new PrintWriter(hubConnection.getOutputStream());
		System.err.println("TCP connection established.")

	}
}
