package control;

import models.Connection;
import protocol.dc.nmdc.NMDC;
import protocol.dc.adc.ADC;
import java.net.Socket;
import interfaces.DCBroadcastReceiver;
import loaders.ProtocolLoader;
import protocol.dc.DCProtocol;

import java.io.IOException;
import java.net.UnknownHostException;

import injectors.DefaultInjector;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Driver {
        public static void main(String[] args){

                final String IP = "127.0.0.1";
                final int port = 9090;

                Injector injector = Guice.createInjector(new DefaultInjector());
                Controller app = injector.getInstance(Controller.class);

                app.setIP(IP);
                app.setPort(port);

                app.loop();
        }
}
