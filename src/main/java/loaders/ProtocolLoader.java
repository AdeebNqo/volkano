package loaders;

import protocol.dc.DCProtocol;
import protocol.dc.adc.ADC;
import protocol.dc.nmdc.NMDC;

public class ProtocolLoader{
        private static ProtocolLoader INSTANCE;

        private final String configurationFile = "configuration.json";
        private final String configuredProtocol;
        public ProtocolLoader(){
                configuredProtocol = "nmdc"; //TODO: read the `configurationFile`
                                             //and set the `configuredProtocol` field.
        }
        public DCProtocol getProtocol(String username, String address, int port){
                if (configuredProtocol.equals("nmdc")){
                        return new NMDC(username, address, port);
                }
                else if (configuredProtocol.equals("adc")){
                        return new ADC(username, address, port);
                }
                return null;
        }
        public static ProtocolLoader getInstance(){
                if (INSTANCE == null){
                        INSTANCE = new ProtocolLoader();
                }
                return INSTANCE;
        }
}
