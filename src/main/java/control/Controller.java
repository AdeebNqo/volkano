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
import java.net.ServerSocket;
import java.security.SecureRandom;
import java.math.BigInteger;
import java.util.Scanner;
import java.io.FileOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.File;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

public class Controller{
	
	final private String nick="volkano001";
	private String password = "test";
	
	private String IP;
	private int port;

	private Socket hubConnection;
	private InputStream hubInput;
	private OutputStream hubOutput;

	private String version = "1.00";

	private LinkedList<String> OPlist;
	private LinkedList<String> NickList;

	private boolean passiveClient = false; //determines if client active or passive

	public Controller(String ip, int port){
		IP = ip; this.port = port;
	}
	/*
	
	Method for connecting to a dc hub

	*/
	public void connect() throws UnknownHostException,IOException, InterruptedException{
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
				getLockSendKey(item);
			}
		}

		//sending client's nick
		sendData("$ValidateNick "+nick+"|");
		String responseX = getResponse();
		System.err.println("$validnick RESPONSE: "+responseX);

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
				if (passiveClient){
					//passive client
				}
				else{
					//active client
					ServerSocket s = new ServerSocket(0);
					int clientport = s.getLocalPort();
					String clientip = "127.0.0.1";
					sendData("$ConnectToMe "+user+" "+clientip+":"+clientport+"|");
					String connectclientResponse = getResponse();
					final Socket clientSocket = s.accept();
					s.close();
					//handling client connection
					Thread t = new Thread(){
						Scanner in;
						InputStream in2;
						OutputStream out;
						String mynick = nick;
						public void run(){
							try{
								
								LinkedList<String> vals = new LinkedList<String>();
								in2 = clientSocket.getInputStream();
								in = new Scanner(in2);
								in.useDelimiter("\\|");
								out = clientSocket.getOutputStream();
								//retrieve other client's nick
								String clientnick = getResponse();
								//send mynick
								sendData("$MyNick "+nick+"|");
								//sending mylock
								sendData("$Lock EXTENDEDPROTOCOL"+getKey()+" Pk=Volkano"+version+"ABCABC|");
								//reading lock
								String lockclient = getResponse();
								//sending stuff supported
								sendData("$Supports MiniSlots XmlBZList ADCGet TTHL TTHF ZLIG|");
								//notifying that client wants to download
								sendData("$Direction Download "+randInt(1,32767)+"|");
								//sending key
								getLockSendKey(lockclient);
								//receive stuff supported by other client
								String clientsupport = getResponse();
								String clientdirection = getResponse();
								//receiving  key response from other client
								String clientkey = getResponse();
								//request file list
								sendData("$ADCGET file files.xml 0 -1|");
								String adcresponse = getResponse();
								System.err.println("adcresponse is "+adcresponse);
								int filesize = Integer.parseInt(adcresponse.split(" ")[4]);		
								System.err.println("filesize: "+filesize);

								in.useDelimiter("</FileListing>");
								String filelist = in.next();
								System.err.println("filelist: "+filelist);

								in.useDelimiter("\\|");
								System.err.println("Downloading file 46GU5HFDLWBRNCLPXYAWMGS6JASDKTDOEUK5VXQ");
								sendData("$ADCGET file TTH/46GU5HFDLWBRNCLPXYAWMGS6JASDKTDOEUK5VXQ 0 -1|");
								String adcresponseX = getResponse();
								System.err.println("adcresponse is "+adcresponseX);
								int filesizeX = Integer.parseInt(adcresponseX.split(" ")[4]);		
								System.err.println("filesize: "+filesizeX);
								
							}catch(Exception e){
								//gracefully fail
								e.printStackTrace();
							}
						}
						private String getResponse() throws IOException{
							return in.next();
						}
						private void sendData(String data) throws IOException{
							out.write(data.getBytes());
							out.flush();
						}
						private void getLockSendKey(String item) throws IOException{
							String[] values = item.split(" ");
							String lock = Controller.sanitizeLockKey(values[1],1);
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
							key = Controller.sanitizeLockKey(String.valueOf(newchars),0);
							System.err.println("Sending "+key);
							sendData("$Key "+key+"|");
						}
					};
					t.start();
					System.err.println("Other client connected...");
					t.join();
					System.err.println("Controller exiting...");
				}
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
	/*
	
	Method for getting lock and sending key

	*/
	private void getLockSendKey(String item) throws IOException{
		String[] values = item.split(" ");
		String lock = sanitizeLockKey(values[1],1);
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
		key = Controller.sanitizeLockKey(String.valueOf(newchars),0);
		System.err.println("Sending "+key);
		sendData("$Key "+key+"|");
	}
	/*
	
	Method for cleaning up lock/key

	mode indicates how to clean lock/key. 0 for replace chars with odd strings, 1 remove odd string for chars
	*/
	public static String sanitizeLockKey(String lockkey,int mode){
		
		int len = lockkey.length();
		if (len==0){		
			String sanitizedLockkey = "";
			for (int i=0; i<len; ++i){
				int val = lockkey.charAt(i);
				switch(val){
					case 0:{
						sanitizedLockkey+="/%DCN000%/";
						break;}
					case 5:{
						sanitizedLockkey+="/%DCN005%/";
						break;}
					case 36:{
						sanitizedLockkey+="/%DCN036%/";
						break;}
					case 96:{
						sanitizedLockkey+="/%DCN096%/";
						break;}
					case 124:{
						sanitizedLockkey+="/%DCN124%/";
						break;}
					case 126:{
						sanitizedLockkey+="/%DCN126%/";
						break;}
					default:{
						sanitizedLockkey+=""+lockkey.charAt(i);
						break;}
				}
			}
			return sanitizedLockkey;
		}
		else{
			lockkey=lockkey.replaceAll("/%DCN000%/",String.valueOf(Character.toChars(0)));
			lockkey=lockkey.replaceAll("/%DCN005%/",String.valueOf(Character.toChars(5)));
			lockkey=lockkey.replaceAll("/%DCN036%/",String.valueOf(Character.toChars(36)));
			lockkey=lockkey.replaceAll("/%DCN096%/",String.valueOf(Character.toChars(96)));
			lockkey=lockkey.replaceAll("/%DCN124%/",String.valueOf(Character.toChars(124)));
			lockkey=lockkey.replaceAll("/%DCN126%/",String.valueOf(Character.toChars(126)));
			return lockkey;
		}
	}
	/*
	Method for generating random key
	
	method stolen form http://stackoverflow.com/a/41156
	*/
	private SecureRandom random = new SecureRandom();
	public String getKey() {
		return new BigInteger(130, random).toString(18);
	}
	/*
	
	Method for generating integers between range
	
	*/
	public int randInt(int min, int max){
		return min + (int)(Math.random() * ((max - min) + 1));
	}
}
