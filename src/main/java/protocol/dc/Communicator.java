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

public abstract class Communicator{
        public Connection connection;

        public void setConnection(Connection connection){
                this.connection = connection;
        }
        public Connection getConnection(){
                return connection;
        }
        public void sendData(String data) throws IOException{
                sendData(connection.getOutputStream(), data);
        }

        public abstract boolean hasData() throws IOException;
        public abstract void sendData(OutputStream outputStream, String data);
        public abstract String getData(InputStream inputStream) throws IOException;
}
