package Server.ReceivedRequest;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPReceivedRequest {
  private final DatagramSocket socket;
  private final byte[] packetBuffer;
  private final DatagramPacket packet;
  private String header;
  private String body;
  private String payload;

  public UDPReceivedRequest(DatagramSocket socket) throws IOException {
    this.socket = socket;
    packetBuffer = new byte[this.socket.getReceiveBufferSize()];
    packet = new DatagramPacket(packetBuffer, packetBuffer.length);
    socket.setSoTimeout(3000);
    this.socket.receive(packet);
    readMessageFromPacket();
  }

  public void readMessageFromPacket(){
    try {
      payload = "";
      byte[] bytes = packet.getData();

      for(byte b : bytes){
        if((int)b == 0)
          break;
        payload += (char) b;
      }

      if(payload.equals("alive?")){
        socket.send(packet);
      }

      int division = payload.indexOf('{');
      body = payload.substring(division);
      header = payload.substring(0, division);
    } catch (Exception e) { }
  }

  public String getBody() {
    return body;
  }

  public String getPayload(){
    return payload;
  }

  public String getHeader() {
    return header;
  }

  public Character getMethod(){
    return header.charAt(0);
  }

  public String getAddress(){
    return packet.getSocketAddress().toString();
  }

  public DatagramPacket getPacket(){
    return packet;
  }

  public DatagramSocket getSocket(){
    return socket;
  }
}
