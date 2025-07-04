package ru.mycompany.awt;

import org.openqa.selenium.*;
import java.util.Date;

public class SocPublicHandler implements Handler {
  private String sitename;
  private String browser;
  private WebDriver WEB_DRIVER;
  private User user;
  private final String URL;
  private Processing processing;

  public SocPublicHandler() {
    this.sitename = "socpublic";
    this.URL = SQLiteProvider.getInstance().getUrlSite(sitename);
    this.browser = SQLiteProvider.getInstance().getBrowser(sitename);
  }

  @Override
  public WebDriver getWebDriver() {
    return WEB_DRIVER;
  }

  @Override
  public void setProperty(WebDriver webDriver, Processing processing, User user) {
    this.WEB_DRIVER = webDriver;
    this.processing = processing;
    this.user = user;
  }

  @Override
  public void run() throws WebDriverException {
    System.out.println();
    Date date = new Date(System.currentTimeMillis());
    System.out.println(date + "   Account: " + user.getLogin());

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

    pause(5000);
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
    processing.sendKeys(webElement, user.getLogin());
    webElement = getElementByXpathWithCount("//*/form/div/div/input[@name='password']", 30);
    if (webElement == null) {
      return false;
    }
    processing.sendKeys(webElement, user.getPassword());
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
    processing.clickElement(webElement);
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
