package injectors;

import com.google.inject.AbstractModule;
import control.JsonSettings;
import protocol.dc.nmdc.NMDC;
import interfaces.DCBroadcastReceiver;
import models.HubBroadcastEcho;
import protocol.dc.DCProtocol;
import interfaces.IConfiguration;

public class DefaultInjector extends AbstractModule {
    @Override
    protected void configure() {
        bind(DCProtocol.class).to(NMDC.class);
        bind(DCBroadcastReceiver.class).to(HubBroadcastEcho.class);
        bind(IConfiguration.class).to(JsonSettings.class);
    }

}
