/*

Zola Mahlaza (AdeebNqo)
http:github.com/AdeebNqo

“The problem with using C++ … is that there’s already a strong tendency in the language to require you to know everything before you can do anything.”

- Larry Wall

Class to controll access to dc hub and stream videos

*/
package control;

import java.net.Socket;
import java.io.InputStreamReader;
import java.net.UnknownHostException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.LinkedList;

public class Controller{
	
	private String nick="volkano001";
	private String password = "test";
	
	private String IP;
	private int port;

	private Socket hubConnection;
	private InputStream hubInput;
	private OutputStream hubOutput;

	private String version = "1.00";

	private LinkedList<String> OPlist;
	private LinkedList<String> NickList;

	public Controller(String ip, int port){
		IP = ip; this.port = port;
	}
	/*
	
	Method for connecting to a dc hub

	*/
	public void connect() throws UnknownHostException,IOException{
		OPlist  = new LinkedList<String>();
		NickList = new LinkedList<String>();
	
		System.err.println("Attempting to connect to "+IP+":"+port);
		hubConnection = new Socket(IP,port);
		hubInput = hubConnection.getInputStream();
		hubOutput = hubConnection.getOutputStream();
		System.err.println("TCP connection established.");
		String response = getResponse();
		//sending the supports string
		String[] items = response.split("\\|");
		for (String item: items){
			if (item.startsWith("$Lock")){
				String[] values = item.split(" ");
				String lock = values[1];
				System.err.println("Lock value is "+lock);
				int len = lock.length();
				
				//computing the key and sending...
				String key  = ""+(char)(lock.charAt(0) ^ lock.charAt(len-1) ^ lock.charAt(len-2) ^ 5);
				for (int i = 1; i < len; i++){
					key += lock.charAt(i) ^ lock.charAt(i-1);
				}
				char[] newchars = new char[len];
				for (int i = 0; i < len; i++){
					char x = (char)((key.charAt(i) >> 4) & 0x0F0F0F0F);
					char y = (char)((key.charAt(i) & 0x0F0F0F0F) << 4);
					newchars[i] = (char)(x | y);
				}
				key = String.valueOf(newchars);
				System.err.println("Sending "+key);
				sendData("$Key "+key+"|");
			}
		}

		//sending client's nick
		sendData("$ValidateNick "+nick+"|");
		String responseX = getResponse();
		System.out.println("$validnick RESPONSE: "+responseX);

		String[] itemsX = responseX.split("|");
		for (String itemX:itemsX){
			if (itemX.startsWith("$GetPass")){
				//if hub requires password
				sendData("$MyPass "+password+"|");
				//retrieve hello client msg
				String responseXY = getResponse();
				System.err.println("RESPONSE: "+responseXY);
			}
		}
		//sending version and myinfo
		sendData("$Version "+version+"|");
		sendData("$MyINFO $ALL "+nick+" <++ V:0.673,M:P,H:0/1/0,S:2>$ $LAN(T3)0x31$test@test.com$1234$|");
		sendData("$GetNickList|");
		String listResponse = getResponse();
		System.err.println("list response is "+listResponse);
		String[] listItems = listResponse.split("\\|");
		for (String listitem:listItems){
			listitem = listitem.trim();
			System.err.println(listitem);
			if (listitem.startsWith("$OpList")){
				String[] opusers = listitem.split(" ");
				if (opusers.length>1){
					for (int i=1; i<opusers.length; ++i){
						OPlist.add(opusers[i]);				
					}
				}
			}
			else if (listitem.startsWith("$NickList")){
				int pos = listitem.indexOf(' ');
				String usersdoubledollar = listitem.substring(pos).trim();
				String[] nicks = usersdoubledollar.split("\\$\\$");
				for (String nick:nicks){
					NickList.add(nick);
				}
			}
		}
		//getting the file lists of all connected users
		for (String user:NickList){
			if (!user.equals(nick) && !OPlist.contains(user)){
				System.out.println("Get file list of "+user);
			}
		}
	}
	/*

	Method for reading response from hub
	
	*/
	private String getResponse() throws IOException{
		try{
			Thread.sleep(30000);
		}catch(Exception e){
			//silently fail
		}
		for (int i=0; i<2; ++i){
			try{
				return reallygetResponse();
			}catch(Exception e){
				//silently fail
			}
		}
		return null;
	}
	private String reallygetResponse() throws Exception{
		int available = hubInput.available();
		System.err.println(available+" bytes available.");
		byte[] string = new byte[available];
		int val = hubInput.read(string);
		if (val==-1){
			throw new Exception("Eish");
		}
		String f = new String(string, "UTF-8");
		return f;
	}
	/*

	Method for sending data to hub
	
	*/
	private void sendData(String data) throws IOException{
		hubOutput.write(data.getBytes(Charset.forName("UTF-8")));
		hubOutput.flush();
	}
}
