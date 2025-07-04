package ru.mycompany.awt;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.awt.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.time.Duration;
import java.util.Date;
import java.util.List;

public class ProdvisotsHandler implements Handler {
  private final String sitename;
  private String browser;
  private WebDriver WEB_DRIVER;
  private User user;
  private final String URL;
  private Processing processing;

  private final String SURFING_SITES = "//*/div[@id='mnu_tblock1']/a[text()[contains(.,'Сёрфинг сайтов')]]";
  private final String AUTHORISATION = "//*/div[@class='titles']";// Контроль страницы авторизации
  private final String WELL_COME = "//*/span[@id='mnu_title1']";// Контроль рабочей страницы
  private final String CLOSE_MESSAGE = "//*/div[@class='mail-users']/span[@class='closed']";// Закрыть сообщение
  private final String INPUT_USERNAME = "//*/table[@class='table']/tbody/tr[1]/td[2]/input[@name='username']";
  private final String INPUT_PASSWORD = "//*/table[@class='table']/tbody/tr[2]/td[2]/input[@name='password']";
  private final String ENTER_BUTTON = "//*/span[@class='btn green']";
  private final String INPUT_CODE = "//*/table[@class='table']/tbody/tr[3]/td[2]/input[@name='code']";
  private final String GET_FRAME = "//*/frame[@name='frminfo']";// Получить фрейм просмотра
  private final String CONFIRM = "//*/a[@class='capthes']";// Подтвердить просмотр
  private final String MENU = "//*/div[@id='mnu_tblock1']";// Меню пользователя
  private final String DINAMIC_LINK = "//*/td[@id='contentwrapper']/div[text()[contains(.,'Динамические ссылки')]]";// Контроль страницы динамических ссылок
  private final String TABLE_SURFING = "//*/table[@class='work-serf']/tbody/tr/td[2]/div/a";
  private final String GET_VIEW = "./a[text()[contains(.,'Приступить к просмотру')]]";
  private final List<BufferedImage> ravno;

  public ProdvisotsHandler() throws AWTException {
    this.sitename = "prodvisots";
    this.URL = SQLiteProvider.getInstance().getUrlSite(sitename);
    this.browser = SQLiteProvider.getInstance().getBrowser(sitename);
    this.ravno = SQLiteProvider.getInstance().getSprites(sitename, browser, "ravno");
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
    if (!goToPaidSurfing()) {
      WEB_DRIVER.quit();
      return;
    }

//    if (!goToYouTube()) {
//      WEB_DRIVER.quit();
//      return;
//    }

    pause(5000);
    WEB_DRIVER.quit();
  }

//  private boolean goToYouTube(){
//    if (!expandTheListToEarn()){
//      return false;
//    }
//    WebElement webElement = getElementByXpathWithCount("//*/div[@id='mnu_tblock1']/a[text()[contains(.,'YouTube')]]", 30);
//    if (webElement == null) {
//      System.out.println("YouTube не найдены");
//      return false;
//    }
//    System.out.println("Переход на 'YouTube'");
//    try {
//      ACTIONS.click(webElement).perform();
//      pause(1000);
//      String[] texts = new String[]{"Выполнение Youtube"};
//      int pageNum = checkPages(texts, 30);
//      if (pageNum != 0){
//        return false;
//      }
//      List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath("//*/table[@class='work-serf']/tbody/tr/td[2]/div/span[1]"));
//      if (dataElements.isEmpty()){
//        return true;
//      }
//      for (WebElement element : dataElements){
//        if (element.getText().isEmpty()){
//          continue;
//        }
//        WebElement parent = element.findElement(By.xpath(".."));
//        pause(500);
//        ACTIONS.click(element).perform();
//        pause(5000);
//        try {
//          WebElement child = parent.findElement(By.xpath("./div/span"));
//          if (!child.getText().equals("Приступить к просмотру")) {
//            continue;
//          }
//          ACTIONS.click(child).perform();
//        }catch (Exception e){
//          continue;
//        }
//        if (isMore1TabsWithCount(5)){
//          Object[] pages = WEB_DRIVER.getWindowHandles().toArray();
//          WEB_DRIVER.switchTo().window((String) pages[1]);
//          String[] contents = new String[]{"Запустите видео", };
//          int contentNum = checkPages(texts, 30);
//          if (contentNum != 0){
//            startVideo();
//          }
//
//
//
//          waitTime("Просмотр засчитан!", 200);
//          closeAllTabs();
//        }
//      }
//    } catch (TimeoutException e) {
//      return false;
//    }
//    return true;
//  }




