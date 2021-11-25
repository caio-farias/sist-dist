package Proxy.resolvers;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Proxy.Request.UDPRequest;
import Proxy.Response.UDPResponse;
import Proxy.LoadBalancer.LoadBalancer;
import Proxy.LoadBalancer.Resource;
import Proxy.LoadBalancer.ResourceBalancer;
import Proxy.ReceivedRequest.UDPReceivedRequest;

public class UDPResolver {
  private final UDPReceivedRequest request;
  private final String payload;
  private String path;
  private ResourceBalancer resourceBalancer;
  private final boolean applyRedundancy;

  public UDPResolver(
    UDPReceivedRequest request,
    LoadBalancer loadBalancer
    ){
    this.request = request;
    path = request.getPath();
    payload = request.getPayload();
    applyRedundancy = loadBalancer.isRedundant();
    resourceBalancer = loadBalancer.getResourceBalancerByName(path);
  }
  
  public void run(){
    try {
      initRequest();
    } catch (SocketException | UnknownHostException e) {
      e.printStackTrace();
    }
  }

  private void initRequest() throws SocketException, UnknownHostException{
    String newPayload = payload;
    if(applyRedundancy){
      newPayload = payload.replaceFirst("/" + path, "");
      ArrayList<Resource> rList =  resourceBalancer.getAvailableResources();
      for(Resource r: rList){
        tryUseResource(
          newPayload, 
          r.getIp(),
          r.getPort()
        );
      }
      return;
    }else{
      newPayload = payload.replaceFirst("/" + path, "");
      Resource resource = resourceBalancer.getResource();
      if(resourceBalancer.getResource() == null)
        return;
  
      tryUseResource(
        newPayload, 
        resource.getIp(),
        resource.getPort()
      );
    }
  }

  private void tryUseResource(String newPayload, String url, int port) 
    throws SocketException, UnknownHostException 
    {
    UDPRequest proxyRequest;
    proxyRequest =	new UDPRequest(
      newPayload, 
      InetAddress.getByName(url), 
      port
    );

    UDPResponse proxyResponse = awaitResourceResponse(proxyRequest);
    proxyResponse.send();
  }

  private UDPResponse awaitResourceResponse(UDPRequest proxyRequest){
    try {
      String response = proxyRequest.sendAndAwaitResponse();
      return new UDPResponse(
        request.getSocket(), 
        response, 
        request.getPacket().getAddress(),
        request.getPacket().getPort()
      );
    } catch (Exception e) {}
    return null;
  }
}
