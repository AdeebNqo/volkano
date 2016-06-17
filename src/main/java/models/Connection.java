package models;

import java.io.IOException;
import java.net.Socket;
import java.io.InputStream;
import java.io.OutputStream;

public class Connection {

    private Socket socket = null;
    private InputStream inputStream = null;
    private OutputStream outputStream = null;

    public Connection(Socket socket) throws java.io.IOException{
        this.socket = socket;
        if (socket!=null) {
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
    public InputStream getInputStream() throws java.io.IOException{
        if (inputStream==null) {
            setInputStream(socket.getInputStream());
        }
        return inputStream;
    }

    public void setOutputStream(OutputStream outputStream){
            this.outputStream = outputStream;
    }
    public OutputStream getOutputStream() throws java.io.IOException{
        if (outputStream == null){
            setOutputStream(socket.getOutputStream());
        }
        return outputStream;
    }

    public boolean isOtherPartyReachable() {
        boolean result = false;
        try {
            result = socket.getInetAddress().isReachable(30000);
        } catch(IOException e) {
            result = false;
        }
        return result;
    }
}
