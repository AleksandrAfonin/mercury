package ru.mycompany.awt;

import org.openqa.selenium.*;
import java.util.Date;
import java.util.List;

public class ProfitCentrHandler implements Handler {
  private final String sitename;
  private final String browser;
  private WebDriver WEB_DRIVER;
  private User user;
  private Processing processing;
  private final String URL;

  public ProfitCentrHandler() {
    this.sitename = "profitcentr";
    this.URL = SQLiteProvider.getInstance().getUrlSite(sitename);
    this.browser = SQLiteProvider.getInstance().getBrowser(sitename);
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

    if (!goToPaidTransitions()) {
      WEB_DRIVER.quit();
      return;
    }

    if (!goToPaidSurfing()) {
      WEB_DRIVER.quit();
      return;
    }
//================================
    if (!goToPaidTransitions()) {
      WEB_DRIVER.quit();
      return;
    }

    if (!goToPaidSurfing()) {
      WEB_DRIVER.quit();
      return;
    }
//================================
//    if (!goToYouTube()) {
//      WEB_DRIVER.quit();
//      return;
//    }

    pause(5000);
    WEB_DRIVER.quit();
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

  private boolean isMore1TabsWithCount(int count) {
    for (int i = 0; i < count; i++) {
      pause(2000);
      if (WEB_DRIVER.getWindowHandles().toArray().length > 1){
        return true;
      }
    }
    return false;
  }

  /**
   * Переход на страницу переходов
   *
   * @return true - успех
   * false - провал
   */
  private boolean goToPaidTransitions() {
    if (!expandTheListToEarn()){
      return false;
    }
    WebElement webElement = getElementByXpathWithCount("//*/a[text()[contains(.,'Переходы')]]", 30);
    if (webElement == null) {
      System.out.println("Оплачиваемые переходы не найдены");
      return false;
    }
    try {
      processing.clickElement(webElement);
      pause(1000);
      String[] texts = new String[]{"Посещение сайтов"};
      int pageNum = checkPages(texts, 30);
      if (pageNum != 0){
        return false;
      }
      List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath("//*/tbody/tr/td/div/a"));
      if (dataElements.isEmpty()){
        return true;
      }
      for (WebElement element : dataElements){
        processing.clickElement(element);
        pause(3000);
        if (isMore1TabsWithCount(5)){
          waitTime("Просмотр засчитан!", 300);
          closeAllTabs();
        }
      }
    } catch (TimeoutException e) {
      return false;
    }
    return true;
  }

