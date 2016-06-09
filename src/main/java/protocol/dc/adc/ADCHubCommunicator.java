package protocol.dc.adc;

import models.Connection;
import protocol.dc.HubCommunicator;
import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ADCHubCommunicator extends ADCCommunicator implements HubCommunicator {

    public ADCHubCommunicator(Connection connection) {
    }

    @Override
    public String getHubData() {
        return null;
    }

    @Override
    public String getHubData(int timeoutSecs) throws TimeoutException {
        return null;
    }

    @Override
    public String getBroadcastData() {
        return null;
    }

    @Override
    public void sendDataToHub(String data) throws IOException {

    }
}
