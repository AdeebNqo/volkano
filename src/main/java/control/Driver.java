package control;

import models.Connection;
import protocol.dc.nmdc.NMDC;
import protocol.dc.adc.ADC;
import java.net.Socket;
import interfaces.DCBroadcastReceiver;
import loaders.ProtocolLoader;
import protocol.dc.DCProtocol;

import java.io.IOException;
import java.net.UnknownHostException;

public class Driver {
        public static void main(String[] args){
                System.out.println("Connecting to Hub...");
                final String IP = "127.0.0.1";
                final int port = 9090;

                Controller control = new Controller(IP, port);
                control.loop();
                
                /*BroadcastTester bTester = new BroadcastTester();

                ProtocolLoader pLoader = ProtocolLoader.getInstance();
                DCProtocol dc = pLoader.getProtocol("FlexGui", IP, port);
                dc.setPassword("abalimi");
                dc.register(bTester);
                try{
                        dc.connect();
                        System.err.println("Connected!");
                }catch(Exception e){
                        e.printStackTrace();
                }
                while(true){

                }*/
        }

        static class BroadcastTester implements DCBroadcastReceiver{
                public void onReceive(String someBroadcastMessage){
                        System.out.println("hub says "+someBroadcastMessage);
                }
        }
}
