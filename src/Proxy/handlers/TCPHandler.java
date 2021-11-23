package Proxy.handlers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Proxy.ReceivedRequest.TCPReceivedRequest;
import Proxy.models.ResourceBalancer;
import Proxy.resolvers.HTTPResolver;
import Proxy.resolvers.TCPResolver;

public class TCPHandler implements Handler{
  private final ServerSocket serverSocket;
  private Socket socket;
  private TCPReceivedRequest request;
  private ResourceBalancer userBalancer;
  private ResourceBalancer authBalancer;


  public TCPHandler(ServerSocket serverSocket) throws IOException{ 
    this.serverSocket = serverSocket;
    userBalancer = new ResourceBalancer(new int[] {8090, 8092});
    authBalancer = new ResourceBalancer(new int[] {8080, 8082});
  }

  @Override
  public void run() {
    try {
      System.out.println(">> Listening with CRUD/TCP and HTTP on port " 
        + serverSocket.getLocalPort());
      while(true){
        socket = serverSocket.accept();
        receiveMessage();
        replyMessage();
      }
    } catch (Exception e) {
      System.out.println("Server crashed..");
    }finally {
      terminateHandler();
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
        new HTTPResolver(request, authBalancer, userBalancer).run();
        return;
      }
      new TCPResolver(request, authBalancer, userBalancer).run();
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
