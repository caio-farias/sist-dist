package Proxy.Response;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPResponse {
  final DatagramSocket socket;
  final DatagramPacket packet;
  final InetAddress address;
  final int port;
  final String message;
  
  public UDPResponse(
    DatagramSocket socket,
    String message,
    InetAddress address, 
    int port) {
    this.socket = socket;
    this.message = message;
    this.address = address;
    this.port = port;

    byte[] packetBuffer = new byte[message.getBytes().length];
    packet = new DatagramPacket(
      packetBuffer, 
      packetBuffer.length,
      address,
      port  
    );
  }

  public void send(){
    try {
      DatagramPacket responsePacket = new DatagramPacket(
        message.getBytes(), 
        message.getBytes().length,
        packet.getAddress(), 
        packet.getPort()
      );
      socket.send(responsePacket);
    } catch (IOException e) {
      System.out.println("Error on sending response with CRUD/UDP..");

    }
  }
}
