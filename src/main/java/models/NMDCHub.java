package models;

import protocol.dc.HubCommunicator;
import protocol.dc.nmdc.NMDCHubCommunicator;

import java.io.IOException;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

public class NMDCHub {

    private List<SupportsFeature> supportedFeatures;
    private HubCommunicator hubComm;

    public NMDCHub(String ip, int port) throws IOException {
        Connection hubConnection = new Connection(new Socket(ip, port));
        hubComm = new NMDCHubCommunicator(hubConnection);
        supportedFeatures = new LinkedList<>();
    }

    public HubCommunicator getHubComm() {
        return hubComm;
    }
    public void addSupportedFeature(SupportsFeature feature) {
        supportedFeatures.add(feature);
    }

    public void addSupportedFeatures(String supportsString) {
        String[] stringFeatureReps = supportsString.replace("$Supports ","").split("\\s+");
        for (String stringFeature : stringFeatureReps) {
            try {
                stringFeature = stringFeature.replace("\\|", "");
                SupportsFeature feature = SupportsFeature.valueOf(stringFeature);
                supportedFeatures.add(feature);
            } catch (IllegalArgumentException e){
                //no such supports feature. swallow error
            }
        }
    }

    public boolean isFeatureSupported(SupportsFeature feature) {
        return supportedFeatures.contains(feature);
    }
}
