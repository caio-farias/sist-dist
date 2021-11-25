package databases.UserDB;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import databases.UserDB.handlers.TCPHandler;
import databases.UserDB.handlers.UDPHandler;
import databases.UserDB.services.users.UserRepository;


public class UserDB{
  private final UserRepository userRepository;
  private ExecutorService tcpExecutorService;
  private ExecutorService udpExecutorService;
  private int port;
  private Semaphore semaphore;
  private ServerSocket serverSocket;
  final int POOL_SIZE = 5;
  final boolean verbose = false;

  public UserDB(String port) throws IOException {
    semaphore = new Semaphore(1, true);
    this.port = Integer.parseInt(port);
    serverSocket = new ServerSocket(this.port);
    tcpExecutorService = Executors.newFixedThreadPool(POOL_SIZE);
    udpExecutorService = Executors.newFixedThreadPool(POOL_SIZE);
    userRepository = new UserRepository(semaphore);
  }

  public void start() throws IOException, InterruptedException{
    System.out.println(">> Server main thread is running...");
    udpExecutorService.submit(new UDPHandler(port, userRepository, verbose));
    tcpExecutorService.submit(new TCPHandler(serverSocket, userRepository, verbose));
  }
}
