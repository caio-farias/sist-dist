package databases.AuthDB.services.auth;

import com.google.gson.GsonBuilder;

public class Auth {
  private Integer userId;
  private String userToken;
  
  public Auth(){}
  
  public Auth(Integer userId, String userToken){
    setUserId(userId);
    setUserToken(userToken);
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
