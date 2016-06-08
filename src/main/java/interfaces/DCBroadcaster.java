package interfaces;
public interface DCBroadcaster {

        public void register(DCBroadcastReceiver someBroadcastReceiver);
        public void unregister(DCBroadcastReceiver someBroadcastReceiver);

        public void broadcast(String broadcastMessage);
}
