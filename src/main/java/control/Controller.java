package control;

import interfaces.IConfiguration;
import protocol.dc.DCProtocol;
import com.google.inject.Inject;

import interfaces.DCBroadcastReceiver;

public class Controller{

    DCProtocol dcprotocol;
    IConfiguration config;

    @Inject
    public Controller(DCProtocol dc, DCBroadcastReceiver receiver, IConfiguration config) {
        dcprotocol = dc;
        this.config = config;
    }
    public void loop() {
        try {
                if (config.isDebugOn())
                    System.err.println("Connecting to Hub...");
                dcprotocol.connect();
                if (config.isDebugOn())
                    System.err.println("Connected!");
        } catch(Exception e) {
                e.printStackTrace();
        }

        //TODO this shoudn't exist
        while(true) {

        }
    }
}
