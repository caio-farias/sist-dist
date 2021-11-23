package Proxy.resolvers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import Proxy.ReceivedRequest.TCPReceivedRequest;
import Proxy.Request.TCPRequest;
import Proxy.Response.HTTPResponse;
import Proxy.models.ResourceBalancer;

public class HTTPResolver {
  private final TCPReceivedRequest request;
  private final String path;
  private final String payload;
  private final ResourceBalancer authBalancer; 
  private final ResourceBalancer userBalancer;
  
  public HTTPResolver(
    TCPReceivedRequest request,
    ResourceBalancer authBalancer, 
    ResourceBalancer userBalancer
    ) {

    this.request = request;
    String[] arr = request.getPath().split("/");
    path = arr[1];
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
    String newPayload = payload.replaceFirst("/" + path, "");
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
			  true
			);
      HTTPResponse proxyResponse = awaitResourceResponse(proxyRequest);
      proxyResponse.send();
		} catch (IOException e) {
		}
  }

  private HTTPResponse awaitResourceResponse(TCPRequest proxyRequest){
    try {
      String response = proxyRequest.sendAndAwaitResponse();
      int status = Integer.valueOf(proxyRequest.getResponseHeader().split(" ")[1]);
      return new HTTPResponse(
        request, 
        response,
        status
      );
    } catch (Exception e) {

    }
    return null;
  }

}