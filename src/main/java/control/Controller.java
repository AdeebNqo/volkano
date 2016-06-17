package control;

import interfaces.IConfiguration;
import protocol.dc.DCProtocol;
import com.google.inject.Inject;

import interfaces.DCBroadcastReceiver;

import java.util.Collection;

public class Controller {

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
            Collection<String> users = dcprotocol.requestConnectedUsersNicks();
            if (config.isDebugOn())
                System.err.println("Connected. There are "+users.size()+" users online.");
        } catch(Exception e) {
            e.printStackTrace();
        }

        //TODO this shoudn't exist
        while(true) {

        }
    }
}
