package interfaces;
public interface DCBroadcaster{

        public void register(DCBroadcastReceiver someBroadcastReceiver);
        public void unregister(DCBroadcastReceiver someBroadcastReceiver);

        public void broadcast(String broadcastMessage);
        public void HandleHubBroadcasts(); //this is the method where the protocol
                                           //should get all broadcasts from the hub
}
