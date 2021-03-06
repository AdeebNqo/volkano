package protocol.dc.adc;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.io.File;
import interfaces.DCBroadcastReceiver;

import protocol.dc.DCProtocol;
import protocol.dc.HubCommunicator;
import models.Connection;
import java.net.Socket;

import com.google.inject.Inject;
import interfaces.IConfiguration;

public class ADC extends DCProtocol{

        private HubCommunicator hubComm;
        private String hubName;

        public ADC(){}

        @Inject
        public ADC(IConfiguration config){
                super(config);
        }

        public void connect() throws UnknownHostException,IOException, InterruptedException, Exception{
                Connection hubConnection = new Connection(new Socket(getAddress(), getPort()));

                hubComm = new ADCHubCommunicator (hubConnection);

                hubComm.sendDataToHub("HSUP ADBASE");
                String greeting = hubComm.getHubData();
                System.err.println("greeting: "+greeting);
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
