package Server.Response;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import Server.ReceivedRequest.TCPReceivedRequest;

public class TCPResponse {
  private final Socket socket;
  private final String message;

  public TCPResponse(TCPReceivedRequest request, String message) {
    socket = request.getSocket();
    this.message = message;
  }

  public void send(){
    try {
      createResponse(message);
    } catch (IOException e) {

    }
  }

  private void createResponse(String message) throws IOException{
    try {
      DataOutputStream out = new DataOutputStream(socket.getOutputStream());
      out.writeBytes(message);
      out.flush();
      out.close();
      socket.close();
    } catch (Exception e) { }
  }
}
