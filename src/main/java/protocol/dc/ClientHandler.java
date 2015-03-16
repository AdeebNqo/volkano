package protocol.dc;
/*

responsible for sending, downloading files
to and from another client.

*/
import models.Connection;
import java.io.File;
import java.io.IOException;

public abstract class ClientHandler extends Communicator{
        private Connection clientConnection;

        private boolean isDownloadPassive;
        private boolean isConnected;

        //These are the usernames of the clients
        //that will communicating.
        private String localUsername;
        private String remoteUsername;

        //this data is the one that identifies the remote client.
        //it will be used to connect to it.
        private String remoteAddress;
        private int remotePort;

        //this data is the one that identifies this client.
        //it will be sent to the remote client.
        private String localAddress;
        private int localPort;

        public ClientHandler(Connection hubConnection){
                setHubConnection(hubConnection);
        }
        public void setDownloadPassive(boolean value){
                isDownloadPassive = value;
        }
        public boolean isDownloadPassive(){
                return isDownloadPassive;
        }
        public boolean isConnected(){
                return isConnected;
        }
        public String getClientData() throws Exception{
                return getData(clientConnection.getInputStream());
        }
        public void sendDataToClient(String data) throws IOException{
                sendData(clientConnection.getOutputStream(), data);
        }
        public void setLocalUsername(String localUsername){
                this.localUsername = localUsername;
        }
        public void setRemoteUsername(String remoteUsername){
                this.remoteUsername = remoteUsername;
        }
        public String getLocalUsername(){
                return localUsername;
        }
        public String getRemoteUsername(){
                return remoteUsername;
        }
        public void setRemoteAddress(String remoteAddress){
                this.remoteAddress = remoteAddress;
        }
        public String getRemoteAddress(){
                return remoteAddress;
        }
        public void setRemotePort(int port){
                this.remotePort = port;
        }
        public int getRemotePort(){
                return remotePort;
        }

        public String getLocalAddress(){
                return localAddress;
        }
        public void setLocalAddress(String localAddress){
                this.localAddress = localAddress;
        }
        public int getLocalPort(){
                return localPort;
        }
        public void setLocalPort(int localPort){
                this.localPort = localPort;
        }
        public void setClientConnection(Connection clientConnection){
                this.clientConnection = clientConnection;
        }
        public Connection getClientConnection(){
                return clientConnection;
        }

        public abstract void connect() throws IOException, Exception;
        public abstract File getFileList() throws IOException, Exception;
}
