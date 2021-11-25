package Proxy.handlers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Proxy.LoadBalancer.LoadBalancer;
import Proxy.ReceivedRequest.TCPReceivedRequest;
import Proxy.resolvers.HTTPResolver;
import Proxy.resolvers.TCPResolver;

public class TCPHandler implements Handler{
  private final ServerSocket serverSocket;
  private Socket socket;
  private TCPReceivedRequest request;
  private final LoadBalancer loadBalancer;


  public TCPHandler(ServerSocket serverSocket, LoadBalancer loadBalancer) throws IOException{ 
    this.serverSocket = serverSocket;
    this.loadBalancer = loadBalancer;
    System.out.println(">> Listening with CRUD/TCP and HTTP on port " 
      + serverSocket.getLocalPort());
  }

  @Override
  public void run() {
    while(true){
      try {        
        socket = serverSocket.accept();
        receiveMessage();
        replyMessage();
      } catch (Exception e) {
        e.printStackTrace();
        System.out.println("Socket error..");
      }
    }
  }

  @Override
  public void receiveMessage() {
    try {
      request  = new TCPReceivedRequest(socket);
    } catch (Exception e) {
      // System.out.println("Error on reading request..");
    }    
  }
  
  @Override
  public void replyMessage() {
    try {
      if(request.isHTTP()){
        new HTTPResolver(request, loadBalancer).run();
        return;
      }
      new TCPResolver(request, loadBalancer).run();
    } catch (Exception e) {
      // System.out.println("Error on replying request..");
    }
  }

  private void terminateHandler(){
    try {
      if (socket != null)
        socket.close();
      System.err.println(socket.toString() + " ~> closing");
    } catch (IOException e) {
      System.out.println("Error when closing server..");
    }
  }
}
