package databases.UserDB.services.users;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class UserRepository {
  private final Semaphore semaphore;
  private final List<User> users;
  private int size;
  public UserRepository(Semaphore semaphore) {
    this.semaphore = semaphore;
    users = new ArrayList<User>();
    size = users.size();
  }

  public User create(User user){
    if(user.isEmpty() || user.getUsername() == null)
      return null;
    try {
      User checkUser = findByUsername(user.getUsername());
      if(checkUser != null)
        return null;
      // semaphore.acquireUninterruptibly();
      size += 1;
      user.setId(size);
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

  public User finUserByUsername(String username){
    try {
      if(size < 1)
        return null;
      for(User user : users){
        if(user.getUsername() == username)
          return user;
      }
    } catch (Exception e) {

    }
    return null;
  }

  public User finUserByEmail(String email){
    try {
      for(User user : users){
        if(user == null)
          continue;
        if(user.getEmail() == email)
          return user;
      }
    } catch (Exception e) {

    }
    return null;
  }

  public User updateEmail(Integer id, String email){
    try {
      User user = findById(id);
      if(user == null)
        return null;
      else if(user.getEmail().equals(email))
        return user;
      // semaphore.acquireUninterruptibly();
      user.setEmail(email);
      // semaphore.release();
      return user;
    } catch (Exception e) {

    }
    return null;
  }

  public User updateFirstName(Integer id, String firstName){
    try {
      User user = findById(id);
      if(user == null)
        return null;
      else if(user.getFirstName().equals(firstName))
        return user;
      // semaphore.acquireUninterruptibly();
      user.setFirstName(firstName);
      // semaphore.release();
      return user;
    } catch (Exception e) {

    }
    return null;
  }

    public User updateLastName(Integer id, String lastName){
    try {
      User user = findById(id);
      if(user == null)
        return null;
      else if(user.getLastName().equals(lastName))
        return user;
      // semaphore.acquireUninterruptibly();
      user.setLastName(lastName);
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
}
