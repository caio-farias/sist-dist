import java.io.IOException;

import backends.AuthBackend.AuthBackend;



public class Backup {
  
  public static void main(String[] args) throws IOException, InterruptedException {
    new AuthBackend("8080").start();
  }
}
