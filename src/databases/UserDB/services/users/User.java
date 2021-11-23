package databases.UserDB.services.users;

import java.util.Objects;
import java.util.stream.Stream;

import com.google.gson.GsonBuilder;

public class User {
  private Integer id;
  private String firstName;
  private String lastName;
  private String email;
  private String username;
  
  public User(){}
  
  public User(
    Integer id,
    String firstName,
    String lastName,
    String email,
    String username
  ){
    this.id = id;
    setFirstName(firstName); 
    setLastName(lastName);
    setEmail(email); 
    setUsername(username);
  }


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
      setFirstName(user.getFirstName()); 
      setLastName(user.getLastName());
      setEmail(user.getEmail()); 
      setUsername(user.getUsername());
      
    } catch (Exception e) {

      System.out.println(content);
    }
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }
  
  public String toJSON() {
    return new GsonBuilder()
    .setPrettyPrinting()
    .create()
    .toJson(this);
  }

  public boolean isEmpty(){
    return Stream.of(id, firstName, lastName, email, username)
      .allMatch(Objects::isNull);
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    if(id == null)
      return;
    this.id = id;
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
