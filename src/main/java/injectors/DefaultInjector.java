package injectors;

import com.google.inject.AbstractModule;
import protocol.dc.nmdc.NMDC;
import interfaces.DCBroadcastReceiver;
import models.BroadcastTester;
import protocol.dc.DCProtocol;

public class DefaultInjector extends AbstractModule {
    @Override
    protected void configure() {
        bind(DCProtocol.class).to(NMDC.class);
        bind(DCBroadcastReceiver.class).to(BroadcastTester.class);
    }

}
