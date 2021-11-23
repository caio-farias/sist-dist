package Proxy.resolvers;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import Proxy.Request.UDPRequest;
import Proxy.Response.UDPResponse;
import Proxy.models.ResourceBalancer;
import Proxy.ReceivedRequest.UDPReceivedRequest;

public class UDPResolver {
  private final UDPReceivedRequest request;
  private String path;
  private final String payload;
  private ResourceBalancer authBalancer; 
  private ResourceBalancer userBalancer;
  
  public UDPResolver(
    UDPReceivedRequest request,
    ResourceBalancer authBalancer, 
    ResourceBalancer userBalancer
    ){
    this.request = request;
    path = request.getPath();
    payload = request.getPayload();
    this.authBalancer = authBalancer;
    this.userBalancer = userBalancer;
  }
  
  public void run(){
    checkPathAndTryResource();
  }

  private void checkPathAndTryResource(){
    if(path.equals("users")){
      checkUsersService();
      return;
    }else if(path.equals("auth")){
      checkAuthService();
      return;
    }
  }

  private void checkAuthService(){
    String newPayload = payload.replace(" " + path, "");
    try {
		  tryUseResource(
        newPayload, 
        "127.0.0.1",
        authBalancer.getPort()
      );
		} catch (SocketException | UnknownHostException e) {

		}
  }

  private void checkUsersService(){
    String newPayload = payload.replace(" " + path, "");  
    try {
      tryUseResource(
        newPayload, 
        "127.0.0.1", 
        userBalancer.getPort()
      );
		} catch (SocketException | UnknownHostException e) {
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
    } catch (Exception e) {

    }
    return null;
  }
}
