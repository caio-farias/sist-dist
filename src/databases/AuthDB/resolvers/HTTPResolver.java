package databases.AuthDB.resolvers;

import java.io.IOException;

import Server.ReceivedRequest.TCPReceivedRequest;
import Server.Response.HTTPResponse;
import databases.AuthDB.services.auth.Auth;
import databases.AuthDB.services.users.Message;
import databases.AuthDB.services.users.User;
import databases.AuthDB.services.users.UserRepository;

public class HTTPResolver {
  private final TCPReceivedRequest request;
  private final UserRepository userRepository;
  private final String httpMethod;
  private final String resource;
  
  public HTTPResolver(TCPReceivedRequest request, UserRepository userRepository) throws IOException{
    this.request = request;
    this.userRepository = userRepository;
    httpMethod = request.getMethod();
    resource = request.getPath().split("/")[1];
  }

  public void run(){
    handleRequest();
  }

  private boolean checkResource(){
    if (resource.equals("auth") 
      && httpMethod.equals("POST")){
      authenticate();
      return false;
    }
    if(!resource.equals("users")){
      handleError();
      return false;
    }
    return true;
  }
  
  private void handleRequest(){
    if(!checkResource())
      return;

    switch (httpMethod) {
      case "GET":
        handleGetMethod();
        return;
      case "POST":
        handlePostMethod();
        return;
      case "PUT":
        handlePutMethod();
        return;
      case "DELETE":
        handleDeleteMethod();
        return;
      default:
        handleError();
        return;
    }
  }

  private void handleGetMethod() {
    try {
      Integer id = Integer.parseInt(request.getPath().split("/")[2]);
      User user = userRepository.findById(id);
      String responseMessage;  

      if(user == null){
        responseMessage = new Message("User does not exists", "").toJSON();
        new HTTPResponse(request, responseMessage, 202).send();
        return;
      }
      
      responseMessage = user.toJSON();  
      new HTTPResponse(request, responseMessage, 200).send();
    } catch (Exception e) {
      new HTTPResponse(request, 400).send();
    }
  }

  private void handlePostMethod(){
    try {
      String body = request.getBody();
      User user = userRepository.create(new User(body));
      String responseMessage;

      if(user == null){
        responseMessage = new Message("User already exists", "").toJSON();
        new HTTPResponse(request, responseMessage, 202).send();
        return;
      }
      
      responseMessage = user.toJSON();
      new HTTPResponse(request, responseMessage, 201).send();
    } catch (Exception e) {

      new HTTPResponse(request, 400).send();
    }
  }

  private void handlePutMethod(){
    try {
      Integer id = Integer.parseInt(request.getPath().split("/")[2]);
      String body = request.getBody();
      User bodyUser = new User(body);
      User user = userRepository.updatePassword(
        id,
        bodyUser.getPassword()
      );
      String responseMessage;

    if(user == null){
      responseMessage = new Message("User does not exists", "").toJSON();
      new HTTPResponse(request, responseMessage, 202).send();
      return;
    }

      responseMessage = user.toJSON();
      new HTTPResponse(request, responseMessage, 201).send();
    } catch (Exception e) {
      new HTTPResponse(request, 400).send();
    }
  }

  private void handleDeleteMethod(){
    try {
      Integer id = Integer.parseInt(request.getPath().split("/")[2]);
      boolean isDeleted = userRepository.deleteById(id);
      String responseMessage; 

      if(!isDeleted){
        responseMessage = new Message("User does not exists.", "").toJSON();
        new HTTPResponse(request, responseMessage, 202).send();
        return;
      }
      
      responseMessage = new Message("User has been deleted", "").toJSON();
      new HTTPResponse(request, responseMessage, 200).send();
    } catch (Exception e) {
      new HTTPResponse(request, 400).send();
    }
  }

  private void authenticate(){
    try {
      String body = request.getBody();
      User bodyUser = new User(body);
      Integer userId = userRepository.authenticate(
        bodyUser.getUsername(),
        bodyUser.getPassword()
      );
      String responseMessage;

      if(userId == null){
        responseMessage = new Message("User does not exists or invalid password").toJSON();
        new HTTPResponse(request, responseMessage, 401).send();
        return;
      }
      
      responseMessage = new Auth(userId, "SIST-DIST").toJSON();
      new HTTPResponse(request, responseMessage, 200).send();
    } catch (Exception e) {
      new HTTPResponse(request, 400).send();
    }
  }

    private void handleError(){
    new HTTPResponse(request, 404).send();
  }
}
