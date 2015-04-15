package protocol.dc.nmdc;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import java.io.IOException;
import models.Connection;
import java.util.Scanner;
import java.util.HashMap;

import java.io.InputStreamReader;
import java.io.Reader;
import protocol.dc.Communicator;

public class NMDCCommunicator extends Communicator{
        public boolean hasData() throws IOException{
                Scanner dataReader = new Scanner(connection.getInputStream());
                return dataReader.hasNext();
        }
        public void sendData(OutputStream outputStream, String data) throws IOException{
                outputStream.write(data.getBytes());
                outputStream.flush();
        }
        public String getData(InputStream inputStream) throws IOException{
                Scanner reader = new Scanner(inputStream);
                reader.useDelimiter("\\|");

                return reader.hasNext() ? reader.next() : "";
        }
}
