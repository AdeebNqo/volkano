/*

This is the interface  that all
DC protocols should follow

*/
package protocol.dc;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.io.File;

import models.Connection;
import interfaces.DCBroadcaster;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Iterator;
import interfaces.DCBroadcastReceiver;

public abstract class DCProtocol implements DCBroadcaster{

        Collection<DCBroadcastReceiver> dCBroadcasterSubscribers = new LinkedList<DCBroadcastReceiver>();

        private String username;
        private String password;

        private String address;
        private int port;

        public DCProtocol(){} //TODO: find out why this has to exit for Guice

        public DCProtocol(String username, String address, int port){
                this.username = username;
                this.address = address;
                this.port = port;
        }
        public String getAddress(){
                return address;
        }
        public int getPort(){
                return port;
        }
        public String getUsername(){
                return username;
        }
        public void setPassword(String password){
                this.password = password;
        }
        public String getPassword(){
                return password;
        }
        public void broadcast(String broadcastMessage){
                Iterator<DCBroadcastReceiver> it = dCBroadcasterSubscribers.iterator();
                while(it.hasNext()){
                        DCBroadcastReceiver subscriber = it.next();
                        if (subscriber==null){
                                it.remove();
                        }else{
                                subscriber.onReceive(broadcastMessage);
                        }
                }
        }
        public void register(DCBroadcastReceiver someBroadcastReceiver){
                dCBroadcasterSubscribers.add(someBroadcastReceiver);
        }
        public void unregister(DCBroadcastReceiver someBroadcastReceiver){
                dCBroadcasterSubscribers.remove(someBroadcastReceiver);
        }
        public abstract void connect() throws UnknownHostException,IOException, InterruptedException, Exception;
        public abstract Collection<String> requestConnectedUsersNicks() throws Exception;
        public abstract File getFileList(String username);
}
