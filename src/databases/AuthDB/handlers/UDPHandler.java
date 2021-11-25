package databases.AuthDB.handlers;

import java.io.IOException;
import java.net.DatagramSocket;

import Server.ReceivedRequest.UDPReceivedRequest;
import databases.AuthDB.resolvers.UDPResolver;
import databases.AuthDB.services.users.UserRepository;

public class UDPHandler implements Handler{
  private final DatagramSocket socket;
  private UDPReceivedRequest request;
  private final UserRepository userRepository;
  private final int port;
  private final boolean verbose;

  public UDPHandler(int port, UserRepository userRepository, boolean verbose) throws IOException {
    this.socket = new DatagramSocket(port);
    this.userRepository = userRepository;
    this.port = port;
    this.verbose = verbose;
    System.out.println(">> Listening with CRUD/UDP on port " + port);  
  }

  private void logger(){
    if(!verbose)
      return;
    System.out.println(">> Received CRUD/UDP on port " + port);
  }

  @Override
  public void run() {
    while(true){
      try {
          receiveMessage();
          logger();
          replyMessage();
      } catch (Exception e) { }
    }
  }
  
  @Override
  public void receiveMessage(){
    try {
      request =  new UDPReceivedRequest(socket);
    } catch (Exception e) {}
  }

  @Override
  public void replyMessage(){
    try {
      new UDPResolver(request, userRepository).run();
    } catch (IOException e) {}
  }  
}
