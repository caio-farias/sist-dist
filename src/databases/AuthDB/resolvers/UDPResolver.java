package databases.AuthDB.resolvers;

import java.io.IOException;

import Server.ReceivedRequest.UDPReceivedRequest;
import Server.Response.UDPResponse;
import databases.AuthDB.services.users.UserRepository;
import databases.AuthDB.services.auth.Auth;
import databases.AuthDB.services.users.Message;
import databases.AuthDB.services.users.User;

public class UDPResolver {
  private final UDPReceivedRequest request;
  private final Character method;
  private final UserRepository userRepository;

  public UDPResolver(UDPReceivedRequest request, UserRepository userRepository){
    this.request = request;
    this.userRepository = userRepository;
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
    String body = request.getBody();
    User user = userRepository.create(new User(body));
    String responseMessage;  
  
    if(user == null)
      responseMessage = new Message(400 , "User already exists").toJSON();
    else
      responseMessage = user.toJSON();

    createResponse(responseMessage);
  }

  private void handleReadMethod() throws IOException {
    String body = request.getBody();
    User user = userRepository.findById(new User(body).getId());
    String responseMessage; 
       
    if(user == null)
      responseMessage = new Message(400 , "User does not exists").toJSON();
    else
      responseMessage = user.toJSON();

    createResponse(responseMessage);
  }

  private void handleUpdateMethod() throws IOException {
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
  }

  private void handleDeleteMethod() throws IOException {
    String body = request.getBody();
    boolean isDeleted = userRepository.deleteById(new User(body).getId());
    String responseMessage;

    if(!isDeleted)
      responseMessage = new Message(400 , "User does not exists.").toJSON();
    else
      responseMessage = new Message(200 , "User has been deleted").toJSON();

    createResponse(responseMessage);
  }

  private void handleAuthMethod(){
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
  }

  private void handleError(){
    createResponse(new Message(404 , "Server Error").toJSON());
  }
}
