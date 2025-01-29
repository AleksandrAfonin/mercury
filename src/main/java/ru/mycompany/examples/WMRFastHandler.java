package ru.mycompany.examples;

import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WMRFastHandler {
  private final WebDriver WEB_DRIVER;
  private final String LOGIN;    // alsupp@yandex.ru
  private final String PASSWORD;  // 19b650660b
  private final Actions ACTIONS;
  private final String SITE_URL;
  private final Processing processing;
  private final BufferedImage[] pas;
  private String mainTabHandle;
  private String mainTabTitle;
  private String currentTabHandle;

  public WMRFastHandler(WebDriver webDriver, User user) throws AWTException, IOException {
    this.WEB_DRIVER = webDriver;
    this.processing = new Processing(webDriver);
    this.LOGIN = user.getLogin();
    this.PASSWORD = user.getPassword();
    this.ACTIONS = new Actions(webDriver, Duration.ofSeconds(1));
    this.SITE_URL = "https://wmrfast.com";

    File directory1 = new File(new File(".", "sprites"), "wmrfast");
    if (webDriver instanceof ChromeDriver) {
      directory1 = new File(directory1, "chrome");
    }
    if (webDriver instanceof EdgeDriver) {
      directory1 = new File(directory1, "edge");
    }
    this.pas = init(directory1, "litera");
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
    System.out.println(date + "   Account: " + LOGIN);

    if (!start()) {
      WEB_DRIVER.quit();
      return;
    }
    pause(5000);

    if (!authorization()) {
      WEB_DRIVER.quit();
      return;
    }

    if (!goToPaidTransitions()) {
      WEB_DRIVER.quit();
      return;
    }

//    WebElement webElement = getElementFromList(new String[]{"Видео YouTube"}, 30);
//    if (webElement == null) {
//      WEB_DRIVER.quit();
//      return;
//    }
//    ACTIONS.click(webElement).perform();
//    webElement = getElementFromList(new String[]{"Просмотры видео YouTube (оплачиваются)"}, 30);
//    if (webElement == null) {
//      WEB_DRIVER.quit();
//      return;
//    }
//
//    while (true) {
//      if (!performingClicks(getDataForClicks())) {
//        WEB_DRIVER.quit();
//        return;
//      }
//      WEB_DRIVER.navigate().refresh();
//    }


//
//    if (!goToPaidVisits()) {
//      WEB_DRIVER.quit();
//      return;
//    }
//

//
//    System.out.println("Закончили обход");
//    pause(10000);
//    WEB_DRIVER.quit();

    pause(5000);
    WEB_DRIVER.quit();

  }

  private boolean goToPaidTransitions() {
    if (!expandTheListToEarn()) {
      return false;
    }
    WebElement webElement = getElementByXpathWithCount("//*/a[text()[contains(.,'Переходы на сайты')]]", 30);
    if (webElement == null) {
      System.out.println("Оплачиваемые переходы не найдены");
      return false;
    }
    System.out.println("Переход на 'Переходы на сайты'");
    try {
      ACTIONS.click(webElement).perform();
      pause(1000);
      while (true) {
        String[] texts = new String[]{"Переходы по ссылкам (оплачиваются)"};
        int pageNum = checkPages(texts, 10);
        if (pageNum != 0) {
          return false;
        }
        List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath("//*/form[@name='serfo']"));
        if (dataElements.isEmpty()) {
          break;
        }
        try {
          for (WebElement element : dataElements) {
            WebElement link = element.findElement(By.xpath("./table/tbody/tr/td[2]/a"));
            ACTIONS.click(link).perform();
            pause(3000);
            if (isMore1TabsWithCount(5)) {
              waitTime("Просмотр засчитан!", 100);
              closeAllTabs();
              pause(7000);
            }
          }
        } catch (JavascriptException e) {
          System.out.println("JS :) 146");
        }
        WEB_DRIVER.get(WEB_DRIVER.getCurrentUrl());
        pause(1000);
      }
    } catch (TimeoutException e) {
      return false;
    }
    return true;
  }

  private boolean waitTime(String titleName, int maxCount) {
    for (int i = 0; i < maxCount; i++) {
      pause(2000);
      String title = WEB_DRIVER.getTitle();
      if (titleName.equals(title)) {
        return true;
      }
    }
    return false;
  }

  private boolean isMore1TabsWithCount(int count) {
    for (int i = 0; i < count; i++) {
      pause(2000);
      if (WEB_DRIVER.getWindowHandles().toArray().length > 1) {
        return true;
      }
    }
    return false;
  }

  private boolean expandTheListToEarn() {
    WebElement webElement = getElementByXpathWithCount("//*/div[@id='help_zar_menu']/div[@id='m_bl2']", 10);
    if (webElement == null) {
      System.out.println("Нет списка раскрывающегося");
      return false;
    }
    JavascriptExecutor js = (JavascriptExecutor) WEB_DRIVER;
    js.executeScript("arguments[0].setAttribute('style', 'display: block;');", webElement);
    pause(1000);
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

  /**
   * Проверка, существует ли вкладка с title
   *
   * @param titles - строковый массив возможных вариантов
   * @param title  - title текущей вкладки
   * @return true - существует
   * false - не существует
   */
  private boolean isExistTab(String[] titles, String title) {
    for (String s : titles) {
      //System.out.println(s + " :: " + title);
      if (title.matches(s)) {
        //System.out.println("True");
        return true;
      }
      //System.out.println("False");
    }
    return false;
  }

  private boolean performingClicks(Map<WebElement, Integer> links) {
    if (links == null || links.isEmpty()) {
      return true;
    }
    try {
      System.out.println("Начинаем переходы:");
      //((JavascriptExecutor) WEB_DRIVER).executeScript("document.title = 'AnchorTab'");
      mainTabHandle = WEB_DRIVER.getWindowHandle();
      mainTabTitle = WEB_DRIVER.getTitle();
      int count = 0;
      for (Map.Entry<WebElement, Integer> entry : links.entrySet()) {
        if (!closeAllTabs()) {
          return false;
        }
        WEB_DRIVER.switchTo().window(mainTabHandle);
        long timer = entry.getValue();
        try {
          ACTIONS.click(entry.getKey()).perform();
          if (!beginVisit()) {
            continue;
          }
        } catch (JavascriptException ignored) {
        }
        //pause(timer);
        System.out.print("\rВыполнено: " + ++count + " из " + links.size());

      }

      if (!closeAllTabs()) {
        return false;
      }
      WEB_DRIVER.switchTo().window(mainTabHandle);
      System.out.println();
      return true;
    } catch (TimeoutException e) {
      System.out.println("performingClicks: TimeoutException");
      return false;
    }
  }

  private boolean isClickPlay(int count) {
    System.out.println("begin isClickPlay");
    Object[] windowHandles = WEB_DRIVER.getWindowHandles().toArray();
    WEB_DRIVER.switchTo().window((String) windowHandles[1]);
    currentTabHandle = WEB_DRIVER.getWindowHandle();
    WebElement frame = switchFrameWithTimer(30);
    if (frame == null) {
      return false;
    }
    while (count-- > 0) {
      pause(2000);
      try {
        WebElement webElement = WEB_DRIVER.findElement(By.xpath("//button[@aria-label='Смотреть']"));
        //button[@title="Смотреть"]
        //WebElement webElement = WEB_DRIVER.findElement(By.xpath("//*/DIV[@id='player']/DIV/DIV/BUTTON"));
        System.out.println("Click Ok isClickPlay");
        ACTIONS.click(webElement).perform();
        return true;
      } catch (NoSuchElementException e) {
        System.out.println("No element isClickPlay");
      }
    }
    //ACTIONS.click(frame).perform();
    return false;
  }

  private WebElement switchFrameWithTimer(int countLoops) {
    while (countLoops-- > 0) {
      pause(2000);
      try {
        WebElement webElement = WEB_DRIVER.findElement(By.id("v_y"));
        System.out.println("Frame Ok");
        WEB_DRIVER.switchTo().frame(webElement);
        return webElement;
      } catch (NoSuchElementException e) {
        System.out.println("Frame not found");
      }
    }
    return null;
  }

  private boolean beginVisit() {
//    WebElement webElement = getElementStartVideo(300);
//    if (webElement == null){
//      return false;
//    }
    pause(10000); // Для открытия вкладки просмотра
    if (!isClickPlay(100)) {
      return false;
    }
    WEB_DRIVER.switchTo().window(currentTabHandle);
    pause(10000);
    WebElement webElement = getElementFromList(new String[]{"Продолжить"}, 100);
    if (webElement == null) {
      return false;
    }
    ACTIONS.click(webElement).perform();
    pause(5000);
    return true;
  }

  /**
   * Получить мапу кликабельных ссылок
   *
   * @return - мапа
   */
  private Map<WebElement, Integer> getDataForClicks() {
    List<WebElement> tr;
    Map<WebElement, Integer> links = new HashMap<>();
    int size;
    int count = 2;
    while (count-- > 0) {
      try {
        pause(10000);
        tr = WEB_DRIVER.findElements(By.xpath("//tr"));
        for (WebElement webElement : tr) {
          try {
            WebElement link = webElement.findElement(By.xpath("./td/a[@class='serf_hash']"));
            //WebElement time = webElement.findElement(By.xpath("./td/span[@class='clickprice']"));
            //String timeText = time.getText();
            //int tm = Integer.parseInt(timeText.substring(0, timeText.indexOf(" ")));
            links.put(link, 1000);
          } catch (NoSuchElementException ignored) {
          }
        }
        size = links.size();
        System.out.println("Размер мапы: " + size);
        break;
      } catch (TimeoutException e) {
        System.out.println("Ну и что, что TimeoutException");
      }
    }
    return links;
  }

  /**
   * Авторизация на сайте
   *
   * @return true - успех
   * false - провал
   */
  private boolean authorization() {
    WebElement webElement = WEB_DRIVER.findElement(By.id("logbtn"));
    if (webElement == null) {
      System.out.println("Искали: " + "logbtn");
      System.out.println("Ничего не нашли !");
      return false;
    }
    ACTIONS.click(webElement).perform();
    pause(1000);

    webElement = WEB_DRIVER.findElement(By.id("vhusername"));
    ACTIONS.sendKeys(webElement, LOGIN).perform();
    pause(1000);

    webElement = WEB_DRIVER.findElement(By.id("vhpass"));
    ACTIONS.sendKeys(webElement, PASSWORD).perform();
    pause(1000);

    processing.refreshScreen();
    Point point = processing.find(pas, true, 5);
    if (point == null) {
      System.out.println("Null !");
      WEB_DRIVER.quit();
      return false;
    }
    processing.refreshScreen(new Rectangle(point.x + 57, point.y + 39, 56, 15));
    String number = processing.resolveWMRCaptcha();
    System.out.println("Number: " + number);
    webElement = WEB_DRIVER.findElement(By.id("cap_text"));
    ACTIONS.sendKeys(webElement, number).perform();
    pause(2000);

    webElement = WEB_DRIVER.findElement(By.id("vhod1"));
    ACTIONS.sendKeys(webElement, number).perform();
    pause(2000);

    String[] texts = new String[]{"СТАТИСТИКА ПОЛЬЗОВАТЕЛЯ"};
    int pageNum = checkPages(texts, 30);
    return pageNum == 0;
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


  /**
   * Получить какой либо элемент по тексту из массива образцов текста
   *
   * @param linksText - массив с образцами текста
   * @return WebElement - найденный элемент
   * null - ничего не нашли
   */
  private WebElement getElementFromList(String[] linksText, int count) {
    if (linksText == null || linksText.length < 1) {
      return null;
    }
    WebElement element;
    while (count-- > 0) {
      pause(2000);
      for (String s : linksText) {
        if (s.isBlank()) {
          continue;
        }
        try {
          element = WEB_DRIVER.findElement(By.xpath("//*[text()[contains(.,'" + s + "')]]"));
          System.out.println("Найдено: '" + element.getText() + "'. ");
          return element;
        } catch (NoSuchElementException e) {
          System.out.println("Элемент '" + s + "' не найден");
        } catch (TimeoutException ignored) {
        }
      }
    }
    System.out.println("Ничего не нашел !");
    return null;
  }

  private WebElement getElementStartVideo(int count) {
    WebElement element;
    while (count-- > 0) {
      pause(2000);
      try {
        element = WEB_DRIVER.findElement(By.xpath("//*/span[@id='tt']"));
        System.out.println("Найдено: //*/span[@id='tt']");
        return element;
      } catch (NoSuchElementException e) {
        System.out.println("Элемент '//*/span[@id='tt']' не найден");
      }

    }
    System.out.println("Ничего не нашел !");
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

  /**
   * Загрузка браузера и открытие стартовой страницы
   *
   * @return true - успешно
   * false - провал
   */
  private boolean start() {
    WEB_DRIVER.manage().window().maximize();
    try {
      WEB_DRIVER.get(SITE_URL);
      System.out.println(WEB_DRIVER.getTitle() + " Стартовая страница загружена");
      return true;
    } catch (TimeoutException e) {
      return false;
    }
  }


}
