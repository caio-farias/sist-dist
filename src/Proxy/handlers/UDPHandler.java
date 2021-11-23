package Proxy.handlers;

import java.io.IOException;
import java.net.DatagramSocket;

import Proxy.ReceivedRequest.UDPReceivedRequest;
import Proxy.models.ResourceBalancer;
import Proxy.resolvers.UDPResolver;

public class UDPHandler implements Handler{
  private final DatagramSocket socket;
  private UDPReceivedRequest request;
  private ResourceBalancer userBalancer;
  private ResourceBalancer authBalancer;

  public UDPHandler(int port) throws IOException {
    this.socket = new DatagramSocket(port);
    userBalancer = new ResourceBalancer(new int[] {8090, 8092});
    authBalancer = new ResourceBalancer(new int[] {8080, 8082});
    System.out.println(">> Listening with CRUD/UDP on port " + port);  
  }

  @Override
  public void run() {
    try {
      while(true){
        receiveMessage();
        replyMessage();
      }
    } catch (Exception e) {
      System.err.println("Cannot open the port on UDP");

    }finally{
      System.out.println("Closing UDP server");
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
      new UDPResolver(request, authBalancer, userBalancer).run();
    } catch (Exception e) {

    }
  }  
}
