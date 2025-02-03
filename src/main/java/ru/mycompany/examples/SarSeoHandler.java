package ru.mycompany.examples;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.List;

public class SarSeoHandler {
  private final WebDriver WEB_DRIVER;
  private final String E_MAIL;    // alsupp@yandex.ru
  private final String PASSWORD;  // 19b650660b
  private final Actions ACTIONS;
  private final String URL;

  private final Processing processing;
  private final Rectangle header;

  private final BufferedImage[] ravno;
//  private final BufferedImage[] visits;
//  private final BufferedImage[] roundLinc;
//  private final BufferedImage[] id;
//  private final BufferedImage[] to_visit;
  private final BufferedImage[] closeActivePage;
  private final BufferedImage[] closePassivePage;
//  private final BufferedImage[] completed;

  public SarSeoHandler(WebDriver webDriver, User user) throws AWTException {
    this.WEB_DRIVER = webDriver;
    this.ACTIONS = new Actions(webDriver, Duration.ofSeconds(1));
    this.processing = new Processing(webDriver, ACTIONS);
    this.E_MAIL = user.getLogin();
    this.PASSWORD = user.getPassword();
    this.header = new Rectangle(270, 2, 900, 30);
    this.URL = "https://sarseo.su/login";

    File directory1 = new File(new File(".", "sprites"), "sarseo");
    if (webDriver instanceof ChromeDriver) {
      directory1 = new File(directory1, "chrome");
    }
    if (webDriver instanceof EdgeDriver) {
      directory1 = new File(directory1, "edge");
    }
    this.ravno = init(directory1, "ravno");
//    this.visits = init(directory1, "visits");
//    this.roundLinc = init(directory1, "round_linc");
//    this.id = init(directory1, "id");
//    this.to_visit = init(directory1, "to_visit");
    this.closeActivePage = init(directory1, "actclose");
    this.closePassivePage = init(directory1, "pasclose");
//    this.completed = init(directory1, "completed");
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

    if (!authorization()) {
      WEB_DRIVER.quit();
      return;
    }

    if (!goToPaidTransitions()) {
      WEB_DRIVER.quit();
      return;
    }

    if (!goToPaidSurfing()) {
      WEB_DRIVER.quit();
      return;
    }
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

//    if (!goToYouTube()) {
//      WEB_DRIVER.quit();
//      return;
//    }

    pause(10000);
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
    scrollPageDown("500");
    if (!expandTheListToEarn()){
      return false;
    }
    WebElement webElement = getElementByXpathWithCount("//*/div[@id='mnu_tblock1']/a[text()[contains(.,'Сёрфинг')]]", 30);
    if (webElement == null) {
      System.out.println("Серфинг сайтов не найдены");
      return false;
    }
    System.out.println("Переход на 'Серфинг сайтов'");
    try {
      ACTIONS.click(webElement).perform();
      pause(2000);
      WebElement checkPage = getElementByXpathWithCount("//*/td[@id='contentwrapper']/div[text()[contains(.,'Динамические ссылки')]]", 30);
      if (checkPage == null){
        return false;
      }
      List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath("//*/table[@class='work-serf']/tbody/tr/td[2]/div/a"));
      if (dataElements.isEmpty()){
        return true;
      }
      scrollPageDown("300");
      for (WebElement element : dataElements){
        if (element.getText().isEmpty()){
          continue;
        }
        WebElement parent = element.findElement(By.xpath(".."));
        pause(500);
        ACTIONS.click(element).perform();
        pause(4000);
        try{
          WebElement child = parent.findElement(By.tagName("a"));
          String content = child.getText();
          if (!content.equals("Приступить к просмотру")){
            continue;
          }
          ACTIONS.click(child).perform();
          pause(4000);
        }catch (Exception e){
          continue;
        }
        if (isMore1TabsWithCount(5)){
          Object[] windowsHandles = WEB_DRIVER.getWindowHandles().toArray();
          WEB_DRIVER.switchTo().window((String) windowsHandles[1]);
          confirm();
          closeAllTabs();
        }
        scrollPageDown("50");
      }
    } catch (TimeoutException e) {
      return false;
    }
    return true;
  }

  private boolean confirm(){
    WebElement webElement = getElementByXpathWithCount("//*/frame[@name='frminfo']", 10);
    if (webElement == null){
      return false;
    }
    WEB_DRIVER.switchTo().frame(webElement);
    webElement = getElementByXpathWithCount("//*/a[@class='capthes']", 200);
    if (webElement == null){
      return false;
    }
    ACTIONS.click(webElement).perform();
    pause(5000);
    return true;
  }

  private void scrollPageDown(String pixels){
    JavascriptExecutor javascriptExecutor = (JavascriptExecutor) WEB_DRIVER;
    javascriptExecutor.executeScript("window.scrollBy(0," + pixels + ")");
    pause(2000);
  }


  private boolean goToPaidTransitions() {
    scrollPageDown("500");
    if (!expandTheListToEarn()){
      return false;
    }
    WebElement webElement = getElementByXpathWithCount("//*/div[@id='mnu_tblock1']/a[text()[contains(.,'Переходы')]]", 30);
    if (webElement == null) {
      System.out.println("Оплачиваемые переходы не найдены");
      return false;
    }
    System.out.println("Переход на 'Переходы'");
    try {
      ACTIONS.click(webElement).perform();
      pause(2000);
      WebElement checkPage = getElementByXpathWithCount("//*/td[@id='contentwrapper']/div[text()[contains(.,'Переходы')]]", 30);
      if (checkPage == null){
        return false;
      }
      List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath("//*/table[@class='work-serf']/tbody/tr/td[2]/div/a"));
      if (dataElements.isEmpty()){
        return true;
      }
      scrollPageDown("300");
      for (WebElement element : dataElements){
//        if (element.getAttribute("target").equals("_blank")){
//          continue;
//        }
        ACTIONS.click(element).perform();
        pause(4000);
        if (isMore1TabsWithCount(5)){
          waitTime("Просмотр засчитан!", 200);
          closeAllTabs();
        }
        scrollPageDown("50");
      }
    } catch (TimeoutException e) {
      return false;
    }
    return true;
  }

  private boolean closeAllTabs() {
    int count = 30;
    while (count-- > 0) {
      Object[] windowsHandles = WEB_DRIVER.getWindowHandles().toArray();
      if (windowsHandles.length == 1) {
        return true;
      }
      pause(1000);
      for (int i = 1; i < windowsHandles.length; i++) {
        try {
          WEB_DRIVER.switchTo().window((String) windowsHandles[i]);
          WEB_DRIVER.close();
        } catch (WebDriverException ignored) {
        }
      }
      WEB_DRIVER.switchTo().window((String) windowsHandles[0]);
      pause(500);
    }
    return false;
  }

  private boolean waitTime(String titleName, int maxCount){
    for (int i = 0; i < maxCount; i++) {
      pause(2000);
      String title = WEB_DRIVER.getTitle();
      if (titleName.equals(title)){
        return true;
      }
    }
    return false;
  }

  private boolean isMore1TabsWithCount(int count) {
    for (int i = 0; i < count; i++) {
      pause(2000);
      if (WEB_DRIVER.getWindowHandles().toArray().length > 1){
        return true;
      }
    }
    return false;
  }

  private boolean expandTheListToEarn() {
    WebElement webElement = getElementByXpathWithCount("//*/div[@id='mnu_tblock1']", 30);
    if (webElement == null) {
      System.out.println("Нет списка раскрывающегося");
      return false;
    }
    JavascriptExecutor js = (JavascriptExecutor) WEB_DRIVER;
    js.executeScript("arguments[0].setAttribute('style', '');", webElement);
    pause(1000);
    return true;
  }

  private boolean authorization() {
    String[] texts = new String[]{"Вход в аккаунт", "Моя статистика"};
    int pageNum = checkPages(texts, 30);
    switch (pageNum) {
      case 0 -> {
        System.out.println("На странице авторизации");
        if (enterAccountData()) {
          texts = new String[]{"Моя статистика"};
          pageNum = checkPages(texts, 15);
          //closeMailMessage();
          return pageNum == 0;
        }
        return false;
      }
      case 1 -> {
        System.out.println("Все Ок. Авторизованы.");
        //closeMailMessage();
        return true;
      }
      default -> {
        return false;
      }
    }
  }

  private boolean enterAccountData() {
    WebElement webElement = getElementByXpathWithCount("//*/table[@class='table']/tbody/tr[1]/td[2]/input[@name='username']", 15);
    if (webElement == null) {
      return false;
    }
    ACTIONS.sendKeys(webElement, E_MAIL).perform();
    webElement = getElementByXpathWithCount("//*/table[@class='table']/tbody/tr[2]/td[2]/input[@name='password']", 15);
    if (webElement == null) {
      return false;
    }
    ACTIONS.sendKeys(webElement, PASSWORD).perform();
    if (resolveCaptcha()) {
      webElement = getElementByXpathWithCount("//*/span[@class='btn green']", 15);
      ACTIONS.click(webElement).perform();
      pause(5000);
      return true;
    }
    return false;
  }

  private boolean resolveCaptcha() {
    Point point = processing.find(ravno, true, 5);
    if (point == null) {
      WEB_DRIVER.quit();
      return false;
    }
    //System.out.println(point);
    WebElement webElement = getElementByXpathWithCount("//*/table[@class='table']/tbody/tr[3]/td[2]/input[@name='code']", 15);
    if (webElement == null) {
      return false;
    }
    ACTIONS.sendKeys(webElement, processing.resolveSarSeoCaptcha(point)).perform();
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
