package Proxy.resolvers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

import Proxy.LoadBalancer.LoadBalancer;
import Proxy.LoadBalancer.Resource;
import Proxy.LoadBalancer.ResourceBalancer;
import Proxy.ReceivedRequest.TCPReceivedRequest;
import Proxy.Request.TCPRequest;
import Proxy.Response.TCPResponse;

public class TCPResolver {
  private final TCPReceivedRequest request;
  private final String path;
  private final String payload;
  private final ResourceBalancer resourceBalancer;
  private final boolean applyRedundancy;
  public TCPResolver(
    TCPReceivedRequest request,
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
    TCPRequest proxyRequest;
    try {
			proxyRequest =	new TCPRequest(
			  newPayload, 
			  InetAddress.getByName(url), 
			  port,
			  false
			);
      TCPResponse proxyResponse = awaitResourceResponse(proxyRequest);
      proxyResponse.send();
		} catch (IOException e) {
			e.printStackTrace();
		}
  }

  private TCPResponse awaitResourceResponse(TCPRequest proxyRequest){
    try {
      String response = proxyRequest.sendAndAwaitResponse();
      return new TCPResponse(
        request, 
        response
      );
    } catch (Exception e) {

    }
    return null;
  }

}
