import java.io.IOException;

import backends.AuthBackend.AuthBackend;
import backends.UserBackend.UserBackend;
import databases.AuthDB.AuthDB;
import databases.UserDB.UserDB;


public class Main {

  public static void main(String[] args) throws IOException, InterruptedException {
    // new AuthDB("8081").start();
    // new AuthBackend("8082").start();
    // new AuthBackend("8080").start();
    
    
    // new UserDB("8091").start();
    // new UserBackend("8092").start();
    new UserBackend("8090").start();
  }
}
