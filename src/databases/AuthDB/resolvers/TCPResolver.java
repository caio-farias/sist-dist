package databases.AuthDB.resolvers;

import java.io.IOException;

import Server.ReceivedRequest.TCPReceivedRequest;
import Server.Response.TCPResponse;
import databases.AuthDB.services.auth.Auth;
import databases.AuthDB.services.users.Message;
import databases.AuthDB.services.users.User;
import databases.AuthDB.services.users.UserRepository;

public class TCPResolver {
  private final TCPReceivedRequest request;
  private final Character method;
  private final UserRepository userRepository;
  
  public TCPResolver(TCPReceivedRequest request, UserRepository userRepository) throws IOException {
    this.request = request;
    this.userRepository = userRepository;
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
      String body = request.getBody();
      User user = userRepository.create(new User(body));
      String responseMessage;    

      if(user == null)
        responseMessage = new Message(400 , "User already exists").toJSON();
      else
        responseMessage = user.toJSON();

      createResponse(responseMessage);
      return;
    } catch (Exception e) {

      createResponse(new Message(500 , "Server Error").toJSON());
    }
  }

  private void handleReadMethod(){
    try {
      String body = request.getBody();
      User user = userRepository.findById(new User(body).getId());
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
      
      String body = request.getBody();
      User bodyUser = new User(body);
      User user = userRepository.updatePassword(
        bodyUser.getId(),
        bodyUser.getPassword()
      );
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
      String body = request.getBody();
      Integer userId = new User(body).getId();
      boolean isDeleted = userRepository.deleteById(userId);
      String responseMessage; 
      
      if(!isDeleted)
        responseMessage = new Message(400 , "User does not exists.").toJSON();
      else
        responseMessage = new Message(200 , "User has been deleted").toJSON();
  
      createResponse(responseMessage);
    } catch (Exception e) {

      createResponse(new Message(500 , "Server Error").toJSON());
    }
  }

  private void handleAuthMethod(){
    try {
      
      String body = request.getBody();
      User bodyUser = new User(body);
      Integer userId = userRepository.authenticate(
        bodyUser.getUsername(),
        bodyUser.getPassword()
      );
      String responseMessage;
      
      if(userId == null)
        responseMessage = new Message(400 , "User does not exists").toJSON();
      else
        responseMessage = new Auth(userId, "SIST-DIST").toJSON();
  
      createResponse(responseMessage);
    } catch (Exception e) {

      createResponse(new Message(500 , "Server Error").toJSON());
    }
  }

  private void handleError(){
    createResponse(new Message(404 , "Server Error").toJSON());
  }
}
