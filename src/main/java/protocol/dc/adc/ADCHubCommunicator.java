package protocol.dc.adc;

import protocol.dc.HubCommunicator;
import models.Connection;
import java.io.IOException;

public class ADCHubCommunicator extends ADCCommunicator implements HubCommunicator{
  public ADCHubCommunicator(Connection connection){

  }
  public String getHubData(){
    return null;
  }
  public String getBroadcastData(){
    return null;
  }
  public void sendDataToHub(String data) throws IOException{

  }
}
