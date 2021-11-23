package Proxy.models;

public class ResourceBalancer {
  private int count = 0;
  private int backupPort;
  private final int[] ports;

  public ResourceBalancer(int[] ports) {
		this.backupPort = ports[1];
		this.ports = ports;
	}

  public int getPort(){
    count += 1;
    if(count > 10000)
      count = 0;
      
    if(count % 2 == 0){
      backupPort = ports[1];
      return ports[0];
    }
    backupPort = ports[0];
    return ports[1];
  }

	public int[] getPorts() {
		return ports;
	}

	public int getBackupPort() {
		return backupPort;
	}

	public int getCount() {
		return count;
	}
}
