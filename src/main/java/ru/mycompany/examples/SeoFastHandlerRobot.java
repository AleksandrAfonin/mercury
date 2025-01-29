package ru.mycompany.examples;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeoFastHandlerRobot {
  private final WebDriver WEB_DRIVER;
  private final String E_MAIL;    // alsupp@yandex.ru
  private final String PASSWORD;  // 19b650660b
  private final Actions ACTIONS;
  private final String URL;
  private final Processing processing;
  private final Rectangle header;

  private final BufferedImage[] email;
  private final BufferedImage[] visits;
  private final BufferedImage[] roundLinc;
  private final BufferedImage[] id;
  private final BufferedImage[] to_visit;
  private final BufferedImage[] closeActivePage;
  private final BufferedImage[] closePassivePage;
  private final BufferedImage[] completed;

  public SeoFastHandlerRobot(WebDriver webDriver, User user) throws AWTException {
    this.WEB_DRIVER = webDriver;
    this.processing = new Processing(webDriver);
    this.E_MAIL = user.getLogin();
    this.PASSWORD = user.getPassword();
    this.ACTIONS = new Actions(webDriver, Duration.ofSeconds(1));

    this.header = new Rectangle(270, 2, 900, 30);
    this.URL = "https://seo-fast.ru/login";

    File directory1 = new File(new File(".", "sprites"), "seofast");
    if (webDriver instanceof ChromeDriver) {
      directory1 = new File(directory1, "chrome");
    }
    if (webDriver instanceof EdgeDriver) {
      directory1 = new File(directory1, "edge");
    }
    this.email = init(directory1, "e_mail");
    this.visits = init(directory1, "visits");
    this.roundLinc = init(directory1, "round_linc");
    this.id = init(directory1, "id");
    this.to_visit = init(directory1, "to_visit");
    this.closeActivePage = init(directory1, "close_active_page");
    this.closePassivePage = init(directory1, "close_passive_page");
    this.completed = init(directory1, "completed");

  }

  private BufferedImage[] init(File file, String folder) {
    File directory2 = new File(file, folder);
    File[] files = directory2.listFiles();
    BufferedImage[] bufferedImages = new BufferedImage[files.length];
    for (int i = 0; i < files.length; i++) {
      try {
        bufferedImages[i] = ImageIO.read(files[i]);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return bufferedImages;
  }

  public void run() throws WebDriverException {
    Date date = new Date(System.currentTimeMillis());
    System.out.println(date + "   Account: " + E_MAIL);

    if (!start()) {
      WEB_DRIVER.quit();
      return;
    }
    pause(10000, 0);
    if (!authorization()) {
      WEB_DRIVER.quit();
      return;
    }
    processing.mouseScrollDown(3);
    pause(2000, 1000);
    Point point = processing.find(visits, true, 5);
    if (point == null) {
      WEB_DRIVER.quit();
      return;
    }
    System.out.println(point);
    processing.mouseLeftClick(point, 80, 10);
    pause(15000, 5000);

    performingClicks();
//===============================
    System.out.println(point);
    pause(5000, 0);
    WEB_DRIVER.quit();
  }


  private void performingClicks() {
    boolean isFinish = false;
    while (true) {
      Point point = new Point();
      point.x = 550;
      point.y = 400;
      processing.mouseMove(point);
      processing.mouseScrollDown(6);
      pause(3000, 1000);
      List<Point> points = getPointsList();
      if (points.isEmpty()) {
        if (isFinish) {
          return;
        }
        isFinish = true;
        continue;
      }
      isFinish = false;
      System.out.println("Size: " + points.size());
      for (Point p : points) {
        p.x = p.x + 65;
        processing.mouseLeftClick(p, 8, 8);
        if (!checkVisit(p)) {
          continue;
        }
        processing.find(completed, header, true, 90);
        System.out.println("completed:");
        closeAllTabs();
        processing.refreshScreen();
      }
    }
  }

  private boolean checkVisit(Point p) {
    Rectangle rectangle = new Rectangle(p.x, p.y, 250, 30);
    for (int i = 0; i < 10; i++) {
      pause(2000, 0);
      Point point = processing.find(to_visit, rectangle, true, 1);
      //System.out.println("Point: " + point);
      if (point != null) {
        pause(1000, 1000);
        processing.mouseMove(point);
        processing.mouseLeftClick(point, 200, 10);
        pause(10000, 0);
        return true;
      }
      point = processing.find(closeActivePage, header, true, 1);
      if (point != null) {
        return true;
      }
    }
    return false;
  }

  private List<Point> getPointsList() {
    List<Point> points = new ArrayList<>();
    int yPoint = processing.getFullScreen().height;
    Rectangle field = new Rectangle(300, 0, 100, yPoint);
    while (true) {
      Point point = processing.find(roundLinc, field, true, 1);
      if (point == null) {
        break;
      }
      System.out.println(point);
      //processing.mouseMove(point);
      Rectangle rectangle = new Rectangle(point.x, point.y, 100, 32);
      Point pointId = processing.find(id, rectangle, true, 1);
      if (pointId == null) {
        field.y = point.y + 10;
        field.height = yPoint - field.y;
        continue;
      }
      //processing.mouseMove(pointId);
      points.add(point);
      field.y = point.y + 10;
      field.height = yPoint - field.y;
    }
    return points;
  }

  private void closeAllTabs() {
    while (true) {
      Point point = processing.find(closeActivePage, header, true, 2);
      if (point == null) {
        return;
      }
      processing.mouseLeftClick(point, 7, 7);
      pause(4000, 2000);
    }
  }

  private boolean authorization() {
    Point point = processing.find(email, true, 5);
    if (point == null) {
      return false;
    }
    System.out.println(point);
    processing.mouseLeftClick(point, 250, 10);
    processing.sendKey(E_MAIL);
    pause(500, 500);
    point.y = point.y + 36;
    processing.mouseLeftClick(point, 250, 10);
    processing.sendKey(PASSWORD);
    pause(500, 500);
    point.y = point.y + 36;
    point.x = point.x + 5;
    processing.mouseLeftClick(point, 250, 30);
    pause(5000, 5000);
    return true;
  }

  private boolean start() {
    try {
      WEB_DRIVER.manage().window().maximize();
      WEB_DRIVER.get(URL);
      System.out.println(WEB_DRIVER.getTitle() + " Стартовая страница загружена");
      return true;
    } catch (TimeoutException e) {
      return false;
    }
  }

  private void pause(long millis, long dif) {
    if (dif < 0) {
      dif = 0;
    }
    millis = (long) (millis + Math.random() * dif);
    try {
      Thread.sleep(millis);
    } catch (InterruptedException ignored) {
    }
  }

}
