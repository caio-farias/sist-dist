package Proxy;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Proxy.handlers.TCPHandler;
import Proxy.handlers.UDPHandler;


public class Proxy{
  private ExecutorService tcpExecutorService;
  private ExecutorService udpExecutorService;
  private int port;
  private ServerSocket serverSocket;
  final int POOL_SIZE = 40;

  public Proxy(String port) throws IOException {
    this.port = Integer.parseInt(port);
    serverSocket = new ServerSocket(this.port);
    tcpExecutorService = Executors.newFixedThreadPool(POOL_SIZE);
    udpExecutorService = Executors.newFixedThreadPool(POOL_SIZE);

  }

  public void start() throws IOException, InterruptedException{
    System.out.println(">> Server main thread is running...");
    udpExecutorService.submit(new UDPHandler(port));
    tcpExecutorService.submit(new TCPHandler(serverSocket));
  }

  public static void main(String[] args) throws IOException, InterruptedException{
    new Proxy("8000").start();
  }
}
