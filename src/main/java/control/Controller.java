package control;

import java.util.Arrays;
import java.util.List;
import java.util.LinkedList;

import models.BroadcastTester;
import protocol.dc.DCProtocol;
import com.google.inject.Inject;

import interfaces.DCBroadcastReceiver;

public class Controller{

  DCProtocol dcprotocol;

  @Inject
  public Controller(DCProtocol dc, DCBroadcastReceiver receiver){
    dcprotocol = dc;
  }
  public void loop(){
    try{
            System.out.println("Connecting to Hub...");
            dcprotocol.connect();
            System.err.println("Connected!");
    }catch(Exception e){
            e.printStackTrace();
    }
    while(true){

    }
  }
}
