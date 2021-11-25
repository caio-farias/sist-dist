package Proxy.handlers;

import java.io.IOException;
import java.net.DatagramSocket;

import Proxy.LoadBalancer.LoadBalancer;
import Proxy.ReceivedRequest.UDPReceivedRequest;
import Proxy.resolvers.UDPResolver;

public class UDPHandler implements Handler{
  private final DatagramSocket socket;
  private UDPReceivedRequest request;
  private final LoadBalancer loadBalancer;
  
  public UDPHandler(int port, LoadBalancer loadBalancer) throws IOException {
    this.socket = new DatagramSocket(port);
    this.loadBalancer = loadBalancer;
    System.out.println(">> Listening with CRUD/UDP on port " + port);  
  }

   @Override
  public void run() {
    while(true){
      try {
          receiveMessage();
          replyMessage();
      } catch (Exception e) { }
    }
  }
  
  @Override
  public void receiveMessage(){
    try {
      request =  new UDPReceivedRequest(socket);
    } catch (Exception e) {
      // System.out.println("Error on reading message with UDP..");

    }
  }

  @Override
  public void replyMessage(){
    try {
      new UDPResolver(request, loadBalancer).run();
    } catch (Exception e) {

    }
  }  
}
