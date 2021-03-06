package backends.AuthBackend.handlers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Server.ReceivedRequest.TCPReceivedRequest;
import backends.AuthBackend.resolvers.HTTPResolver;
import backends.AuthBackend.resolvers.TCPResolver;

public class TCPHandler implements Handler{
  private final ServerSocket serverSocket;
  private Socket socket;
  private TCPReceivedRequest request;
  private final boolean verbose;

  public TCPHandler(ServerSocket serverSocket, boolean verbose) throws IOException{ 
    this.serverSocket = serverSocket;
    this.verbose = verbose;
    System.out.println(">> Listening with CRUD/TCP and HTTP on port " 
          + serverSocket.getLocalPort());
  }
  
  private void logger(){
    if(!verbose)
      return;
    System.out.println(">> Received CRUD/TCP and HTTP on port " 
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
        System.out.println("Socket error..");
      }
    }
  }

  @Override
  public void receiveMessage() {
    try {
      request  = new TCPReceivedRequest(socket);
    } catch (Exception e) {
      System.out.println("Error on reading request TCP..");
    }    
  }
  
  @Override
  public void replyMessage() {
    try {
      if(request.isHTTP()){
        new HTTPResolver(request).run();
        return;
      }
      logger();
      new TCPResolver(request).run();
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
