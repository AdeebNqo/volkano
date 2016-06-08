package protocol.dc.nmdc;

import java.io.IOException;
import models.Connection;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import protocol.dc.HubCommunicator;

public class NMDCHubCommunicator extends NMDCCommunicator implements HubCommunicator{

        final Queue<String> hubData = new ConcurrentLinkedQueue<String>();
        final Queue<String> broadcastData = new ConcurrentLinkedQueue<String>();

        public NMDCHubCommunicator(Connection connection) {
            setConnection(connection);
            ScheduledExecutorService getHubDataService = Executors.newSingleThreadScheduledExecutor();
            System.err.println("Scheduling getHubDataService");
            getHubDataService.scheduleAtFixedRate( new Runnable() {
                    @Override
                    public void run() {
                        try {
                            System.err.println("attempting to read stream");
                            String data = getData(getConnection().getInputStream());
                            System.err.println("data is "+data);
                            if (!data.isEmpty()) {
                                    System.err.println("hub comm, data: "+data);
                                    //TODO: decide whether to insert into hubdata or broadcast data

                                    if (data.startsWith("<")) {
                                        broadcastData.add(data);
                                    } else {
                                        hubData.add(data);
                                    }
                            }
                        } catch(Exception e) {
                            e.printStackTrace();
                        }
                    }
            }, 0, 50, TimeUnit.MILLISECONDS);
        }

        public String getHubData() {
                //hubdata consumer
                String data = hubData.poll();
                while(data==null) {
                        data = hubData.poll();
                }
                return data;
        }
        public String getBroadcastData() {
                //broadcast data consumer
                String data = broadcastData.poll();
                while(data==null) {
                        data = broadcastData.poll();
                }
                return data;
        }

        public void sendDataToHub(String data) throws IOException {
                sendData(data);
        }
}
