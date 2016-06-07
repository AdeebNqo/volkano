package control;

import injectors.DefaultInjector;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Driver {
        public static void main(String[] args) {

            Injector injector = Guice.createInjector(new DefaultInjector());
            Controller app = injector.getInstance(Controller.class);

            app.loop();
        }
}
