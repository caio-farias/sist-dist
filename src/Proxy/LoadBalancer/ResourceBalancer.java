package Proxy.LoadBalancer;

import java.util.ArrayList;

public class ResourceBalancer {
  private ArrayList<Resource> resources;
  private String name;
  private Resource lastResource;

  public ResourceBalancer(){}

  public ResourceBalancer(String name, ArrayList<Resource> resources) {
    this.resources = resources;
    this.name = name;
	}

  public ArrayList<Resource> getAvailableResources(){
    ArrayList<Resource> output = new ArrayList<>();
    for(Resource r : resources){
      if(r.getIsAvalailable())
        output.add(r);
    }
    return output;
  }

  public String getName(){
    return name;
  }

  public ArrayList<Resource> getResources() {
    return resources;
  }

  public Resource getResource(){
    ArrayList<Resource> availableList = getAvailableResources();
    int size = availableList.size();
    
    if(size < 1)
      return null;
      
    if(size > 1
      && availableList.get(0).equals(lastResource)
    ){
      lastResource = availableList.get(1);
      return lastResource;
    }

    lastResource = availableList.get(0);
    return lastResource;
  }
}
