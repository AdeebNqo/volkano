package models;

import interfaces.DCBroadcastReceiver;

//this class is used to test the hubs
//broadcast feature
public class HubBroadcastEcho implements DCBroadcastReceiver {
    public void onReceive(String someBroadcastMessage) {
        System.out.println("hub says "+someBroadcastMessage);
    }
}
