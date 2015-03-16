package protocol.dc.adc;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.io.File;
import interfaces.DCBroadcastReceiver;

import protocol.dc.DCProtocol;

public class ADC extends DCProtocol{
        public ADC(String username, String address, int port){
                super(username,address,port);
        }

        public void connect() throws UnknownHostException,IOException, InterruptedException, Exception{

        }
        public Collection<String> requestConnectedUsersNicks() throws Exception{
                return null;
        }
        public File getFileList(String username){
                return null;
        }
        public void HandleHubBroadcasts(){

        }
}
