package backends.AuthBackend.resolvers;

import java.io.IOException;

import Server.ReceivedRequest.TCPReceivedRequest;
import Server.Response.HTTPResponse;
import backends.AuthBackend.services.auth.AuthMessage;
import backends.AuthBackend.services.auth.Message;
import backends.AuthBackend.services.auth.User;
import backends.AuthBackend.services.auth.Repository.AuthRepositoryTCP;

public class HTTPResolver {
  private final TCPReceivedRequest request;
  private final AuthRepositoryTCP authRepository;
  private final String httpMethod;
  private final String resource;
  
  public HTTPResolver(TCPReceivedRequest request) throws IOException{
    this.request = request;
    this.authRepository = new AuthRepositoryTCP("127.0.0.1", true);
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
      String payload = request.getPayload();
      User user = authRepository.findUserById(payload);
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
      String payload = request.getPayload();
      User user = authRepository.createUser(payload);
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
    String payload = request.getPayload();
    User user = authRepository.updatePassword(payload);
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
      String payload = request.getPayload();
      boolean isDeleted = authRepository.deleteById(payload);
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

  private void handleError(){
    new HTTPResponse(request, 404).send();
  }

  private void authenticate(){
    try {
      String payload = request.getPayload();
      Integer userId = authRepository.authenticate(payload);
      String responseMessage;

      if(userId == null){
        responseMessage = new Message("User does not exists or invalid password", "").toJSON();
        new HTTPResponse(request, responseMessage, 401).send();
        return;
      }
      
      responseMessage = new AuthMessage(userId, "SIST-DIST").toJSON();
      new HTTPResponse(request, responseMessage, 200).send();
    } catch (Exception e) {
      new HTTPResponse(request, 400).send();
    }
  }
}
