package control;

import models.Connection;
import java.net.Socket;
import interfaces.DCBroadcastReceiver;

import java.io.IOException;
import java.net.UnknownHostException;

public class Driver implements DCBroadcastReceiver{
        public static void main(String[] args){
                System.out.println("Connecting to (A)DC Hub...");
                final String IP = "127.0.0.1";
                final int port = 9191;
        }
        public void onReceive(String someBroadcastMessage){
                System.out.println("hub says "+someBroadcastMessage);
        }
}
