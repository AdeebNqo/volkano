package models;

import interfaces.DCBroadcastReceiver;

public class BroadcastTester implements DCBroadcastReceiver{
        public void onReceive(String someBroadcastMessage){
                System.out.println("hub says "+someBroadcastMessage);
        }
}
