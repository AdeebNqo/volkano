package protocol.dc;

import java.io.IOException;
import java.util.concurrent.*;

public interface HubCommunicator{
    public String getHubData();
    public String getHubData(int timeoutSecs) throws TimeoutException;
    public String getBroadcastData();
    public void sendDataToHub(String data) throws IOException;
}
