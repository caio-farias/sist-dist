package backends.UserBackend.handlers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import Server.ReceivedRequest.TCPReceivedRequest;
import backends.UserBackend.resolvers.HTTPResolver;
import backends.UserBackend.resolvers.TCPResolver;

public class TCPHandler implements Handler{
  private final ServerSocket serverSocket;
  private Socket socket;
  private TCPReceivedRequest request;
  private final boolean verbose;

  public TCPHandler(ServerSocket serverSocket, boolean verbose) throws IOException{ 
    this.serverSocket = serverSocket;
    this.verbose = verbose;
  }
 
  private void logger(){
    if(!verbose)
      return;
    System.out.println(">> Received CRUD/TCP and HTTP on port " 
        + serverSocket.getLocalPort());
  }

  @Override
  public void run() {
    try {
      System.out.println(">> Listening with CRUD/TCP and HTTP on port " 
        + serverSocket.getLocalPort());
      while(true){
        socket = serverSocket.accept();
        receiveMessage();
        logger();
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
        new HTTPResolver(request).run();
        return;
      }
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
