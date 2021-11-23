package databases.AuthDB.services.users;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class UserRepository {
  private final Semaphore semaphore;
  private List<User> users;
  private static Integer size;

  public UserRepository(Semaphore semaphore) {
    this.semaphore = semaphore;
    users = new ArrayList<User>();
    size = users.size();
  }

  public User create(User incomingUser){
    if(incomingUser.isEmpty())
      return null;
    try {
      User checkUser = findByUsername(incomingUser.getUsername());
      if(checkUser != null)
        return null;
      // semaphore.acquireUninterruptibly();
      size++;
      User user = new User(size, incomingUser.getUsername(), incomingUser.getPassword());
      users.add(user);
      // semaphore.release();
      return user;
    } catch (Exception e) {

    }
    return null;
  }
  
  public User findById(Integer id){
    try {
      if(size < 1)
        return null;
      for(User user : users){
        if(user.getId() == id)
          return user;
      }
    } catch (Exception e) {

    }

    return null;
  }

  public User findByUsername(String username){
    try {
      if(size < 1)
        return null;
      for(User user : users){
        if(user.getUsername().equals(username))
          return user;
      }
    } catch (Exception e) {

    }

    return null;
  }

  public User updatePassword(Integer id, String password){
    try {
      User user = findById(id);
      if(user == null)
        return null;
      else if(user.getPassword().equals(password))
        return user;
      // semaphore.acquireUninterruptibly();
      user.setPassword(password);
      // semaphore.release();
      return user;
    } catch (Exception e) {

    }
    return null;
  }

  public boolean deleteById(Integer id){
    try {
      User user = findById(id);
      if(user == null)
        return false;
      // semaphore.acquireUninterruptibly();
      users.remove(user);
      // semaphore.release();
      return true;
    } catch (Exception e) {

    }
    return false;
  }

  public Integer authenticate(String username, String password){
    if(size < 1)
      return null;
    try {
      for(User user : users){
        if(user.getUsername().equals(username)
          && user.getPassword().equals(password))
          return user.getId();
      }
    } catch (Exception e) {

    }
    return null;
  }
}
