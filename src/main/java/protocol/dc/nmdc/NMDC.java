package protocol.dc.nmdc;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.io.File;

import exceptions.PasswordException;
import models.Connection;
import java.net.Socket;

import java.util.ArrayList;
import java.util.HashMap;
import interfaces.DCBroadcastReceiver;
import protocol.dc.DCProtocol;
import protocol.dc.nmdc.NMDCUtil;

import protocol.dc.HubCommunicator;
import exceptions.HubConnectionException;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;

import com.google.inject.Inject;
import interfaces.IConfiguration;

public class NMDC extends DCProtocol implements DCBroadcastReceiver{

    private HubCommunicator hubComm;
    private String hubName;

    public NMDC(){} //TODO: find out why this has to exist for Guice

    @Inject
    public NMDC(IConfiguration config){
            super(config);
    }
    public void connect() throws InterruptedException, IOException, PasswordException, HubConnectionException {
        Connection hubConnection = new Connection(new Socket(getAddress(), getPort()));

        hubComm = new NMDCHubCommunicator(hubConnection);

        if (config.isDebugOn())
            System.err.println("Attempting to get lock from the hub.");

        //getting the 'lock' string from the hub.
        String lockString = hubComm.getHubData();
        String lockValue = NMDCUtil.identifyLock(lockString);

        if (config.isDebugOn())
            System.err.println("received lock. value is "+lockValue);

        List<String> supportedFeatures = config.getSupportedFeatures();
        if (supportedFeatures!=null && !supportedFeatures.isEmpty()) {
            //building $Supports string
            String supportsString = "$Supports";
            for (String supportedFeature : supportedFeatures) {
                supportsString += " "+supportedFeature;
            }
            supportsString += "|";

            //sending hub $Supports string
            hubComm.sendDataToHub(supportsString);
            if (config.isDebugOn())
                System.err.println("Sent hub supports string. value is "+supportsString);
        } else if (config.isDebugOn()) {
            System.err.println("No supported features detected.");
        }

        //sending key to hub
        if (lockValue != null) {
            if (config.isDebugOn())
                System.err.println("Sending key to hub...");
            String key = NMDCUtil.getKeyFromLock(lockValue);
            hubComm.sendDataToHub("$Key "+key+"|");
            if (config.isDebugOn())
                System.err.println("value of key is "+key);
        }

        if (config.isDebugOn())
            System.err.println("Requesting validation of username "+getUsername());
        //sending client's nick
        hubComm.sendDataToHub("$ValidateNick "+getUsername()+"|");
        String response;

        try {
            response = hubComm.getHubData(30);
            if (config.isDebugOn())
                System.err.println("Hub responded with "+response);

            if (response.startsWith("$GetPass")) {

                //sending pass if neccessary
                //if hub requires password
                String password = getPassword();
                if (password.isEmpty()) {
                    throw new exceptions.PasswordException("Password not provided, however, it is required by hub.");
                } else {
                    hubComm.sendDataToHub("$MyPass "+password+"|");
                }
            } else if (config.isDebugOn()) {
                System.err.println("Hub responded with "+response+", instead of $GetPass");
            }
        } catch (TimeoutException e) {
            if (config.isDebugOn()) {
                System.err.println("Hub did not respond to validate nick request" );
                //e.printStackTrace();
            }
        }


        //checking connection status
        response = hubComm.getHubData();
        if (!response.equals("$LogedIn")) {
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
        broadcastSrvice.scheduleAtFixedRate( new Runnable() {
                @Override
                public void run() {
                        try {
                                String broadcastVal = hubComm.getBroadcastData();
                                if (broadcastVal!=null){
                                        onReceive(broadcastVal);
                                }
                        } catch(Exception e) {
                                e.printStackTrace();
                        }
                }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    public Collection<String> requestConnectedUsersNicks() throws Exception {
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
