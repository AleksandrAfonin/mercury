package ru.mycompany.examples;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.Point;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.List;

public class GoldenClicksHandler implements Handler{
  private final WebDriver WEB_DRIVER;
  private final String E_MAIL;    // alsupp@yandex.ru
  private final String PASSWORD;  // 19b650660b
  private final Actions ACTIONS;
  private final String URL;
  private final Processing processing;
  private final String SURFING_SITES = "//*/div[@id='mnu_tblock1']/a[text()[contains(.,'Сёрфинг сайтов')]]";
  private final String AUTHORISATION = "//*/div[@class='titles'][text()[contains(.,'Авторизация')]]";// Контроль страницы авторизации
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
  private final String GO_RUTUBE = "//*/div[@id='mnu_tblock1']/a[text()[contains(.,'RuTube')]]";
  private final String RUTUBE_CONTROL = "//*/td[@id='contentwrapper']/div[text()[contains(.,'Выполнение rutube')]]";
  private final String TABLE_RUTUBE = "//*/td[@id='contentwrapper']/div[2]/div/table[@class='work-serf']";
  private final String CLICK_LINK_RUTUBE = "./tbody/tr/td[2]/div[1]/span[1]";
  private final String CLICK_VIEW_RUTUBE = "./tbody/tr/td[2]/div[1]/div/span[text()[contains(.,'Приступить к просмотру')]]";
  private final BufferedImage[] ravno;


  public GoldenClicksHandler(WebDriver webDriver, User user) throws AWTException {
    this.WEB_DRIVER = webDriver;
    this.ACTIONS = new Actions(webDriver, Duration.ofSeconds(1));
    this.processing = new Processing(webDriver, ACTIONS);
    this.E_MAIL = user.getLogin();
    this.PASSWORD = user.getPassword();
    this.URL = "https://golden-clicks.ru/login";

    File directory1 = new File(new File(".", "sprites"), "soofast");
    if (webDriver instanceof ChromeDriver) {
      directory1 = new File(directory1, "chrome");
    }
    if (webDriver instanceof EdgeDriver) {
      directory1 = new File(directory1, "edge");
    }
    this.ravno = init(directory1, "ravno");
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

  @Override
  public void run() throws WebDriverException {
    System.out.println();
    Date date = new Date(System.currentTimeMillis());
    System.out.println(date + "   Account: " + E_MAIL);

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

    if (!goToPaidSurfing()) {
      WEB_DRIVER.quit();
      return;
    }

    if (!goToRuTube()) {
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

  private boolean goToRuTube() {
    WEB_DRIVER.get(WEB_DRIVER.getCurrentUrl());
    WebElement webElement = processing.getElementByXpathWithCount(WELL_COME, 30);
    if (webElement == null){
      return false;
    }
    processing.scrollPageDown(500);
    if (!expandTheListToEarn()){
      return false;
    }
    webElement = processing.getElementByXpathWithCount(GO_RUTUBE, 30);
    if (webElement == null) {
      return false;
    }
    try {
      ACTIONS.click(webElement).perform();
      pause(2000);
      WebElement checkPage = processing.getElementByXpathWithCount(RUTUBE_CONTROL, 30);
      if (checkPage == null){
        return false;
      }
      processing.scrollPageDown(200);
      List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath(TABLE_RUTUBE));
      if (dataElements.isEmpty()){
        return true;
      }
      for (WebElement element : dataElements){
        WebElement el = processing.getElementByXpathWithCount(element, CLICK_LINK_RUTUBE, 1);
        if (el == null){
          continue;
        }
        ACTIONS.click(el).perform();
        try{
          el = processing.getElementByXpathWithCount(element, CLICK_VIEW_RUTUBE, 30);
          if (el == null){
            continue;
          }
          ACTIONS.click(el).perform();
          pause(5000);
        }catch (Exception e){
          continue;
        }
        if (processing.isMore1TabsWithCount(30)){
          Object[] windowsHandles = WEB_DRIVER.getWindowHandles().toArray();
          WEB_DRIVER.switchTo().window((String) windowsHandles[1]);
          pause(10000);
          processing.mouseLeftClick(400, 300, 100,100);
          pause(7000);
          confirmVideo();
          processing.closeAllTabs(30);
        }
        processing.scrollPageDown(60);
      }
    } catch (TimeoutException e) {
      return false;
    }
    return true;
  }

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
      ACTIONS.click(webElement).perform();
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
        ACTIONS.click(element).perform();
        try{
          WebElement child = processing.getElementByXpathWithCount(parent, GET_VIEW, 30);
          if (child == null){
            continue;
          }
          ACTIONS.click(child).perform();
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
        processing.scrollPageDown(60);
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
    ACTIONS.click(webElement).perform();
    pause(5000);
    return true;
  }

  private boolean confirmVideo(){
    WebElement webElement = processing.getElementByXpathWithCount(CONFIRM, 200);
    if (webElement == null){
      return false;
    }
    processing.clickInteractable(webElement, 200);
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
      ACTIONS.click(webElement).perform();
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
        ACTIONS.click(element).perform();
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
      ACTIONS.click(webElement).perform();
      pause(5000);
    }
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
    if (resolveCaptcha()) {
      webElement = processing.getElementByXpathWithCount(ENTER_BUTTON, 15);
      ACTIONS.click(webElement).perform();
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
    ACTIONS.sendKeys(webElement, processing.resolveSarSeoCaptcha(point)).perform();
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
