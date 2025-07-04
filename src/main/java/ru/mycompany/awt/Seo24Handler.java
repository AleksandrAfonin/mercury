package ru.mycompany.awt;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.awt.*;
import java.time.Duration;
import java.util.Date;
import java.util.List;

public class Seo24Handler implements Handler {
    private final String sitename;
    private String browser;
    private WebDriver WEB_DRIVER;
    private User user;
    private final String URL;
    private Processing processing;

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

    public Seo24Handler() {
        this.sitename = "seo24";
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
        System.out.println(date + "    Account: " + user.getLogin());

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

        pause(5000);
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
            processing.clickElement(webElement);
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
                if (click == null) {
                    continue;
                }
                pause(500);
                processing.clickElement(click);
                try {
                    WebElement begin = processing.getElementByXpathWithCount(element, GET_VIEW, 10);
                    if (begin == null) {
                        continue;
                    }
                    processing.clickElement(begin);
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
        processing.clickInteractable(webElement, 30);
        pause(5000);
        return true;
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
        processing.clickElement(webElement);
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
        processing.sendKeys(webElement, user.getLogin());
        webElement = processing.getElementByXpathWithCount(INPUT_PASSWORD, 15);
        if (webElement == null) {
            return false;
        }
        processing.sendKeys(webElement, user.getPassword());
        webElement = processing.getElementByXpathWithCount(ENTER_BUTTON, 15);
        if (webElement == null) {
            return false;
        }
        processing.clickElement(webElement);
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
