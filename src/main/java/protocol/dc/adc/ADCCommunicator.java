package protocol.dc.adc;

import protocol.dc.Communicator;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;

public class ADCCommunicator extends Communicator{
  public  boolean hasData() throws IOException{
    return false;
  }
  public  void sendData(OutputStream outputStream, String data){

  }
  public  String getData(InputStream inputStream) throws IOException{
    return null;
  }
}
