package Proxy.Request;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import Server.ReceivedRequest.UDPReceivedRequest;

public class UDPRequest {
  private final DatagramSocket socket;
  private final byte[] packetBuffer;
  private final int remotePort;
  private final InetAddress inetAddress;
  private UDPReceivedRequest response;
  private String responseHeader;
  private String responseBody;

  public UDPRequest(
    String requestContent, 
    InetAddress inetAdress, 
    int remotePort
  ) throws SocketException
  {
    this.socket = new DatagramSocket();
    this.remotePort = remotePort;
    this.inetAddress = inetAdress;
    packetBuffer = requestContent.getBytes();
  }

  public String getResponseBody() {
		return responseBody;
	}

	public String getResponseHeader() {
		return responseHeader;
	}

	public String sendAndAwaitResponse(){
    try {
      DatagramPacket responsePacket = new DatagramPacket(
        packetBuffer, 
        packetBuffer.length,
        inetAddress,
        remotePort
      );
      socket.send(responsePacket);
      return awaitRequestResponse();
    } catch (IOException e) {
      System.out.println("Error on sending response with CRUD/UDP..");

    }
    return null;
  }

  private String awaitRequestResponse() throws SocketException, IOException {
    response = new UDPReceivedRequest(socket);
    responseHeader = response.getHeader();
    responseBody = response.getBody();
    return responseBody;
  }
}
