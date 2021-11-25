package Proxy;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Proxy.LoadBalancer.LoadBalancer;
import Proxy.LoadBalancer.Resource;
import Proxy.LoadBalancer.ResourceBalancer;
import Proxy.handlers.TCPHandler;
import Proxy.handlers.UDPHandler;
public class Proxy{
  private ExecutorService tcpExecutorService;
  private ExecutorService udpExecutorService;
  private int port;
  private ServerSocket serverSocket;
  final int POOL_SIZE = 5;
  private final LoadBalancer loadBalancer;

  public Proxy(
    String port, 
    ArrayList<ResourceBalancer> resourceBalancers, 
    boolean applyRedundancy
    ) throws IOException {
    this.port = Integer.parseInt(port);
    serverSocket = new ServerSocket(this.port);

    loadBalancer = new LoadBalancer(resourceBalancers);
    loadBalancer.run();
    loadBalancer.setRedundant(applyRedundancy);
    tcpExecutorService = Executors.newFixedThreadPool(POOL_SIZE);
    udpExecutorService = Executors.newFixedThreadPool(POOL_SIZE);
  }

  public void start() throws IOException, InterruptedException{
    System.out.println(">> Server main thread is running...");
    udpExecutorService.submit(
      new UDPHandler(
        port,
        loadBalancer
      ));
    tcpExecutorService.submit(
      new TCPHandler(
        serverSocket, 
        loadBalancer
    ));
  }

  public static void main(String[] args) throws IOException, InterruptedException{
    ArrayList<ResourceBalancer> backendResourceBalancers = new ArrayList<ResourceBalancer>();

    ArrayList<Resource> userBackendResources = new ArrayList<Resource>();
    userBackendResources.add(new Resource(1, "127.0.0.1", 8080));
    userBackendResources.add(new Resource(2, "127.0.0.1", 8082));
    backendResourceBalancers.add(new ResourceBalancer("auth", userBackendResources));

    ArrayList<Resource> authBackendReousrces = new ArrayList<Resource>();
    authBackendReousrces.add(new Resource(1, "127.0.0.1", 8090));
    authBackendReousrces.add(new Resource(2, "127.0.0.1", 8092));
    backendResourceBalancers.add(new ResourceBalancer("users", authBackendReousrces));
    
    ArrayList<ResourceBalancer> dbResourceBalancers = new ArrayList<ResourceBalancer>();
    ArrayList<Resource> dbUserResources = new ArrayList<Resource>();
    dbUserResources.add(new Resource(1, "127.0.0.1", 8081));
    dbUserResources.add(new Resource(1, "127.0.0.1", 8083));
    dbResourceBalancers.add(new ResourceBalancer("auth", dbUserResources));

    ArrayList<Resource> dbAuthResources = new ArrayList<Resource>();
    dbAuthResources.add(new Resource(1, "127.0.0.1", 8091));
    dbAuthResources.add(new Resource(1, "127.0.0.1", 8093));
    dbResourceBalancers.add(new ResourceBalancer("users", dbAuthResources));
  

    new Proxy("8000", backendResourceBalancers, false).start();
    // new Proxy("8001", dbResourceBalancers, true).start();
  }
}
