package ru.mycompany.awt;

import org.openqa.selenium.*;

import java.awt.*;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Date;
import java.util.List;

public class SarSeoHandler implements Handler {
    private final String sitename;
    private String browser;
    private WebDriver WEB_DRIVER;
    private User user;
    private final String URL;
    private Processing processing;

    private final List<BufferedImage> ravno;
    private final List<BufferedImage> closeActivePage;
    private final List<BufferedImage> closePassivePage;

    public SarSeoHandler() throws AWTException {
        this.sitename = "sarseo";
        this.URL = SQLiteProvider.getInstance().getUrlSite(sitename);
        this.browser = SQLiteProvider.getInstance().getBrowser(sitename);
        this.ravno = SQLiteProvider.getInstance().getSprites(sitename, browser, "ravno");
        this.closeActivePage = SQLiteProvider.getInstance().getSprites(sitename, browser, "actclose");
        this.closePassivePage = SQLiteProvider.getInstance().getSprites(sitename, browser, "pasclose");
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


    private boolean goToPaidSurfing() {
        scrollPageDown("500");
        if (!expandTheListToEarn()) {
            return false;
        }
        WebElement webElement = getElementByXpathWithCount("//*/div[@id='mnu_tblock1']/a[text()[contains(.,'Сёрфинг')]]", 30);
        if (webElement == null) {
            System.out.println("Серфинг сайтов не найдены");
            return false;
        }
        System.out.println("Переход на 'Серфинг сайтов'");
        try {
            processing.clickElement(webElement);
            pause(2000);
            WebElement checkPage = getElementByXpathWithCount("//*/td[@id='contentwrapper']/div[text()[contains(.,'Динамические ссылки')]]", 30);
            if (checkPage == null) {
                return false;
            }
            List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath("//*/table[@class='work-serf']/tbody/tr/td[2]/div/a"));
            if (dataElements.isEmpty()) {
                return true;
            }
            scrollPageDown("300");
            for (WebElement element : dataElements) {
                if (element.getText().isEmpty()) {
                    continue;
                }
                WebElement parent = element.findElement(By.xpath(".."));
                pause(500);
                processing.clickElement(element);
                pause(4000);
                try {
                    WebElement child = parent.findElement(By.tagName("a"));
                    String content = child.getText();
                    if (!content.equals("Приступить к просмотру")) {
                        continue;
                    }
                    processing.clickElement(child);
                    pause(4000);
                } catch (Exception e) {
                    continue;
                }
                if (isMore1TabsWithCount(5)) {
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

    private boolean confirm() {
        WebElement webElement = getElementByXpathWithCount("//*/frame[@name='frminfo']", 10);
        if (webElement == null) {
            return false;
        }
        WEB_DRIVER.switchTo().frame(webElement);
        webElement = getElementByXpathWithCount("//*/a[@class='capthes']", 200);
        if (webElement == null) {
            return false;
        }
        processing.clickElement(webElement);
        pause(5000);
        return true;
    }

    private void scrollPageDown(String pixels) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) WEB_DRIVER;
        javascriptExecutor.executeScript("window.scrollBy(0," + pixels + ")");
        pause(2000);
    }


    private boolean goToPaidTransitions() {
        scrollPageDown("500");
        if (!expandTheListToEarn()) {
            return false;
        }
        WebElement webElement = getElementByXpathWithCount("//*/div[@id='mnu_tblock1']/a[text()[contains(.,'Переходы')]]", 30);
        if (webElement == null) {
            System.out.println("Оплачиваемые переходы не найдены");
            return false;
        }
        System.out.println("Переход на 'Переходы'");
        try {
            processing.clickElement(webElement);
            pause(2000);
            WebElement checkPage = getElementByXpathWithCount("//*/td[@id='contentwrapper']/div[text()[contains(.,'Переходы')]]", 30);
            if (checkPage == null) {
                return false;
            }
            List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath("//*/table[@class='work-serf']/tbody/tr/td[2]/div/a"));
            if (dataElements.isEmpty()) {
                return true;
            }
            scrollPageDown("300");
            for (WebElement element : dataElements) {
//        if (element.getAttribute("target").equals("_blank")){
//          continue;
//        }
                processing.clickElement(element);
                pause(4000);
                if (isMore1TabsWithCount(5)) {
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
        processing.sendKeys(webElement, user.getLogin());
        webElement = getElementByXpathWithCount("//*/table[@class='table']/tbody/tr[2]/td[2]/input[@name='password']", 15);
        if (webElement == null) {
            return false;
        }
        processing.sendKeys(webElement, user.getPassword());
        if (resolveCaptcha()) {
            webElement = getElementByXpathWithCount("//*/span[@class='btn green']", 15);
            processing.clickElement(webElement);
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
        processing.sendKeys(webElement, processing.resolveSarSeoCaptcha(point));
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
