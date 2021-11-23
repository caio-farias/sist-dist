package backends.AuthBackend.resolvers;

import java.io.IOException;

import Server.ReceivedRequest.TCPReceivedRequest;
import Server.Response.TCPResponse;
import backends.AuthBackend.services.auth.AuthMessage;
import backends.AuthBackend.services.auth.Message;
import backends.AuthBackend.services.auth.User;
import backends.AuthBackend.services.auth.Repository.AuthRepositoryTCP;

public class TCPResolver {
  private final TCPReceivedRequest request;
  private final Character method;
  private final AuthRepositoryTCP authRepositoryTCP;
  
  public TCPResolver(TCPReceivedRequest request) throws IOException {
    this.request = request;
    authRepositoryTCP = new AuthRepositoryTCP("127.0.0.1", false);
    method = request.getMethod().charAt(0);
  }

  public void run(){
    handleRequest();
  }

  private void handleRequest(){
    switch (method) {
      case 'A':
        handleAuthMethod();
        return;
      case 'C':
        handleCreateMethod();
        return;
      case 'R':
        handleReadMethod();
        return;
      case 'U':
        handleUpdateMethod();
        return;
      case 'D':
        handleDeleteMethod();
        return;
      default:
        handleError();
        return;
    }
  }

  private void createResponse(String responseMessage){
    new TCPResponse(request, responseMessage).send();
  }

  private void handleCreateMethod(){
    try {
      String payload = request.getPayload();
      User user = authRepositoryTCP.createUser(payload);
      String responseMessage;    
  
      if(user == null)
        responseMessage = new Message(400 , "User already exists").toJSON();
      else
        responseMessage = user.toJSON();
  
      createResponse(responseMessage);
    } catch (Exception e) {

      createResponse(new Message(500 , "Server Error").toJSON());
    }
  }

  private void handleReadMethod(){
    try {
      String payload = request.getPayload();
      User user = authRepositoryTCP.findUserById(payload);
      String responseMessage; 
      
      if(user == null)
        responseMessage = new Message(400 , "User does not exists").toJSON();
      else
        responseMessage = user.toJSON();
  
      createResponse(responseMessage);
      
    } catch (Exception e) {

      createResponse(new Message(500 , "Server Error").toJSON());
    }
  }

  private void handleUpdateMethod(){
    try {
      String payload = request.getPayload();
      User user = authRepositoryTCP.updatePassword(payload);
      String responseMessage;
  
      if(user == null)
        responseMessage = new Message(400 , "User does not exists").toJSON();
      else
        responseMessage = user.toJSON();
  
      createResponse(responseMessage);
      
    } catch (Exception e) {

      createResponse(new Message(500 , "Server Error").toJSON());
    }
  }

  private void handleDeleteMethod(){
    try {
      String payload = request.getPayload();
      boolean isDeleted = authRepositoryTCP.deleteById(payload);
      String responseMessage; 
      
      if(!isDeleted)
        responseMessage = new Message(400 , "User does not exists.").toJSON();
      else
        responseMessage = new Message(200 , "User has been deleted").toJSON();
  
      createResponse(responseMessage);
      return;
      
    } catch (Exception e) {

      createResponse(new Message(500 , "Server Error").toJSON());
    }
  }

  private void handleAuthMethod(){
    try {
      String payload = request.getPayload();
      Integer userId = authRepositoryTCP.authenticate(payload);
      String responseMessage;
      
      if(userId == null)
        responseMessage = new Message(400 , "User does not exists").toJSON();
      else
        responseMessage = new AuthMessage(userId, "SIST-DIST").toJSON();
  
      createResponse(responseMessage);
      return;
    } catch (Exception e) {

      createResponse(new Message(500 , "Server Error").toJSON());
    }
  }

  private void handleError(){
    createResponse(new Message(404 , "Server Error").toJSON());
  }
}
