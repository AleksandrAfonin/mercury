package ru.mycompany.awt;

public class Task implements Runnable {
  private Handler handler;
  private User user;

  public Task(Handler handler, User user){
    this.handler = handler;
    this.user = user;
  }

  @Override
  public void run() {
      long currentTime = System.currentTimeMillis();
      long nextTime = user.getNextTime();
      if (currentTime < nextTime) {
        return;
      }
      //webDriver.manage().window().maximize();// Устанавливаем размеры окна браузера
      try {
        handler.run();
        //setNextTime(user);
      } catch (Exception e) {
        e.printStackTrace();
        handler.getWebDriver().quit();
      }
  }

  private void setNextTime(User user) {
    long nextTime = System.currentTimeMillis() + user.getIntervalMinutes() * 60000L;
    SQLiteProvider.getInstance().setNextTime(user, nextTime);
    user.setNextTime(nextTime);
  }
}
