package databases.UserDB.handlers;

/**
 * Handler
 */
public interface Handler extends Runnable{

  @Override
  public void run();
  public void receiveMessage();
  public void replyMessage();
}