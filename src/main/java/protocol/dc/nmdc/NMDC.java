package protocol.dc.nmdc;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.io.File;

import models.Connection;
import java.net.Socket;

import java.util.ArrayList;
import interfaces.DCBroadcastReceiver;
import protocol.dc.DCProtocol;

public class NMDC extends DCProtocol{

        private boolean isDownloadPassive = false;

        public NMDC(String username, String address, int port){
                super( username,  address,  port);
        }
        public void connect() throws UnknownHostException,IOException, InterruptedException, Exception{
                Connection hubConnection = new Connection(new Socket(getAddress(), getPort()));
                setHubConnection(hubConnection);

                String response = getHubData();
                //sending the supports string
                String[] items = response.split("\\|");
                for (String item: items){
                        if (item.startsWith("$Lock")){
                                String key = getKeyFromLock(item);
                                sendDataToHub(key);
                        }
                }
                //sending client's nick
                sendDataToHub("$ValidateNick "+getUsername()+"|");
                response = getHubData();

                //determining whether
                String[] itemsX = response.split("|");
                for (String itemX:itemsX){
                        if (itemX.startsWith("$GetPass")){
                                //if hub requires password
                                String password = getPassword();
                                if (password.isEmpty()){
                                        throw new exceptions.PasswordException("Password not provided, however, it is required by hub.");
                                }else{
                                        sendDataToHub("$MyPass "+password+"|");
                                        //retrieve hello client msg
                                        response = getHubData();
                                }
                        }
                }
                //sending version and myinfo
                sendDataToHub("$Version 1.0|");
                sendDataToHub("$MyINFO $ALL "+getUsername()+" <++ V:0.673,M:P,H:0/1/0,S:2>$ $LAN(T3)0x31$test@test.com$1234$|");
                //TODO: most of the information being sent to the hub is not set anywhere. that should be fixed. a settings/configuration
                //page should be responsible for setting/creating this information. It shouldn't be static.
        }
        public Collection<String> requestConnectedUsersNicks() throws Exception{
                sendDataToHub("$GetNickList|");

                String response = getHubData();

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

        public void HandleHubBroadcasts(){

        }
}
