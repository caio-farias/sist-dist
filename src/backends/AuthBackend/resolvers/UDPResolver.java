package backends.AuthBackend.resolvers;

import java.io.IOException;
import java.net.UnknownHostException;

import Server.ReceivedRequest.UDPReceivedRequest;
import Server.Response.UDPResponse;
import backends.AuthBackend.services.auth.AuthMessage;
import backends.AuthBackend.services.auth.Message;
import backends.AuthBackend.services.auth.User;
import backends.AuthBackend.services.auth.Repository.AuthRepositoryUDP;

public class UDPResolver {
  private final UDPReceivedRequest request;
  private final Character method;
  private final AuthRepositoryUDP authRepositoryUDP;

  public UDPResolver(UDPReceivedRequest request) throws UnknownHostException{
    this.request = request;
    this.authRepositoryUDP = new AuthRepositoryUDP("127.0.0.1");
    method = request.getMethod();
  }
  
  public void run() throws IOException{
    handleRequest();
  }

  private void handleRequest() throws IOException{
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

  private void createResponse(String responseMessage) {
    new UDPResponse(
      request.getSocket(), 
      responseMessage,
      request.getPacket().getAddress(),
      request.getPacket().getPort()
    ).send();
  }

  private void handleCreateMethod() throws IOException {
    String payload = request.getPayload();
    User user = authRepositoryUDP.createUser(payload);
    String responseMessage;  
  
    if(user == null)
      responseMessage = new Message(400 , "User already exists").toJSON();
    else
      responseMessage = user.toJSON();

    createResponse(responseMessage);
  }

  private void handleReadMethod() throws IOException {
    String payload = request.getPayload();
    User user = authRepositoryUDP.findUserById(payload);
    String responseMessage; 
       
    if(user == null)
      responseMessage = new Message(400 , "User does not exists").toJSON();
    else
      responseMessage = user.toJSON();

    createResponse(responseMessage);
  }

  private void handleUpdateMethod() throws IOException {
    String payload = request.getPayload();
    User user = authRepositoryUDP.updatePassword(payload);
    String responseMessage;
        
    if(user == null)
      responseMessage = new Message(400 , "User does not exists").toJSON();
    else
      responseMessage = user.toJSON();

    createResponse(responseMessage);
  }

  private void handleDeleteMethod() throws IOException {
    String payload = request.getPayload();
    boolean isDeleted = authRepositoryUDP.deleteById(payload);
    String responseMessage;

    if(!isDeleted)
      responseMessage = new Message(400 , "User does not exists.").toJSON();
    else
      responseMessage = new Message(200 , "User has been deleted").toJSON();

    createResponse(responseMessage);
  }

  private void handleAuthMethod(){
    String payload = request.getPayload();
    Integer userId = authRepositoryUDP.authenticate(payload);
    String responseMessage;

    if(userId == null)
      responseMessage = new Message(400 , "User does not exists").toJSON();
    else
      responseMessage = new AuthMessage(userId, "SIST-DIST").toJSON();

    createResponse(responseMessage);
  }

  private void handleError(){
    createResponse(new Message(404 , "Server Error").toJSON());
  }
}
