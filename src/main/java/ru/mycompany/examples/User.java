package ru.mycompany.examples;

public class User {
  private final int id;
  private final String login;
  private final String password;
  private long nextTime;
  private final int intervalMinutes;

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

  public User(int id, String login, String password, long nextTime, int intervalMinutes) {
    this.id = id;
    this.login = login;
    this.password = password;
    this.nextTime = nextTime;
    this.intervalMinutes = intervalMinutes;
  }
}
