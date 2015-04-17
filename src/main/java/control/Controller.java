package control;

import dagger.ObjectGraph;
import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

import models.BroadcastTester;
import protocol.dc.DCProtocol;

import javax.inject.Inject;
import module.ApplicationModule;

public class Controller{
  private ObjectGraph graph;

  @Inject
  BroadcastTester bTester;
  @Inject
  DCProtocol dc;

  public Controller(String ip, int port){
    graph = ObjectGraph.create(getModules().toArray());
  }

  private List<Object> getModules() {
    List<Object> modules = new LinkedList<>();
    modules.add(
        new ApplicationModule()
    );
    return modules;
  }

  public void inject(Object object) {
    graph.inject(object);
  }

  public void loop(){
    try{
            dc.connect();
            System.err.println("Connected!");
    }catch(Exception e){
            e.printStackTrace();
    }
    while(true){

    }
  }
}