  private boolean goToPaidSurfing(){
    processing.scrollPageDown(500);
    if (!expandTheListToEarn()){
      return false;
    }
    WebElement webElement = processing.getElementByXpathWithCount(SURFING_SITES, 30);
    if (webElement == null) {
      return false;
    }
    try {
      processing.clickElement(webElement);
      pause(2000);
      WebElement checkPage = processing.getElementByXpathWithCount(DINAMIC_LINK, 30);
      if (checkPage == null){
        return false;
      }
      pause(1000);
      List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath(TABLE_SURFING));
      if (dataElements.isEmpty()){
        return true;
      }
      processing.scrollPageDown(300);
      for (WebElement element : dataElements){
        if (element.getText().isEmpty()){
          continue;
        }
        WebElement parent = element.findElement(By.xpath(".."));
        pause(500);
        processing.clickElement(element);
        try{
          WebElement child = processing.getElementByXpathWithCount(parent, GET_VIEW, 30);
          if (child == null){
            continue;
          }
          processing.clickElement(child);
          pause(4000);
        }catch (Exception e){
          continue;
        }
        if (processing.isMore1TabsWithCount(30)){
          Object[] windowsHandles = WEB_DRIVER.getWindowHandles().toArray();
          WEB_DRIVER.switchTo().window((String) windowsHandles[1]);
          confirm();
          processing.closeAllTabs(30);
        }
        processing.scrollPageDown(55);
      }
    } catch (TimeoutException e) {
      return false;
    }
    return true;
  }

  private boolean confirm(){
    WebElement webElement = processing.getElementByXpathWithCount(GET_FRAME, 30);
    if (webElement == null){
      return false;
    }
    WEB_DRIVER.switchTo().frame(webElement);
    webElement = processing.getElementByXpathWithCount(CONFIRM, 200);
    if (webElement == null){
      return false;
    }
    processing.clickElement(webElement);
    pause(5000);
    return true;
  }

  private boolean goToPaidTransitions() {
    processing.scrollPageDown(500);
    if (!expandTheListToEarn()){
      return false;
    }
    WebElement webElement = processing.getElementByXpathWithCount("//*/div[@id='mnu_tblock1']/a[text()[contains(.,'Переходы')]]", 30);
    if (webElement == null) {
      System.out.println("Оплачиваемые переходы не найдены");
      return false;
    }
    System.out.println("Переход на 'Переходы'");
    try {
      processing.clickElement(webElement);
      pause(2000);
      WebElement checkPage = processing.getElementByXpathWithCount("//*/td[@id='contentwrapper']/div[text()[contains(.,'Переходы')]]", 30);
      if (checkPage == null){
        return false;
      }
      List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath("//*/table[@class='work-serf']/tbody/tr/td[2]/div/a"));
      if (dataElements.isEmpty()){
        return true;
      }
      processing.scrollPageDown(300);
      for (WebElement element : dataElements){
        processing.clickElement(element);
        pause(4000);
        if (processing.isMore1TabsWithCount(5)){
          processing.waitTimeOnTitlePage("Просмотр засчитан!", 200);
          processing.closeAllTabs(30);
        }
        processing.scrollPageDown(50);
      }
    } catch (TimeoutException e) {
      return false;
    }
    return true;
  }

  private boolean expandTheListToEarn() {
    WebElement webElement = processing.getElementByXpathWithCount(MENU, 30);
    if (webElement == null) {
      return false;
    }
    JavascriptExecutor js = (JavascriptExecutor) WEB_DRIVER;
    js.executeScript("arguments[0].setAttribute('style', '');", webElement);
    pause(1000);
    return true;
  }

  private boolean authorization() {
    WebElement webElement = processing.getElementByXpathWithCount(AUTHORISATION, 30);
    if (webElement == null){
      return false;
    }
    if (enterAccountData()) {
      webElement = processing.getElementByXpathWithCount(WELL_COME, 30);
      if (webElement == null){
        return false;
      }
      closeMailMessage();
      return true;
    }
    return false;
  }

  private void closeMailMessage(){
    WebElement webElement = processing.getElementByXpathWithCount(CLOSE_MESSAGE, 5);
    if (webElement != null) {
      processing.clickElement(webElement);
      pause(5000);
    }
  }

  private boolean enterAccountData() {
    WebElement webElement = processing.getElementByXpathWithCount(INPUT_USERNAME, 15);
    if (webElement == null) {
      return false;
    }
    processing.sendKeys(webElement, user.getLogin());
    webElement = processing.getElementByXpathWithCount(INPUT_PASSWORD, 15);
    if (webElement == null) {
      return false;
    }
    processing.sendKeys(webElement, user.getPassword());
    if (resolveCaptcha()) {
      webElement = processing.getElementByXpathWithCount(ENTER_BUTTON, 15);
      processing.clickElement(webElement);
      pause(5000);
      return true;
    }
    return false;
  }

  private boolean resolveCaptcha() {
    Point point = processing.find(ravno, true, 5);
    if (point == null) {
      return false;
    }
    WebElement webElement = processing.getElementByXpathWithCount(INPUT_CODE, 15);
    if (webElement == null) {
      return false;
    }
    processing.sendKeys(webElement, processing.resolveSarSeoCaptcha(point));
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
