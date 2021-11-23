package Proxy.ReceivedRequest;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class TCPReceivedRequest {
  private final Socket socket;
  private String method = "";
  private String path = "";
  private boolean isHTTP = false;
  private String[] headerLines;
  private String header = "";
  private String body = "";

  public TCPReceivedRequest(Socket socket) throws IOException {
    this.socket = socket;
    readRequestContent();
    extractPayloadoInfo();
  }

  private void extractPayloadoInfo(){
    try {
      String[] headerInfo = header.split("\n");
      if(headerInfo.length > 1 && headerInfo[0].split(" ").length <= 2){
        method = headerInfo[0].split(" ")[0];
        path = headerInfo[0].split(" ")[1];
        return;
      }
      headerLines = headerInfo[0].split(" ");
      method = headerLines[0];
      path = headerLines[1];
      isHTTP = headerLines[2].contains("HTTP");
    } catch (Exception e) {

    }
  }

  private void readRequestContent() throws IOException {
    try { 
      InputStream in = socket.getInputStream();
      BufferedInputStream bf = new BufferedInputStream(in);
      int dataInt = bf.read();
      String protocolHeader = "";

      while((char) dataInt != '\n'){
        protocolHeader += (char) dataInt;
        dataInt = bf.read();
      }

      String[] headerInfo = protocolHeader.split("\n")[0].split(" ");
      boolean isNoBody = headerInfo[0].equals("GET") || headerInfo[0].equals("DELETE");

      if(protocolHeader.contains("HTTP") && !isNoBody){
        readAsHTTP(bf);
      }else if (!isNoBody)
        readAsCRUD(bf);
      
      header = protocolHeader + '\n' + header;
    } catch (Exception e) {

    }
  }

  private void readAsHTTP(BufferedInputStream bf) throws IOException {
    int dataInt = bf.read();
    String output = "";
    
    try {
      while(true){
        char letter = (char) dataInt;
        output += letter;
        dataInt = bf.read();
        if((char) dataInt == '}'){
          output += (char) dataInt;
          break;
        }        
    }
      int jj = output.indexOf("{");
      header = output.substring(0, jj);
      body =  output.substring(jj);  
    } catch (Exception e) {

    }
  }

  public void readAsCRUD(BufferedInputStream bf) throws IOException{
    byte[] bytesBody;
    int dataInt = bf.read();
    try {
      
      while(true){
        char letter = (char) dataInt; 
        if(header.contains("Content-Length: ")){
          String size = "" + letter;
          int jj;
          while(true){
            jj = bf.read();
            char l = (char) jj;
            if(l == '\n') break;
            size += l;
          }
          bytesBody = bf.readNBytes(Integer.valueOf(size));
          size += '\n';
          header += size;
          break;
        }
        header += letter;      
        dataInt = bf.read();
      }
      body = new String(bytesBody);
    } catch (Exception e) {
      //TODO: handle exception
    }
  }


  public String getMethod() {
    return method;
  }

  public String getHeader(){
    return header;
  }

  public String getPath(){
    return path;
  }

  public String getBody(){
    return body;
  }

  public String getPayload(){
    return header + body;
  }

  public boolean isHTTP(){
    return isHTTP;
  }

  public Socket getSocket(){
    return socket;
  }

  public String getAddress(){
    return socket.getRemoteSocketAddress().toString();
  }
}
