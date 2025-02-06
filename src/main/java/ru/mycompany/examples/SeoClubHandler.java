package ru.mycompany.examples;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeoClubHandler {
  private final WebDriver WEB_DRIVER;
  private final String E_MAIL;    // alsupp@yandex.ru
  private final String PASSWORD;  // 19b650660b
  private final Actions ACTIONS;
  private final String URL;
  private final Processing processing;
  private final String AUTHORISATION = "//*/div[@class='titles'][text()[contains(.,'Вход в аккаунт')]]";// Контроль страницы авторизации
  private final String WELL_COME = "//*/span[@id='mnu_title1']";// Контроль рабочей страницы
  private final String CLOSE_MESSAGE = "//*/div[@class='mail-users']/span[@class='closed']";// Закрыть сообщение
  private final String INPUT_USERNAME = "//*/table[@class='table']/tbody/tr[1]/td[2]/input[@name='username']";
  private final String INPUT_PASSWORD = "//*/table[@class='table']/tbody/tr[2]/td[2]/input[@name='password']";
  private final String ENTER_BUTTON = "//*/span[@class='btn green']";
  private final String OUT_CAPCHA_TITLE = "//*/div[@class='out-capcha-title']/b";
  private final String TABLE_CAPCHA = "//*/tr/td[@colspan='2']/div[@class='out-capcha']/label[@class='out-capcha-lab']";
  private final String GO_SURFING = "//*/div[@id='mnu_tblock1']/a[text()[contains(.,'Серфинг сайтов')]]";
  private final String SURFING_CONTROL = "//*/td[@id='contentwrapper']/div[text()[contains(.,'Динамические ссылки')]]";
  private final String GET_FRAME = "//*/frame[@name='frminfo']";// Получить фрейм просмотра
  private final String MENU = "//*/div[@id='mnu_tblock1']";// Меню
  private final String TABLE_SURFING = "//*/table[@class='work-serf']";
  private final String CONTROL_3_COLUMN = "./tbody/tr/td[3]";
  private final String CLICK_LINK = "./tbody/tr/td[2]/div/a";
  private final String CLICK_VIEW = "./tbody/tr/td[2]/div/a[text()[contains(.,'Приступить к просмотру')]]";
  private final String FORM_INPUT = "//*/form/input";
  private final String FORM_BUTTON = "//*/form/button";
  private final String GO_TRANSITION = "//*/div[@id='mnu_tblock1']/a[text()[contains(.,'Переходы')]]";
  private final String TRANSITION_CONTROL = "//*/td[@id='contentwrapper']/div[text()[contains(.,'Переходы')]]";
  private final String TABLE_TRANSITION = "//*/table[@class='work-serf']/tbody/tr/td[2]/div/a";



  public SeoClubHandler(WebDriver webDriver, User user) throws AWTException {
    this.WEB_DRIVER = webDriver;
    this.ACTIONS = new Actions(webDriver, Duration.ofSeconds(1));
    this.processing = new Processing(webDriver, ACTIONS);
    this.E_MAIL = user.getLogin();
    this.PASSWORD = user.getPassword();
    this.URL = "https://seoclub.su/login";
  }

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

    if (!goToPaidTransitions()) {
      WEB_DRIVER.quit();
      return;
    }

    if (!goToPaidSurfing()) {
      WEB_DRIVER.quit();
      return;
    }
//=============================
    if (!goToPaidTransitions()) {
      WEB_DRIVER.quit();
      return;
    }

    if (!goToPaidSurfing()) {
      WEB_DRIVER.quit();
      return;
    }
//=============================

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
    WebElement webElement = processing.getElementByXpathWithCount(GO_SURFING, 30);
    if (webElement == null) {
      return false;
    }
    try {
      ACTIONS.click(webElement).perform();
      pause(2000);
      WebElement checkPage = processing.getElementByXpathWithCount(SURFING_CONTROL, 30);
      if (checkPage == null){
        return false;
      }
      List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath(TABLE_SURFING));
      List<WebElement> elements = new ArrayList<>();
      if (dataElements.isEmpty()){
        return true;
      }
      for (WebElement element : dataElements){
        if (element.getText().isBlank()){
          continue;
        }
        WebElement el = processing.getElementByXpathWithCount(element, CONTROL_3_COLUMN, 0);
        if (el == null){
          continue;
        }
        elements.add(element);
      }
      if (elements.isEmpty()){
        return true;
      }
      for (WebElement element : elements){
        WebElement el = processing.getElementByXpathWithCount(element, CLICK_LINK, 1);
        if (el == null){
          continue;
        }
        ACTIONS.click(el).perform();
        try{
          el = processing.getElementByXpathWithCount(element, CLICK_VIEW, 30);
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
          moveSlider();
          processing.closeAllTabs(30);
        }
      }
    } catch (TimeoutException e) {
      return false;
    }
    return true;
  }

  private boolean moveSlider(){
    WebElement webElement = processing.getElementByXpathWithCount(GET_FRAME, 30);
    if (webElement == null){
      return false;
    }
    WEB_DRIVER.switchTo().frame(webElement);
    webElement = processing.getElementByXpathWithCount(FORM_INPUT, 200);
    if (webElement == null){
      return false;
    }
    String max = webElement.getAttribute("max");
    JavascriptExecutor javascriptExecutor = (JavascriptExecutor) WEB_DRIVER;
    javascriptExecutor.executeScript("arguments[0].setAttribute('value', '" + max +"');", webElement);
    pause(200);
    javascriptExecutor.executeScript("arguments[0].setAttribute('wfd-id', 'id0');", webElement);
    webElement = processing.getElementByXpathWithCount(FORM_BUTTON, 5);
    ACTIONS.click(webElement).perform();
    pause(5000);
    return true;
  }

  private boolean goToPaidTransitions() {
    processing.scrollPageDown(500);
    if (!expandTheListToEarn()){
      return false;
    }
    WebElement webElement = processing.getElementByXpathWithCount(GO_TRANSITION, 30);
    if (webElement == null) {
      return false;
    }
    try {
      ACTIONS.click(webElement).perform();
      pause(2000);
      WebElement checkPage = processing.getElementByXpathWithCount(TRANSITION_CONTROL, 30);
      if (checkPage == null){
        return false;
      }
      List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath(TABLE_TRANSITION));
      if (dataElements.isEmpty()){
        return true;
      }
      for (WebElement element : dataElements){
        if (element.getAttribute("target").equals("_blank")){
          continue;
        }
        ACTIONS.click(element).perform();
        pause(4000);
        if (processing.isMore1TabsWithCount(30)){
          processing.waitTimeOnTitlePage("Просмотр засчитан!", 200);
          processing.closeAllTabs(30);
        }
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
    WebElement webElement = processing.getElementByXpathWithCount(INPUT_USERNAME, 30);
    if (webElement == null) {
      return false;
    }
    ACTIONS.sendKeys(webElement, E_MAIL).perform();
    webElement = processing.getElementByXpathWithCount(INPUT_PASSWORD, 30);
    if (webElement == null) {
      return false;
    }
    ACTIONS.sendKeys(webElement, PASSWORD).perform();
    if (resolveCaptcha()) {
      webElement = processing.getElementByXpathWithCount(ENTER_BUTTON, 30);
      ACTIONS.click(webElement).perform();
      pause(5000);
      return true;
    }
    return false;
  }

  private boolean resolveCaptcha() {
    boolean isSuccess = false;
    WebElement webElement = processing.getElementByXpathWithCount(OUT_CAPCHA_TITLE, 30);
    if (webElement == null) {
      return false;
    }
    String nameImage = webElement.getText().trim();
    nameImage = nameImage.substring(nameImage.lastIndexOf(" ") + 1);
    List<WebElement> webElements = WEB_DRIVER.findElements(By.xpath(TABLE_CAPCHA));
    SQLiteProvider sqLiteProvider = SQLiteProvider.getInstance();
    for (WebElement element : webElements){
      String content = element.getAttribute("style");
      if (sqLiteProvider.checkImageInSeoClubTable(nameImage, content)) {
        ACTIONS.click(element).perform();
        isSuccess = true;
        pause(500);
      }
    }
    return isSuccess;
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
