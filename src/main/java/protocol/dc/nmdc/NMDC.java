package protocol.dc.nmdc;

import java.io.IOException;
import java.util.Collection;
import java.io.File;
import exceptions.PasswordException;
import java.util.ArrayList;
import interfaces.DCBroadcastReceiver;
import models.NMDCHub;
import models.ValidateNickResponse;
import protocol.dc.DCProtocol;
import protocol.dc.HubCommunicator;
import exceptions.HubConnectionException;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeoutException;
import com.google.inject.Inject;
import interfaces.IConfiguration;
/*

The protocol was found at http://wiki.gusari.org/

 */
public class NMDC extends DCProtocol implements DCBroadcastReceiver {

    private HubCommunicator hubComm;
    private String hubName;
    private NMDCHub hub;

    public NMDC(){} //TODO: find out why this has to exist for Guice

    @Inject
    public NMDC(IConfiguration config){
            super(config);
    }

    public void connect() throws InterruptedException, IOException, PasswordException, HubConnectionException {

        hub = new NMDCHub(getAddress(), getPort());
        hubComm = hub.getHubComm();

        if (config.isDebugOn())
            System.err.println("About to connect to hub.");

        exchangeLock();
        ValidateNickResponse validatedNickResponse = validateNick();
        boolean canEnterHub = false;
        if (validatedNickResponse.isNickRegistered() && !validatedNickResponse.isNickValid()) {
            //wrong password
            //canEnterHub = false;
            throw new PasswordException("Incorrect password");
        } else if (!validatedNickResponse.isNickRegistered() && validatedNickResponse.isNickValid()) {
            //remind registration, can continue though
            canEnterHub = true;
        } else if (validatedNickResponse.isNickRegistered() && validatedNickResponse.isNickValid()) {
            //say welcome back perhaps
            canEnterHub = true;
        } else {
            //cannot connect to hub.
            //canEnterHub = false;
            throw new HubConnectionException("Cannot connect. Check if username is valid or internet connection is available");
        }

        if (canEnterHub) {
            //sending version and myinfo
            updateUserInfo();

            //getting hub name
            if (config.isDebugOn())
                System.err.println("Getting hub name.");
            String response = hubComm.getHubData();
            if (config.isDebugOn())
                System.err.println("The hub name request was responded with "+response);

            //scheduling a task to poll for broadcasts
            if (config.isDebugOn())
                System.err.println("scheduling broadcast poll system");
            ScheduledExecutorService broadcastSrvice = Executors.newSingleThreadScheduledExecutor();
            broadcastSrvice.scheduleAtFixedRate( new Runnable() {
                @Override
                public void run() {
                    try {
                        String broadcastVal = hubComm.getBroadcastData();
                        if (broadcastVal!=null) {
                            onReceive(broadcastVal);
                        }
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                }
            }, 0, 50, TimeUnit.MILLISECONDS);
        }
    }

    private void exchangeLock() throws IOException {
        if (config.isDebugOn())
            System.err.println("Attempting to get lock from the hub.");

        //getting the 'lock' string from the hub.
        String lockString = hubComm.getHubData();

        if (config.isDebugOn())
            System.err.println("received lock. value is "+lockString);

        sendSupportedFeatures();

        //sending key to hub
        if (lockString != null) {
            if (config.isDebugOn())
                System.err.println("Sending key to hub...");
            String key = NMDCUtil.generateKey(lockString);
            hubComm.sendDataToHub("$Key "+key+"|");
            if (config.isDebugOn())
                System.err.println("value of key is "+key);
        }
    }

    private void sendSupportedFeatures() throws IOException {
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
            String hubSupports = hubComm.getHubData();
            hub.addSupportedFeatures(hubSupports);
            if (config.isDebugOn())
                System.err.println("Hub responded with its supports string. value is "+hubSupports);
        } else if (config.isDebugOn()) {
            System.err.println("No supported features detected.");
        }
    }

    private ValidateNickResponse validateNick() throws IOException, PasswordException {
        if (config.isDebugOn())
            System.err.println("Requesting validation of username "+getUsername());
        //sending client's nick
        hubComm.sendDataToHub("$ValidateNick "+getUsername()+"|");

        String response = "";
        boolean nickIsRegistered = false;
        boolean isNickValid = false;

        try {
            response = hubComm.getHubData(30);
            if (config.isDebugOn())
                System.err.println("Hub responded with "+response);

            if (response.startsWith("$GetPass")) {
                //sending pass if neccessary
                //if hub requires password
                nickIsRegistered = true;
                String password = getPassword();
                if (password.isEmpty()) {
                    throw new exceptions.PasswordException("Password not provided, however, it is required by hub.");
                } else {
                    hubComm.sendDataToHub("$MyPass "+password+"|");
                    response = hubComm.getHubData(30);
                    if (response.startsWith("$BadPass")) {
                        isNickValid = false;
                        nickIsRegistered = true;
                    }
                }
            }
            if (response.startsWith("$Hello")) {
                isNickValid = true;
            } else {
                nickIsRegistered = false;
                isNickValid = false;
            }
        } catch (TimeoutException e) {
            if (config.isDebugOn()) {
                System.err.println("Hub did not respond to validate nick request" );
            }
            nickIsRegistered = false;
            isNickValid = true; //TODO this is tmp
        }

        return new ValidateNickResponse(isNickValid, nickIsRegistered, response);
    }


    public void updateUserInfo() throws IOException {
        if (config.isDebugOn())
            System.err.println("Updating user info. "+"$MyINFO $ALL "+getUsername()+" "+config.getDescription()+"$ <++ V:0.673,M:P,H:0/1/0,S:2>$ $LAN(T3)0x31$"+config.getEmail()+"$1234$|");
        //hubCommunicator.sendDataToHub("$Version 1.0|");
        hubComm.sendDataToHub("$MyINFO $ALL "+getUsername()+" "+config.getDescription()+"$ <++ V:0.673,M:P,H:0/1/0,S:2>$ $LAN(T3)0x31$"+config.getEmail()+"$1234$|");
        //TODO: most of the information being sent to the hub is not set anywhere. that should be fixed. a settings/configuration already exists. move it
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
