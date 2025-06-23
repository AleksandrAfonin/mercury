package ru.mycompany.awt;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.Duration;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class SeoFastHandler implements Handler {
  private final WebDriver WEB_DRIVER;
  private final String E_MAIL;    // alsupp@yandex.ru
  private final String PASSWORD;  // 19b650660b
  private final Actions ACTIONS;
  private final String URL;
  private final File DIRECTORY;

  public SeoFastHandler(WebDriver webDriver, User user) {
    this.WEB_DRIVER = webDriver;
    this.E_MAIL = user.getLogin();
    this.PASSWORD = user.getPassword();
    this.ACTIONS = new Actions(webDriver, Duration.ofSeconds(1));
    this.URL = "https://seo-fast.ru/login";
    this.DIRECTORY = new File(".", "captcha");
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

    if (!goToPaidTransitions()) {
      WEB_DRIVER.quit();
      return;
    }



    pause(5000);
    WEB_DRIVER.quit();
  }

  private boolean goToPaidTransitions() {
    WebElement webElement = getElementByXpathWithCount("//*/a[text()[contains(.,'Оплачиваемые Посещения')]]", 30);
    if (webElement == null) {
      System.out.println("Оплачиваемые переходы не найдены");
      return false;
    }
    System.out.println("Переход на 'Переходы'");
    try {
      ACTIONS.click(webElement).perform();
      pause(2000);
      String[] texts = new String[]{"Здесь зарабатывают на посещениях сайтов и страниц"};
      int pageNum = checkPages(texts, 30);
      if (pageNum != 0){
        return false;
      }
      List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath("//*/tbody/tr/td[2]/div/div/a[@class='surf_ckick']"));
      if (dataElements.isEmpty()){
        return true;
      }
      for (WebElement element : dataElements) {
        String content = element.getText();
        if (content.isEmpty()) {
          continue;
        }
        ACTIONS.click(element).perform();
        pause(4000);
        if (!isGetTimeTab(50)){
          continue;
        }
        for (int i = 0; i < 100; i++) {
          pause(3000);
          if (Objects.requireNonNull(WEB_DRIVER.getTitle()).trim().equals("Готово")) {
            //pause(1000);
            closeAllTabs();
            break;
          }
        }
      }
    } catch (TimeoutException e) {
      return false;
    }
    return true;
  }

  private boolean isGetTimeTab(int num){
    int count = num;
    while (count-- > 0) {
      pause(600);
      if (getTimeTab()) {
        return true;
      }
      try {
        WebElement child = WEB_DRIVER.findElement(By.xpath("//*/div[text()[contains(.,'Приступить к просмотру')]]"));
        ACTIONS.click(child).perform();
        pause(2000);
        if (getTimeTabWithCount(50)) {
          return true;
        }
      } catch (NoSuchElementException ignored) {
      }
    }
    return false;
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

  private boolean closeAllTabs() {
    int count = 30;
    while (count-- > 0) {
      Object[] windowsHandles = WEB_DRIVER.getWindowHandles().toArray();
      if (windowsHandles.length == 1) {
        return true;
      }
      WEB_DRIVER.switchTo().window((String) windowsHandles[0]);
      pause(2000);

//      for (int i = 1; i < windowsHandles.length; i++) {
//        try {
//          WEB_DRIVER.switchTo().window((String) windowsHandles[i]);
//          WEB_DRIVER.close();
//        } catch (WebDriverException ignored) {
//        }
//      }

      for (int i = windowsHandles.length - 1; i > 0; i--) {
        try {
          WEB_DRIVER.switchTo().window((String) windowsHandles[i]);
          WEB_DRIVER.close();
          pause(100);
        } catch (WebDriverException ignored) {
        }
      }
      WEB_DRIVER.switchTo().window((String) windowsHandles[0]);
      pause(1000);
    }
    return false;
  }

  private boolean getTimeTab() {
    Object[] windowsHandles = WEB_DRIVER.getWindowHandles().toArray();
    if (windowsHandles.length > 1) {
      WEB_DRIVER.switchTo().window((String) windowsHandles[1]);
      return true;
    }
    return false;
  }

  private boolean getTimeTabWithCount(int number) {
    int count = number;
    while (count-- > 0) {
      pause(200);
      if (getTimeTab()) {
        return true;
      }
    }
    return false;
  }

  /**
   * Авторизация на сайте
   *
   * @return true - успех
   * false - провал
   */
  private boolean authorization() {
    if (!enterAccountData()){
      return false;
    }
    String[] listElementsText = new String[]{"Моя статистика"};
    WebElement webElement = getElementFromList(listElementsText);
    return webElement != null;
  }

  /**
   * Загрузка браузера и открытие стартовой страницы
   *
   * @return true - успешно
   * false - провал
   */
  private boolean start() {
    //WEB_DRIVER.manage().window().maximize();
    try {
      WEB_DRIVER.get(URL);
      System.out.println(WEB_DRIVER.getTitle() + " Стартовая страница загружена");
      return true;
    } catch (TimeoutException e) {
      return false;
    }
  }

  private WebElement singleGetElementByText(String text, String tag) {
    if (text == null || text.isBlank() || tag == null || tag.isBlank()) {
      return null;
    }
    try {
      return WEB_DRIVER.findElement(By.xpath("//" + tag + "[text()[contains(.,'" + text + "')]]"));
    } catch (NoSuchElementException e) {
      //System.out.println("Элемент '" + text + "' не найден");
      return null;
    }
  }

  private WebElement getElementByText(String text, String tag, int number) {
    if (text == null || text.isBlank() || tag == null || tag.isBlank()) {
      return null;
    }
    WebElement element;
    int count = number;
    while (count-- > 0) {
      pause(2000);
      element = singleGetElementByText(text, tag);
      if (element != null) {
        return element;
      }
    }
    System.out.println("getElementByText: Ничего не нашел !");
    return null;
  }

  /**
   * Решить капчу
   *
   * @param Directory - директория с файлами данных картинок
   * @return true - капча решена
   * false - капча не поддалась
   */
  private boolean resolveCaptcha(File Directory) {
    boolean isSuccess = false;
    WebElement webElement = getElementByText("изображения c", "*", 30);
    if (webElement == null) {
      return false;
    }
    String name = webElement.getText();
    name = name.substring(name.lastIndexOf(" ") + 1) + ".txt";
    name = name.substring(name.lastIndexOf(" ") + 1);

    File file = new File(Directory, name);
    String picData;
    try (BufferedReader br = new BufferedReader(new FileReader(file))) {
      picData = br.readLine();
      while (picData != null) {
        try {
          WebElement webElementPic = WEB_DRIVER.findElement(By.xpath("//*[@style='" + picData + "']"));
          System.out.println("Нашел: " + webElementPic.getTagName());
          webElementPic.findElement(By.tagName("input"));
          ACTIONS.click(webElementPic).perform();
          isSuccess = true;
          picData = br.readLine();
          pause(500);
        } catch (NoSuchElementException e) {
          picData = br.readLine();
        }
      }
    } catch (IOException e) {
      System.out.println("Ошибка считывания из файла");
      return false;
    }
    if (!isSuccess) {
      System.out.println("Картинки не распознаны !");
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
    WebElement webElement = getElementByIdWithCount("logusername", 30);
    if (webElement == null) {
      return false;
    }
    ACTIONS.sendKeys(webElement, E_MAIL).perform();
    webElement = getElementByIdWithCount("logpassword", 30);
    if (webElement == null) {
      return false;
    }
    ACTIONS.sendKeys(webElement, PASSWORD).perform();
    webElement = getElementByClassNameWithCount("sf_button", 30);
    if (webElement == null) {
      return false;
    }
    ACTIONS.click(webElement).perform();
    return true;
  }

  private WebElement getElementByClassNameWithCount(String className, int count) {
    WebElement webElement;
    while (count-- > 0) {
      pause(2000);
      try {
        webElement = WEB_DRIVER.findElement(By.className(className));
        return webElement;
      } catch (NoSuchElementException ignored) {
      }
    }
    return null;
  }

  private WebElement getElementByIdWithCount(String id, int count) {
    WebElement webElement;
    while (count-- > 0) {
      pause(2000);
      try {
        webElement = WEB_DRIVER.findElement(By.id(id));
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

  /**
   * Получить какой либо элемент по тексту из массива образцов текста
   *
   * @param linksText - массив с образцами текста
   * @return WebElement - найденный элемент
   * null - ничего не нашли
   */
  private WebElement getElementFromList(String[] linksText) {
    if (linksText == null || linksText.length < 1) {
      return null;
    }
    WebElement element;
    int count = 30;
    while (count-- > 0) {
      pause(2000);
      for (String s : linksText) {
        if (s.isBlank()) {
          continue;
        }
        try {
          element = WEB_DRIVER.findElement(By.xpath("//*[text()[contains(.,'" + s + "')]]"));
          return element;
        } catch (NoSuchElementException e) {
        }
      }
    }
    return null;
  }

}
