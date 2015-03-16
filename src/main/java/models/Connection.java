package models;

import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

public class Connection{

        private Socket socket = null;
        private InputStream inputStream = null;
        private OutputStream outputStream = null;

        public Connection(){

        }
        public Connection(Socket socket) throws java.io.IOException{
                this.socket = socket;
                if (socket!=null){
                        setInputStream(socket.getInputStream());
                        setOutputStream(socket.getOutputStream());
                }
        }

        public void setSocket(Socket socket){
                this.socket = socket;
        }
        public Socket getSocket(){
                return socket;
        }

        public void setInputStream(InputStream inputStream){
                this.inputStream = inputStream;
        }
        public InputStream getInputStream(){
                return inputStream;
        }

        public void setOutputStream(OutputStream outputStream){
                this.outputStream = outputStream;
        }
        public OutputStream getOutputStream(){
                return outputStream;
        }
}