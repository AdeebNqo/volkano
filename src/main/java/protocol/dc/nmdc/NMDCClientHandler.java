package protocol.dc.nmdc;

import models.Connection;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.net.ServerSocket;
import java.io.InputStream;

import protocol.dc.ClientHandler;
import protocol.dc.HubCommunicator;

public class NMDCClientHandler extends ClientHandler{

        private final Scanner inputReader;
        private HubCommunicator hubComm = HubCommunicator.getInstance();

        public NMDCClientHandler(Connection clientConnection) throws java.io.IOException{
                super(clientConnection);

                inputReader = new Scanner(clientConnection.getInputStream());
                inputReader.useDelimiter("\\|");

                setDownloadPassive(true); //TODO: remove this here, this should be set in configuration file
        }
        public void connect() throws IOException, Exception{

                if (isDownloadPassive()){
                        //creating a listener for current client
                        ServerSocket localListener = new ServerSocket(0);
                        setLocalAddress("127.0.0.1"); //TODO: determine IP from server socket
                        setLocalPort(localListener.getLocalPort());

                        hubComm.sendDataToHub("$ConnectToMe "+getRemoteUsername()+" "+getLocalAddress()+":"+getLocalPort()+"|");
                        String response = hubComm.getHubData();

                        //accepting the connecting of the remote client and creating
                        //the connection between the two
                        setConnection(new Connection(localListener.accept()));
                        localListener.close();
                }else{
                        //TODO: find out how to connect non passively.
                }
        }
        public File getFileList() throws IOException, Exception{
                sendDataToClient("$ADCGET file files.xml 0 -1|");
                String response = getClientData();
                int filesize = Integer.parseInt(response.split(" ")[4]);

                //getting the file
                String fileString = "";
                InputStream input = getConnection().getInputStream();
                int read = 0;
                while (read<filesize){
                        int availableBytes = input.available();
                        byte[] inputBuffer = new byte[availableBytes];
                        input.read(inputBuffer);
                        fileString += new String(inputBuffer);
                        read+=availableBytes;
                }
                //TODO: convert a string into a file object.
                return null;
        }
}
