package ru.mycompany.examples;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

public class SocPublicHandler {
  private final WebDriver WEB_DRIVER;
  private final String E_MAIL;    // alsupp@yandex.ru
  private final String PASSWORD;  // 19b650660b
  private final Actions ACTIONS;
  private final String URL;

  public SocPublicHandler(WebDriver webDriver, String eMail, String password) {
    this.WEB_DRIVER = webDriver;
    this.E_MAIL = eMail;
    this.PASSWORD = password;
    this.ACTIONS = new Actions(webDriver, Duration.ofSeconds(1));
    this.URL = "https://socpublic.com/auth_login.html";
  }

  public void run() throws WebDriverException {

    System.out.println("Account: " + E_MAIL);

    if (!start()) {
      WEB_DRIVER.quit();
      return;
    }

    if (!authorization()) {
      WEB_DRIVER.quit();
      return;
    }

//    if (!goToPaidTransitions()) {
//      WEB_DRIVER.quit();
//      return;
//    }
//
//    if (!goToPaidSurfing()) {
//      WEB_DRIVER.quit();
//      return;
//    }
////=============================
//    if (!goToPaidTransitions()) {
//      WEB_DRIVER.quit();
//      return;
//    }
//
//    if (!goToPaidSurfing()) {
//      WEB_DRIVER.quit();
//      return;
//    }
////=============================
//
////    if (!goToYouTube()) {
////      WEB_DRIVER.quit();
////      return;
////    }

    pause(10000);
    WEB_DRIVER.quit();
  }





  private boolean authorization() {
    String[] texts = new String[]{"Логин и пароль", "Сводка"};
    int pageNum = checkPages(texts, 30);
    switch (pageNum) {
      case 0 -> {
        System.out.println("На странице авторизации");
        if (enterAccountData()) {
          texts = new String[]{"Сводка"};
          pageNum = checkPages(texts, 30);
          return pageNum == 0;
        }
        return false;
      }
      case 1 -> {
        System.out.println("Все Ок. Авторизованы.");
        return true;
      }
      default -> {
        return false;
      }
    }
  }

  private boolean enterAccountData() {
    WebElement webElement = getElementByXpathWithCount("//*/form/div/div/input[@name='name']", 30);
    if (webElement == null) {
      return false;
    }
    ACTIONS.sendKeys(webElement, E_MAIL).perform();
    webElement = getElementByXpathWithCount("//*/form/div/div/input[@name='password']", 30);
    if (webElement == null) {
      return false;
    }
    ACTIONS.sendKeys(webElement, PASSWORD).perform();
    webElement = getElementByXpathWithCount("//*/iframe[@title='reCAPTCHA']", 30);
    if (webElement == null){
      return false;
    }
    String currentPage = WEB_DRIVER.getWindowHandle();
    WEB_DRIVER.switchTo().frame(webElement);
    webElement = getElementByXpathWithCount("//*/span[@role='checkbox'][@aria-checked='false']", 30);
    if (webElement == null){
      WEB_DRIVER.switchTo().window(currentPage);
      return false;
    }
    ACTIONS.click(webElement).perform();
    pause(1000);
    webElement = getElementByXpathWithCount("//*/span[@role='checkbox'][@aria-checked='true']", 30);
    if (webElement == null){
      WEB_DRIVER.switchTo().window(currentPage);
      return false;
    }
    WEB_DRIVER.switchTo().window(currentPage);

    pause(5000);
    return true;
  }

  private int checkPages(String[] texts, int count) {
    if (texts == null || texts.length == 0) {
      return -1;
    }
    WebElement element;
    for (int i = 0; i < count; i++) {
      pause(2000);
      for (int j = 0; j < texts.length; j++) {
        element = getElementByXpathWithCount("//*[text()[contains(.,'" + texts[j] + "')]]", 1);
        if (element != null) {
          return j;
        }
      }
    }
    return -1;
  }

  private WebElement getElementByXpathWithCount(String xpath, int count) {
    WebElement webElement;
    for (int i = 0; i < count; i++) {
      pause(2000);
      try {
        webElement = WEB_DRIVER.findElement(By.xpath(xpath));
        return webElement;
      } catch (NoSuchElementException ignored) {
      }
    }
    return null;
  }

  private boolean start() {
    try {
      WEB_DRIVER.get(URL);
      System.out.println(WEB_DRIVER.getTitle() + " Стартовая страница загружена");
      return true;
    } catch (TimeoutException e) {
      return false;
    }
  }

  private void pause(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException ignored) {
    }
  }
}
