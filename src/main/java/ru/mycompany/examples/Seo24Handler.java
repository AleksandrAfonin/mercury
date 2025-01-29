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
import java.util.Date;
import java.util.List;

public class Seo24Handler {
  private final WebDriver WEB_DRIVER;
  private final String E_MAIL;    // alsupp@yandex.ru
  private final String PASSWORD;  // 19b650660b
  private final Actions ACTIONS;
  private final String URL;
  private final Processing processing;
  private final String ENTER = "//*/div[@class='m-t-15']/a";
  private final String SURFING_SITES = "//*/ul/li[1]/a[text()[contains(.,'Сёрфинг')]]";
  private final String AUTHORISATION = "//*/div[@class='content-br']/h1[text()[contains(.,'Вход')]]";// Контроль страницы авторизации
  private final String WELL_COME = "//*/div[@class='user-b'][@id='br_title_1']";// Контроль рабочей страницы
  private final String INPUT_USERNAME = "//*/p/input[@id='login_email']";
  private final String INPUT_PASSWORD = "//*/p/input[@id='login_pass']";
  private final String ENTER_BUTTON = "//*/div[@class='m-t-20']/span[@id='login_js']";
  private final String CONFIRM = "//*/span[@class='capthes'][text()[contains(.,'Подтвердите!')]]";// Подтвердить просмотр
  private final String MENU = "//*/div[@class='left-panel']/span[@class='menu-head']";// Меню пользователя
  private final String DINAMIC_LINK = "//*/div[@class='content-br']/h1[text()[contains(.,'Сёрфинг')]]";// Контроль страницы динамических ссылок
  private final String TABLE_SURFING = "//*/span[@id='load_take']/table[@class='row_table']";
  private final String CLICK_LINK = "./tbody/tr/td[2]/a";
  private final String GET_VIEW = "./tbody/tr/td[2]/a[text()[contains(.,'Приступить к просмотру')]]";

  public Seo24Handler(WebDriver webDriver, User user) throws AWTException {
    this.WEB_DRIVER = webDriver;
    this.processing = new Processing(webDriver);
    this.E_MAIL = user.getLogin();
    this.PASSWORD = user.getPassword();
    this.ACTIONS = new Actions(webDriver, Duration.ofSeconds(1));
    this.URL = "https://seo-24.pro";

  }

  public void run() throws WebDriverException {
    Date date = new Date(System.currentTimeMillis());
    System.out.println(date + "    Account: " + E_MAIL);

    if (!start()) {
      WEB_DRIVER.quit();
      return;
    }

    if (!authorization()) {
      WEB_DRIVER.quit();
      return;
    }

    if (!goToPaidSurfing()) {
      WEB_DRIVER.quit();
      return;
    }

    pause(10000);
    WEB_DRIVER.quit();
  }

  private boolean goToPaidSurfing() {
    if (!expandTheListToEarn()) {
      return false;
    }
    WebElement webElement = processing.getElementByXpathWithCount(SURFING_SITES, 30);
    if (webElement == null) {
      return false;
    }
    try {
      ACTIONS.click(webElement).perform();
      pause(2000);
      WebElement checkPage = processing.getElementByXpathWithCount(DINAMIC_LINK, 30);
      if (checkPage == null) {
        return false;
      }
      pause(1000);
      List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath(TABLE_SURFING));
      if (dataElements.isEmpty()) {
        return true;
      }
      processing.scrollPageDown(300);
      for (WebElement element : dataElements) {
        WebElement click = processing.getElementByXpathWithCount(element, CLICK_LINK, 5);
        if (click == null){
          continue;
        }
        pause(500);
        ACTIONS.click(click).perform();
        try {
          WebElement begin = processing.getElementByXpathWithCount(element, GET_VIEW, 10);
          if (begin == null) {
            continue;
          }
          ACTIONS.click(begin).perform();
          pause(5000);
        } catch (Exception e) {
          continue;
        }
        if (processing.isMore1TabsWithCount(10)) {
          Object[] windowsHandles = WEB_DRIVER.getWindowHandles().toArray();
          WEB_DRIVER.switchTo().window((String) windowsHandles[1]);
          confirm();
          processing.closeAllTabs(30);
        }
        processing.scrollPageDown(60);
      }
    } catch (TimeoutException e) {
      return false;
    }
    return true;
  }

  private boolean confirm() {
    WebElement webElement = processing.getElementByXpathWithCount(CONFIRM, 120);
    if (webElement == null) {
      return false;
    }
    clickInteractable(webElement, 20);
    pause(3000);
    return true;
  }

  private boolean clickInteractable(WebElement webElement, int count){
    for (int i = 0; i < count; i++) {
      try {
        ACTIONS.click(webElement).perform();
        return true;
      }catch (ElementNotInteractableException e){
        pause(2000);
      }
    }
    return false;
  }

  private boolean expandTheListToEarn() {
    WebElement webElement = processing.getElementByXpathWithCount(MENU, 30);
    if (webElement == null) {
      return false;
    }
    JavascriptExecutor js = (JavascriptExecutor) WEB_DRIVER;
    js.executeScript("arguments[0].setAttribute('style', 'display: block;');", webElement);
    pause(1000);
    return true;
  }

  private boolean authorization() {
    WebElement webElement = processing.getElementByXpathWithCount(ENTER, 30);
    if (webElement == null) {
      return false;
    }
    ACTIONS.click(webElement).perform();
    pause(5000);
    webElement = processing.getElementByXpathWithCount(AUTHORISATION, 30);
    if (webElement == null) {
      return false;
    }
    if (enterAccountData()) {
      webElement = processing.getElementByXpathWithCount(WELL_COME, 30);
      if (webElement == null) {
        return false;
      }
      processing.scrollPageDown(300);
      //closeMailMessage();
      return true;
    }
    return false;
  }

  private boolean enterAccountData() {
    WebElement webElement = processing.getElementByXpathWithCount(INPUT_USERNAME, 15);
    if (webElement == null) {
      return false;
    }
    ACTIONS.sendKeys(webElement, E_MAIL).perform();
    webElement = processing.getElementByXpathWithCount(INPUT_PASSWORD, 15);
    if (webElement == null) {
      return false;
    }
    ACTIONS.sendKeys(webElement, PASSWORD).perform();
    webElement = processing.getElementByXpathWithCount(ENTER_BUTTON, 15);
    if (webElement == null) {
      return false;
    }
    ACTIONS.click(webElement).perform();
    return true;
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
