package protocol.dc.nmdc;

import java.io.IOException;
import models.Connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;

import protocol.dc.HubCommunicator;

public class NMDCHubCommunicator extends NMDCCommunicator implements HubCommunicator {

    final Queue<String> hubData = new ConcurrentLinkedQueue<String>();
    final Queue<String> broadcastData = new ConcurrentLinkedQueue<String>();

    public NMDCHubCommunicator(Connection connection) {
        setConnection(connection);
        ScheduledExecutorService getHubDataService = Executors.newSingleThreadScheduledExecutor();
        getHubDataService.scheduleAtFixedRate( new Runnable() {
                @Override
                public void run() {
                    try {
                        String data = getData(getConnection().getInputStream());
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
        System.err.println("hub says "+data);
        return data;
    }

    @Override
    public String getHubData(int timeoutSecs) throws TimeoutException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        TimeoutGetDataRunnable getDataAction = new TimeoutGetDataRunnable();
        executor.submit(getDataAction);
        try {
            executor.awaitTermination(timeoutSecs, TimeUnit.SECONDS);
            throw new TimeoutException();
        } catch (InterruptedException e) {
            //swallow it
        }
        return getDataAction.getData();
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

    private class TimeoutGetDataRunnable implements Runnable {
        String data = "";
        @Override
        public void run() {
            data = hubData.poll();
            while(data==null) {
                data = hubData.poll();
            }
        }

        public String getData() {
            if (data == null) {
                data = "";
            }
            return data;
        }
    }
}
