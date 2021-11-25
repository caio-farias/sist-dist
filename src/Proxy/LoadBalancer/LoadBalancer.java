package Proxy.LoadBalancer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LoadBalancer {
  private final ArrayList<ResourceBalancer> resourceBalancers;;
  private ExecutorService service;
  private boolean isRedundant = false;
  
  public LoadBalancer(ArrayList<ResourceBalancer> resourceBalancers) throws IOException {
    this.resourceBalancers = resourceBalancers;
    service = Executors.newFixedThreadPool(1);
  }
  
  public boolean isRedundant() {
    return isRedundant;
  }

  public void setRedundant(boolean isRedundant) {
    this.isRedundant = isRedundant;
  }

  public void run(){
    service.submit(new ResourcesListener(resourceBalancers));
  }

  public ResourceBalancer getResourceBalancerByName(String name){
    for (int index = 0; index < resourceBalancers.size(); index++) {
      if(resourceBalancers.
        get(index).
        getName().
        equals(name)){
          return resourceBalancers.get(index);
      }
    }
    return null;
  }
}