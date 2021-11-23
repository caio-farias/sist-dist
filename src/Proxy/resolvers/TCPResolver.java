package Proxy.resolvers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import Proxy.ReceivedRequest.TCPReceivedRequest;
import Proxy.Request.TCPRequest;
import Proxy.Response.TCPResponse;
import Proxy.models.ResourceBalancer;

public class TCPResolver {
  private final TCPReceivedRequest request;
  private final String path;
  private final String payload;
  private final ResourceBalancer authBalancer; 
  private final ResourceBalancer userBalancer;
  
  
  public TCPResolver(
    TCPReceivedRequest request,
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
      try {
        tryUseResource(
          newPayload, 
          "127.0.0.1", 
          authBalancer.getPort()
        );
      } catch (SocketException | UnknownHostException e1) {
        e1.printStackTrace();
      }
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

      try {
        tryUseResource(
          newPayload, 
          "127.0.0.1", 
          userBalancer.getPort()
        );
      } catch (SocketException | UnknownHostException e1) {
        e1.printStackTrace();
      }
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
