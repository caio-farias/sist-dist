package databases.AuthDB.services.users;

import com.google.gson.GsonBuilder;

public class Message {
  private Integer status;
  private String message;

  public Message(){}
  
  public Message(Integer status, String message){
    setMessage(message);
    setStatus(status);
  }

  public Message(String message, String mode){
    setMessage(message);
  }

  public Message(String json){
    try {
      Message message = new GsonBuilder()
        .setLenient()
        .setPrettyPrinting()
        .create()
        .fromJson(json, Message.class);
        
      setMessage(message.getMessage());
      setStatus(message.getStatus());
      
    } catch (Exception e) {
    }
  }

  public String getMessage() {
    return message;
  }
  public void setMessage(String message) {
    this.message = message;
  }
  public Integer getStatus() {
    return status;
  }

  public void setStatus(Integer status) {
    this.status = status;
  }

  public String toJSON() {
    return new GsonBuilder()
    .setPrettyPrinting()
    .create()
    .toJson(this);
  }
}
