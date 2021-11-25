package Proxy.LoadBalancer;

public class Resource {
  private int id;
  private String ip;
  private int port;
  private boolean isAvalailable = false;

  public Resource() {}

  public Resource(
    int id,
    String ip,
    int port
    ){
    this.id = id;
    this.ip = ip;
    this.port = port;
  }

  public boolean getIsAvalailable(){
    return this.isAvalailable;
  }

  public void setIsAvalailable(boolean isAvalailable) {
    this.isAvalailable = isAvalailable;
  }
  
  public Integer getId() {
    return id;
  }

  public int getPort() {
    return port;
  }
  
  public String getIp() {
    return ip;
  }
}
