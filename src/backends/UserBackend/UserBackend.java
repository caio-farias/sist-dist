package backends.UserBackend;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import backends.UserBackend.handlers.TCPHandler;
import backends.UserBackend.handlers.UDPHandler;

public class UserBackend{
  private ExecutorService tcpExecutorService;
  private ExecutorService udpExecutorService;
  private int port;
  private ServerSocket serverSocket;
  final int POOL_SIZE = 5;
  private final boolean verbose = true;

  public UserBackend(String port) throws IOException {
    this.port = Integer.parseInt(port);
    serverSocket = new ServerSocket(this.port);
    tcpExecutorService = Executors.newFixedThreadPool(POOL_SIZE);
    udpExecutorService = Executors.newFixedThreadPool(POOL_SIZE);
  }

  public void start() throws IOException, InterruptedException{
    System.out.println(">> Server main thread is running...");
    udpExecutorService.submit(new UDPHandler(port, verbose));
    tcpExecutorService.submit(new TCPHandler(serverSocket, verbose));
  }
}
