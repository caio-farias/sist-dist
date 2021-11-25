package Proxy.LoadBalancer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class ResourcesListener implements Runnable {
  private final ArrayList<ResourceBalancer> resourceBalancers;
  public ResourcesListener(ArrayList<ResourceBalancer> resourceBalancers){
    this.resourceBalancers = resourceBalancers;
  }

  @Override
  public void run() {
    while(true){
      try {
        ping();
      } catch (IOException e) {}    
    } 
  }

  public void ping() throws IOException{
    for(ResourceBalancer r : resourceBalancers){
      for(Resource resource : r.getResources()){
        resource.setIsAvalailable(tryConnectionUDP(resource, 500));
      }
    }
  }

  
  public static boolean tryConnectionUDP(Resource resource, int timeout) throws SocketException{
    DatagramSocket socket = new DatagramSocket();
    socket.setSoTimeout(timeout);
    byte[] bytes = new byte[socket.getReceiveBufferSize()];
    DatagramPacket p1 = new DatagramPacket(bytes, bytes.length);

    try {
      DatagramPacket p = new DatagramPacket(
        "alive?".getBytes(), 
        "alive?".length(), 
        InetAddress.getByName(resource.getIp()),
        resource.getPort()
      ); 
      socket.send(p);
      socket.receive(p1);
      socket.close();
      return true;
    } catch (IOException e) {
      socket.close();
    }
    return false;
  }
  
  public static boolean tryConnection(Resource resource, int timeout) throws IOException{
    Socket socket = new Socket();
    long init = System.currentTimeMillis();
    long after;
    try {
      socket.connect(new InetSocketAddress(resource.getIp(), resource.getPort()), timeout);
      socket.close();
      return true;
    } catch (IOException e) {
      socket.close();
      after = System.currentTimeMillis();
    }
    return after - init >= timeout;
  }
}
