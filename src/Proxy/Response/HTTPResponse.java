package Proxy.Response;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

import Proxy.ReceivedRequest.TCPReceivedRequest;

public class HTTPResponse {
  private final Socket socket;
  private final int statusCode;
  private String responseString;
  private String contentLengthHeader;
  private final String serverHeader = "Server: WebServer";
  private final String contentTypeHeader = "Content-Type: application/json";
  private static DataOutputStream out;

  public HTTPResponse(TCPReceivedRequest request, String responseString, int statusCode) {
    this.statusCode = statusCode;
    this.responseString = responseString;
    socket = request.getSocket(); 
    contentLengthHeader = "Content-Length: " + responseString.getBytes().length;
    try {
      out = new DataOutputStream(socket.getOutputStream());
    } catch (IOException e) {

    }
  }

  public HTTPResponse(TCPReceivedRequest request, int statusCode) {
    this.statusCode = statusCode;
    socket = request.getSocket(); 
    try {
      out = new DataOutputStream(socket.getOutputStream());
    } catch (IOException e) {

    }
  }

  public void send() {
    try {
      switch (statusCode) {
        case 200:
          mountRequestWithPayload("HTTP/1.0 200 OK");
          return;
        case 201:
          mountRequestWithPayload("HTTP/1.0 201 Created");
          return;
        case 202:
          mountRequestWithPayload("HTTP/1.0 202 Accepted");
          return;
        case 400:
          mountRequest("HTTP/1.0 400 Bad Request");
          return;
        case 401:
          mountRequest("HTTP/1.0 401 Unauthorized");
          return;
        case 404:
          mountRequest("HTTP/1.0 404 Not Found");
          return;
        case 405:
          mountRequest("HTTP/1.0 405 Method Not Allowed");
          return;
        default:
          mountRequest("HTTP/1.0 202 Accepted");
          break;
      }
    } catch (Exception e) {
    }
  }

  private void mountRequest(
    String statusLine
    ) throws IOException 
    {
      out.writeBytes(statusLine);
      out.writeBytes("\r\n");
      out.writeBytes(serverHeader);
      out.writeBytes("\r\n");
      out.flush();
      out.close();
      socket.close();
  }

  private void mountRequestWithPayload(
    String statusLine
    ) throws IOException 
    {
      out.writeBytes(statusLine);
      out.writeBytes("\r\n");
      out.writeBytes(serverHeader);
      out.writeBytes("\r\n");
      out.writeBytes(contentTypeHeader);
      out.writeBytes("\r\n");
      out.writeBytes(contentLengthHeader);
      out.writeBytes("\r\n");
      out.writeBytes("\r\n");
      out.writeBytes(responseString);
      out.flush();
      out.close();
      socket.close();
  }
}
