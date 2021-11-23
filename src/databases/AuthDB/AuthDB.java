package databases.AuthDB;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import databases.AuthDB.handlers.TCPHandler;
import databases.AuthDB.handlers.UDPHandler;
import databases.AuthDB.services.users.UserRepository;


public class AuthDB{
  private final UserRepository userRepository;
  private ExecutorService tcpExecutorService;
  private ExecutorService udpExecutorService;
  private int port;
  private Semaphore semaphore;
  private ServerSocket serverSocket;
  final int POOL_SIZE = 40;
  private final boolean verbose = false;

  public AuthDB(String port) throws IOException {
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

  public static void main(String[] args) throws IOException, InterruptedException {
    new AuthDB("8081").start();
  }
}
