package protocol.dc;

import java.io.IOException;
import models.Connection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public interface HubCommunicator{
        public String getHubData();
        public String getBroadcastData();
        public void sendDataToHub(String data) throws IOException;
}
