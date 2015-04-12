package protocol.dc.nmdc;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.io.File;

import models.Connection;
import java.net.Socket;

import java.util.ArrayList;
import java.util.HashMap;
import interfaces.DCBroadcastReceiver;
import protocol.dc.DCProtocol;
import protocol.dc.nmdc.NMDCUtil;

import protocol.dc.HubCommunicator;
import exceptions.HubConnectionException;

import java.util.concurrent.*;

public class NMDC extends DCProtocol implements DCBroadcastReceiver{

        private boolean isDownloadPassive = false;

        private HubCommunicator hubComm;
        private String hubName;

        public NMDC(String username, String address, int port){
                super( username,  address,  port);
        }
        public void connect() throws UnknownHostException,IOException, InterruptedException, Exception{
                Connection hubConnection = new Connection(new Socket(getAddress(), getPort()));

                HubCommunicator.Init(hubConnection);
                hubComm = HubCommunicator.getInstance();

                //getting the 'lock' string from the hub.
                String lockString = hubComm.getHubData();
                String lockValue = NMDCUtil.identifyLock(lockString);

                //todo: send the supports string

                //sending key to hub
                if (lockValue!=null){
                        String key = NMDCUtil.getKeyFromLock(lockValue);
                        hubComm.sendDataToHub("$Key "+key+"|");
                }

                //sending client's nick
                hubComm.sendDataToHub("$ValidateNick "+getUsername()+"|");
                String response;

                response = hubComm.getHubData();
                //System.err.println(response);
                if (response.startsWith("$GetPass")){

                        //sending pass if neccessary
                        //if hub requires password
                        String password = getPassword();
                        if (password.isEmpty()){
                                throw new exceptions.PasswordException("Password not provided, however, it is required by hub.");
                        }else{
                                hubComm.sendDataToHub("$MyPass "+password+"|");
                        }
                }

                //checking connection status
                response = hubComm.getHubData();
                if (!response.equals("$LogedIn")){
                        throw new HubConnectionException("Cannot log into hub.");
                }

                //sending version and myinfo
                hubComm.sendDataToHub("$Version 1.0|");
                hubComm.sendDataToHub("$MyINFO $ALL "+getUsername()+" <++ V:0.673,M:P,H:0/1/0,S:2>$ $LAN(T3)0x31$test@test.com$1234$|");
                //TODO: most of the information being sent to the hub is not set anywhere. that should be fixed. a settings/configuration
                //page should be responsible for setting/creating this information. It shouldn't be static.

                //getting hub name
                response = hubComm.getHubData();
                hubName = NMDCUtil.getHubName(response);

                ScheduledExecutorService broadcastSrvice = Executors.newSingleThreadScheduledExecutor();
                broadcastSrvice.scheduleAtFixedRate( new Runnable(){
                        @Override
                        public void run(){
                                try{
                                        String broadcastVal = hubComm.getBroadcastData();
                                        if (broadcastVal!=null){
                                                onReceive(broadcastVal);
                                        }
                                }catch(Exception e){
                                        e.printStackTrace();
                                }
                        }
                }, 0, 50, TimeUnit.MILLISECONDS);
        }
        public Collection<String> requestConnectedUsersNicks() throws Exception{
                hubComm.sendDataToHub("$GetNickList|");

                String response = hubComm.getHubData();

                Collection<String> NickList = new ArrayList<String>();
                String[] listItems = response.split("\\|");
                for (String listitem:listItems){
                        listitem = listitem.trim();
                        if (listitem.startsWith("$NickList")){
                                int pos = listitem.indexOf(' ');
                                String usersdoubledollar = listitem.substring(pos).trim();
                                String[] nicks = usersdoubledollar.split("\\$\\$");
                                for (String nick:nicks){
                                        NickList.add(nick);
                                }
                        }
                }
                return NickList;
        }
        public File getFileList(String username){
                return null;
        }

        //propagate hub broadcasts
        public void onReceive(String someBroadcastMessage){
                broadcast(someBroadcastMessage);
        }
}
