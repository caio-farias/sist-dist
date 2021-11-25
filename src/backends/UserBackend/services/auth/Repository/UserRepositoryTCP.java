package backends.UserBackend.services.auth.Repository;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import Server.Request.TCPRequest;
import backends.UserBackend.services.auth.User;
import databases.UserDB.services.users.Message;

public class UserRepositoryTCP {
  private final InetAddress address;
  private final boolean useHTTP;
  private final int mainPort = 8091;
  
  public UserRepositoryTCP(String url, boolean useHTTP) throws UnknownHostException{
    address = InetAddress.getByName(url);
    this.useHTTP = useHTTP;
  }

  private String useDB(String requestPayload) throws IOException {
    TCPRequest request;
    String body;
    try {
      request = new TCPRequest(requestPayload, address, mainPort, useHTTP);
      body = request.sendAndAwaitResponse();
      return body;
    } catch (Exception e) {

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
      TCPRequest request = new TCPRequest(requestPayload, address, 8091, useHTTP);
      request.sendAndAwaitResponse();
      int status;
      try {
        status = Integer.valueOf(request.getResponseHeader().split(" ")[1]);
      } catch (Exception e) {
        status = new Message(request.getResponseBody()).getStatus();
      }
      if(status != 200)
        return false;
      return true;
    } catch (Exception e) {

    }
    return false;
  }
}
