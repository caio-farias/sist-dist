package backends.AuthBackend.services.auth;

import com.google.gson.GsonBuilder;

public class AuthMessage {
  private Integer userId;
  private String userToken;
  
  public AuthMessage(){}
  
  public AuthMessage(Integer userId, String userToken){
    setUserId(userId);
    setUserToken(userToken);
  }

  public AuthMessage( String content){
    try {
      AuthMessage authMessage = new GsonBuilder()
        .setLenient()
        .setPrettyPrinting()
        .create()
        .fromJson(content, AuthMessage.class);
        
      setUserId(authMessage.getUserId());
      setUserToken(authMessage.getUserToken());
      
    } catch (Exception e) {
    }
  }

  public String toJSON() {
    return new GsonBuilder()
    .setPrettyPrinting()
    .create()
    .toJson(this);
  }
  
  public Integer getUserId() {
    return userId;
  }
  public String getUserToken() {
    return userToken;
  }
  public void setUserToken(String userToken) {
    this.userToken = userToken;
  }
  public void setUserId(Integer userId) {
    this.userId = userId;
  }
}
