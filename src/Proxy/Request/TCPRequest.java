package Proxy.Request;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

public class TCPRequest {
  private final Socket socket;
  private final String requestContent;
  private static DataOutputStream out;
  private String responseHeader;
  private String responseBody;
  private final boolean isHTTP;

  public TCPRequest(
    String requestContent,
    InetAddress address,  
    int remotePort, 
    boolean isHTTP) throws IOException
    {
    socket = new Socket(address.getHostAddress(), remotePort);
    out = new DataOutputStream(socket.getOutputStream());
    this.requestContent = requestContent;
    this.isHTTP = isHTTP;
  }

	public String sendAndAwaitResponse(){
    try{
      if(isHTTP) sendHTTPPayload(requestContent);
        else sendTCPPayload(requestContent);

      return awaitRequestResponse();
    } catch (Exception e) {

    }
    return null;
  }

  private void sendTCPPayload(String requestContent) throws IOException{
    out.writeBytes(requestContent);
    out.flush();
  }

  private void sendHTTPPayload(String requestContent) throws IOException{
    final String[] fields = requestContent.split("\n");
    final String serverHeader = "Server: WebServer" + "\r\n";
    final String contentTypeHeader = "Content-Type: application/json" + "\r\n";
    final int idx = requestContent.indexOf("{");
    out.writeBytes(fields[0]);
    out.writeBytes(serverHeader);
    out.writeBytes(contentTypeHeader);
    if(idx > 0) out.writeBytes("\r\n" + requestContent.substring(idx));
    out.flush();
  }

  private String awaitRequestResponse() throws SocketException, IOException {
    readResponse();
    return responseBody;
  }

  private void readResponse() throws IOException {
    try {
      InputStream in = socket.getInputStream();
      BufferedInputStream bf = new BufferedInputStream(in);
      responseHeader = "";
      responseBody = "";
      boolean isHeaderReady = false;
      int dataInt = bf.read();
      while(dataInt != -1){
        char letter = (char) dataInt;
        if(letter == '{'){
          isHeaderReady = true;
          responseBody+= letter;
        }
        else if(isHeaderReady) 
          responseBody += letter;
        else
          responseHeader += letter;
        dataInt = bf.read();
      }
      bf.close();
      in.close();
    } catch (Exception e) {

    }
    socket.close();
  }

  public String getResponseHeader() {
		return responseHeader;
	}

  public String getResponseBody() {
		return responseBody;
	}
}