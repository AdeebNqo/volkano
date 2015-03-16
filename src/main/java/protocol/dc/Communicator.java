package protocol.dc;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import java.io.IOException;
import models.Connection;

public abstract class Communicator{
        private Connection hubConnection;

        public void setHubConnection(Connection connection){
                hubConnection = connection;
        }
        public Connection getHubConnection(){
                return hubConnection;
        }
        public String getHubData() throws Exception{
                return getData(hubConnection.getInputStream());
        }
        public void sendDataToHub(String data) throws IOException{
                sendData(hubConnection.getOutputStream(), data);
        }

        //the following methods are for sending data and receiving it
        public String getData(InputStream inputStream) throws Exception{
                int availableDataSize = inputStream.available();
                byte[] responseBuffer = new byte[availableDataSize];
                int val = inputStream.read(responseBuffer);
                if (val==-1){
                        throw new Exception("Could not read the "+availableDataSize+" bytes that were advertized as being available.");
                }
                String response = new String(responseBuffer, "UTF-8");
                return response;
        }
        public void sendData(OutputStream outputStream, String data) throws IOException{
                outputStream.write(data.getBytes(Charset.forName("UTF-8")));
                outputStream.flush();
        }
        public String getKeyFromLock(String item) throws IOException{
                String[] values = item.split(" ");
                String lock = sanitizeKey(values[1],1);
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
                key = sanitizeKey(String.valueOf(newchars),0);
                return key;
        }
        /*

        Method for cleaning up lock/key

        mode indicates how to clean lock/key. 0 for replace chars with odd strings, 1 remove odd string for chars
        */
        public String sanitizeKey(String Key,int mode){

                int len = Key.length();
                if (len==0){
                        String sanitizedKey = "";
                        for (int i=0; i<len; ++i){
                                int val = Key.charAt(i);
                                switch(val){
                                        case 0:{
                                                sanitizedKey+="/%DCN000%/";
                                                break;
                                        }
                                        case 5:{
                                                sanitizedKey+="/%DCN005%/";
                                                break;
                                        }
                                        case 36:{
                                                sanitizedKey+="/%DCN036%/";
                                                break;
                                        }
                                        case 96:{
                                                sanitizedKey+="/%DCN096%/";
                                                break;
                                        }
                                        case 124:{
                                                sanitizedKey+="/%DCN124%/";
                                                break;
                                        }
                                        case 126:{
                                                sanitizedKey+="/%DCN126%/";
                                                break;
                                        }
                                        default:{
                                                sanitizedKey+=""+Key.charAt(i);
                                                break;
                                        }
                                }
                        }
                        return sanitizedKey;
                }
                else{
                        Key=Key.replaceAll("/%DCN000%/",String.valueOf(Character.toChars(0)));
                        Key=Key.replaceAll("/%DCN005%/",String.valueOf(Character.toChars(5)));
                        Key=Key.replaceAll("/%DCN036%/",String.valueOf(Character.toChars(36)));
                        Key=Key.replaceAll("/%DCN096%/",String.valueOf(Character.toChars(96)));
                        Key=Key.replaceAll("/%DCN124%/",String.valueOf(Character.toChars(124)));
                        Key=Key.replaceAll("/%DCN126%/",String.valueOf(Character.toChars(126)));
                        return Key;
                }
        }

}
