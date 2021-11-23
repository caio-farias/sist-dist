package backends.AuthBackend.services.auth.Repository;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

import Server.Request.UDPRequest;
import backends.AuthBackend.services.auth.AuthMessage;
import backends.AuthBackend.services.auth.Message;
import backends.AuthBackend.services.auth.User;

public class AuthRepositoryUDP {
  private final InetAddress address;
  private final int mainPort = 8081;
  private final int backupPort = 8083;

  public AuthRepositoryUDP(String url) throws UnknownHostException {
    address = InetAddress.getByName(url);
  }

  private String useDB(String requestPayload){
    UDPRequest request;
    String body;
    try {
      request = new UDPRequest(requestPayload, address, mainPort);
      body = request.sendAndAwaitResponse();
      if(body == null){
        request = new UDPRequest(requestPayload, address, backupPort);
         body = request.sendAndAwaitResponse();
      }
      return body;
    } catch (SocketException e) {

    }
    return null;
  }

  public User createUser(String requestPayload){
    try {
      String userBody = useDB(requestPayload);
      User user = new User(userBody);
      if(user.isEmpty())
        return null;
      return user;
    } catch (Exception e) {

    }
    return null;
  }
  
  public User findUserById(String requestPayload){
    try {
      String userBody = useDB(requestPayload);
      User user = new User(userBody);
      if(user.isEmpty())
        return null;
      return user;
    } catch (Exception e) {

    }
    return null;
  }

  public User updatePassword(String requestPayload){
    try {
      String userBody = useDB(requestPayload);
      User user = new User(userBody);
      if(user.isEmpty())
        return null;
      return user;
    } catch (Exception e) {

    }
    return null;
  }

  public boolean deleteById(String requestPayload){
    try {
      String messageBody = useDB(requestPayload);
      Message response = new Message(messageBody);
      if(response.getStatus() != 200)
        return false;

      return true;
    } catch (Exception e) {

    }
    return false;
  }

  public Integer authenticate(String requestPayload){
    try {
      String userBody = useDB(requestPayload);
      AuthMessage response = new AuthMessage(userBody);
      if(response.getUserId() == null)
        return null;
      return response.getUserId();
    } catch (Exception e) {

    }
    return null;
  }
}
