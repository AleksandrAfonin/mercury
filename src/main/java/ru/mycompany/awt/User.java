package ru.mycompany.awt;

public class User {
  private final int id;
  private final String login;
  private final String password;
  private long nextTime;
  private final int intervalMinutes;
  private final Browser browser;
  private final HandlerName handler;

  public int getId() {
    return id;
  }

  public String getLogin() {
    return login;
  }

  public String getPassword() {
    return password;
  }

  public long getNextTime() {
    return nextTime;
  }

  public int getIntervalMinutes() {
    return intervalMinutes;
  }

  public void setNextTime(long nextTime) {
    this.nextTime = nextTime;
  }

  public Browser getBrowser() {
    return browser;
  }

  public HandlerName getHandler() {
    return handler;
  }

  public User(int id, String login, String password, String browser, String handler, long nextTime, int intervalMinutes) {
    this.id = id;
    this.login = login;
    this.password = password;
    this.nextTime = nextTime;
    this.intervalMinutes = intervalMinutes;
    this.browser = Browser.valueOf(browser);
    this.handler = HandlerName.valueOf(handler);
  }
}