  private boolean goToPaidSurfing(){
    if (!expandTheListToEarn()){
      return false;
    }
    WebElement webElement = getElementByXpathWithCount("//*/a[text()[contains(.,'Серфинг сайтов')]]", 30);
    if (webElement == null) {
      System.out.println("Серфинг сайтов не найдены");
      return false;
    }
    try {
      processing.clickElement(webElement);
      pause(500);
      String[] texts = new String[]{"Серфинг сайтов"};
      int pageNum = checkPages(texts, 30);
      if (pageNum != 0){
        return false;
      }
      List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath("//*/tbody/tr/td/div/a"));
      if (dataElements.isEmpty()){
        return true;
      }
      for (WebElement element : dataElements){
        String content = element.getText();
        if (content.isEmpty()){
          continue;
        }
        WebElement parent = element.findElement(By.xpath(".."));
        pause(500);
        processing.clickElement(element);
        pause(4000);
        try{
          WebElement child = parent.findElement(By.tagName("a"));
          content = child.getText();
          if (!content.equals("Приступить к просмотру")){
            continue;
          }
          processing.clickElement(child);
          pause(4000);
        }catch (Exception e){
          continue;
        }
        if (isMore1TabsWithCount(5)){
          Object[] windowsHandles = WEB_DRIVER.getWindowHandles().toArray();
          WEB_DRIVER.switchTo().window((String) windowsHandles[1]);
          moveSlider();
          closeAllTabs();
        }
      }
    } catch (TimeoutException e) {
      return false;
    }
    return true;
  }

  private boolean goToYouTube(){
    if (!expandTheListToEarn()){
      return false;
    }
    WebElement webElement = getElementByXpathWithCount("//*/a[text()[contains(.,'YouTube')]]", 30);
    if (webElement == null) {
      System.out.println("YouTube не найдены");
      return false;
    }
    try {
      processing.clickElement(webElement);
      pause(1000);
      String[] texts = new String[]{"Пройти проверку"};
      int pageNum = checkPages(texts, 30);
      if (pageNum != 0){
        return false;
      }
      if (!resolveCaptchaYouTube()){
        return false;
      }
      webElement = getElementByXpathWithCount("//*/button/span[@class='btn green']", 30);
      processing.clickElement(webElement);
      pause(4000);
      int count = checkPages(new String[]{"Просмотр видео"}, 30);
      if (count != 0){
        return false;
      }
      List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath("//*/tbody/tr/td/div/span[1]"));
      if (dataElements.isEmpty()){
        return true;
      }
      for (WebElement element : dataElements){
        if (element.getText().isEmpty()){
          continue;
        }
        WebElement parent = element.findElement(By.xpath(".."));
        pause(500);
        processing.clickElement(element);
        pause(4000);
        try {
          WebElement child = parent.findElement(By.xpath("./div/span"));
          if (!child.getText().equals("Приступить к просмотру")) {
            continue;
          }
          processing.clickElement(child);
          pause(4000);
        }catch (Exception e){
          continue;
        }
        if (isMore1TabsWithCount(5)){
          waitTime("Просмотр засчитан!", 300);
          closeAllTabs();
        }
      }
    } catch (TimeoutException e) {
      return false;
    }
    return true;
  }

  private boolean moveSlider(){
    WebElement webElement = getElementByXpathWithCount("//*/frame[@name='frminfo']", 10);
    if (webElement == null){
      return false;
    }
    WEB_DRIVER.switchTo().frame(webElement);
    int number = checkPages(new String[]{"передвиньте"}, 300);
    if (number < 0){
      return false;
    }
    webElement = getElementByXpathWithCount("//*/form/input", 5);
    String max = webElement.getAttribute("max");
    JavascriptExecutor javascriptExecutor = (JavascriptExecutor) WEB_DRIVER;
    javascriptExecutor.executeScript("arguments[0].setAttribute('value', '" + max +"');", webElement);
    pause(200);
    javascriptExecutor.executeScript("arguments[0].setAttribute('wfd-id', 'id0');", webElement);
    pause(1000);
    webElement = getElementByXpathWithCount("//*/form/button", 5);
    processing.clickElement(webElement);
    pause(5000);
    return true;
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

  /**
   * Авторизация на сайте
   *
   * @return true - успех
   * false - провал
   */
  private boolean authorization() {
    String[] texts = new String[]{"Вход в аккаунт", "Моя статистика"};
    int pageNum = checkPages(texts, 30);
    switch (pageNum) {
      case 0 -> {
        System.out.println("На странице авторизации");
        if (enterAccountData()) {
          texts = new String[]{"Моя статистика"};
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

  /**
   * Загрузка браузера и открытие стартовой страницы
   *
   * @return true - успешно
   * false - провал
   */
  private boolean start() {
    try {
      WEB_DRIVER.get(URL);
      System.out.println(WEB_DRIVER.getTitle() + " Стартовая страница загружена");
      return true;
    } catch (TimeoutException e) {
      return false;
    }
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

  /**
   * Решить капчу
   *
   * @return true - капча решена
   * false - капча не поддалась
   */
  private boolean resolveCaptcha() {
    boolean isSuccess = false;
    WebElement webElement = getElementByXpathWithCount("//*/div[@class='out-capcha-title']", 30);
    if (webElement == null) {
      return false;
    }
    String nameImage = webElement.getText().trim();
    nameImage = nameImage.substring(nameImage.lastIndexOf(" ") + 1);

    List<WebElement> webElements = WEB_DRIVER.findElements(By.xpath("//*/tr/td[@colspan='2']/div[@class='out-capcha']/label[@class='out-capcha-lab']"));
    SQLiteProvider sqLiteProvider = SQLiteProvider.getInstance();

    for (int i = 0; i < 6; i++) {
      String content = webElements.get(i).getAttribute("style");
      if (sqLiteProvider.checkImageInProfitCentrTable(nameImage, content)) {
        processing.clickElement(webElements.get(i));
        isSuccess = true;
        pause(500);
      }
    }
    return isSuccess;
  }

  private boolean resolveCaptchaYouTube(){
    boolean isSuccess = false;
    WebElement webElement = getElementByXpathWithCount("//*/div[@class='out-capcha-title']", 30);
    if (webElement == null) {
      return false;
    }
    String nameImage = webElement.getText().trim();
    nameImage = nameImage.substring(nameImage.lastIndexOf(" ") + 1);

    List<WebElement> webElements = WEB_DRIVER.findElements(By.xpath("//*/form/div[@class='out-capcha']/label[@class='out-capcha-lab']"));
    SQLiteProvider sqLiteProvider = SQLiteProvider.getInstance();

    for (int i = 0; i < 6; i++) {
      String content = webElements.get(i).getAttribute("style");
      if (sqLiteProvider.checkImageInProfitCentrTable(nameImage, content)) {
        processing.clickElement(webElements.get(i));
        isSuccess = true;
        pause(500);
      }
    }
    return isSuccess;
  }

  /**
   * Авторизовываемся на сайте своими данными
   *
   * @return true - успех
   * false - провал
   */
  private boolean enterAccountData() {
    WebElement webElement = getElementByXpathWithCount("//*/input[@class='login_vh'][@name='username']", 30);
    if (webElement == null) {
      return false;
    }
    processing.sendKeys(webElement, user.getLogin());
    webElement = getElementByXpathWithCount("//*/input[@class='login_vh'][@name='password']", 30);
    if (webElement == null) {
      return false;
    }
    processing.sendKeys(webElement, user.getPassword());
    if (resolveCaptcha()) {
      webElement = getElementByXpathWithCount("//*/span[@class='btn_big_green']", 30);
      processing.clickElement(webElement);
      pause(5000);
      return true;
    }
    return false;
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

  /**
   * Пауза в выполнении
   *
   * @param millis - миллисекунды
   */
  private void pause(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException ignored) {
    }
  }

}
