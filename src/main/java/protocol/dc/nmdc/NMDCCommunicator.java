package protocol.dc.nmdc;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.util.Scanner;
import protocol.dc.Communicator;

public class NMDCCommunicator extends Communicator {
    public boolean hasData() throws IOException {
            Scanner dataReader = new Scanner(connection.getInputStream());
            return dataReader.hasNext();
    }
    public void sendData(OutputStream outputStream, String data) {
      try{
        outputStream.write(data.getBytes());
        outputStream.flush();
      }catch(Exception e) {
        e.printStackTrace();
      }
    }
    public String getData(InputStream inputStream) throws IOException {
        Scanner reader = new Scanner(inputStream);
        reader.useDelimiter("\\|");
        return reader.hasNext() ? reader.next() : "";
    }
}
