package databases.AuthDB.services.users;

import java.util.Objects;
import java.util.stream.Stream;

import com.google.gson.GsonBuilder;

public class User {
  private Integer id;
  private String username;
  private String password;
  
  public User(){}
  
  public User(String content){
    try {
      User user = new GsonBuilder()
        .setLenient()
        .serializeNulls()
        .setPrettyPrinting()
        .create()
        .fromJson(content, User.class);

      if(user == null)
        return;
        
      this.id = user.getId();
      setUsername(user.getUsername());
      setPassword(user.getPassword());
      
    } catch (Exception e) {

      System.out.println(content);
    }
  }
  
  public User(Integer id, String username, String password) {
    this.id = id;
    setUsername(username);
    setPassword(password);
  }
  
  public String toJSON() {
    return new GsonBuilder()
    .setPrettyPrinting()
    .create()
    .toJson(this);
  }

  public boolean isEmpty(){
    return Stream.of(id, username, password)
      .allMatch(Objects::isNull);
  }

  public Integer getId() {
    return id;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    if(password == null)
      return;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    if(username == null)
      return;
    this.username = username;
  }

}
