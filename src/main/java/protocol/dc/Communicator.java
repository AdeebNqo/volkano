package protocol.dc;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import java.io.IOException;
import models.Connection;
import java.util.Scanner;
import java.util.HashMap;

import java.io.InputStreamReader;
import java.io.Reader;

public class Communicator{
        public Connection connection;

        public void setConnection(Connection connection){
                this.connection = connection;
        }
        public Connection getConnection(){
                return connection;
        }

        public boolean hasData() throws IOException{
                Scanner dataReader = new Scanner(connection.getInputStream());
                return dataReader.hasNext();
        }
        public void sendData(String data) throws IOException{
                sendData(connection.getOutputStream(), data);
        }
        public void sendData(OutputStream outputStream, String data) throws IOException{
                outputStream.write(data.getBytes());
                outputStream.flush();
        }

        public String getData(InputStream inputStream) throws IOException{
                /*String result = "";
                int read = 0;
                while((read=inputStream.read())!=-1){
                        System.err.println("read: "+read);
                        result += read;
                }
                return result;*/

                /*int bufferSize = 1024;

                final char[] buffer = new char[bufferSize];
                final StringBuilder out = new StringBuilder();

                try(Reader in = new InputStreamReader(inputStream, "UTF-8")){
                        for (;;){
                                int rsz = in.read(buffer, 0, buffer.length);
                                if (rsz<0){
                                        break;
                                }
                                out.append(buffer, 0, rsz);
                        }
                }catch(Exception e){
                        e.printStackTrace();
                }
                return out.toString();*/
                Scanner reader = new Scanner(inputStream);
                reader.useDelimiter("\\|");

                return reader.hasNext() ? reader.next() : "";
        }
}
