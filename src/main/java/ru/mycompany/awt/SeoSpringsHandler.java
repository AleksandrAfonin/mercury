package ru.mycompany.awt;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.awt.*;
import java.time.Duration;
import java.util.Date;
import java.util.List;

public class SeoSpringsHandler implements Handler {
    private String sitename;
    private String browser;
    private WebDriver WEB_DRIVER;
    private User user;
    private final String URL;
    private Processing processing;

    private final String SURFING_SITES = "//*/div[@id='mnu_tblock1']/a[text()[contains(.,'Серфинг')]]";
    private final String AUTHORISATION = "//*/div[@class='titles'][text()[contains(.,'Вход в аккаунт')]]";// Контроль страницы авторизации
    private final String WELL_COME = "//*/span[@id='mnu_title1']";// Контроль рабочей страницы
    private final String CLOSE_MESSAGE = "//*/div[@class='mail-users']/span[@class='closed']";// Закрыть сообщение
    private final String INPUT_USERNAME = "//*/table[@class='table']/tbody/tr[1]/td[2]/input[@name='username']";
    private final String INPUT_PASSWORD = "//*/table[@class='table']/tbody/tr[2]/td[2]/input[@name='password']";
    private final String ENTER_BUTTON = "//*/span[@class='btn green']";
    private final String GET_FRAME = "//*/frame[@name='frminfo']";// Получить фрейм просмотра
    private final String CONFIRM = "//*/a[@class='capthes']";// Подтвердить просмотр
    private final String MENU = "//*/div[@id='mnu_tblock1']";// Меню пользователя
    private final String DINAMIC_LINK = "//*/td[@id='contentwrapper']/div[text()[contains(.,'Здесь зарабатывают на просмотре сайтов')]]";// Контроль страницы динамических ссылок
    private final String TABLE_SURFING = "//*/table[@class='work-serf']/tbody/tr/td[2]/div/a";
    private final String GET_VIEW = "./a[text()[contains(.,'Приступить к просмотру')]]";
    private final String GO_RUTUBE = "//*/div[@id='mnu_tblock1']/a[text()[contains(.,'RuTube')]]";
    private final String RUTUBE_CONTROL = "//*/td[@id='contentwrapper']/div[text()[contains(.,'Выполнение Rutube')]]";
    private final String TABLE_RUTUBE = "//*/td[@id='contentwrapper']/div[2]/div/table[@class='work-serf']";
    private final String CLICK_LINK_RUTUBE = "./tbody/tr/td[2]/div[1]/span[1]";
    private final String CLICK_VIEW_RUTUBE = "./tbody/tr/td[2]/div[1]/div/span[text()[contains(.,'Приступить к просмотру')]]";
    private final String FORM_INPUT = "//*/input[@id='code-video']";
    private final String FORM_BUTTON = "//*/button[text()[contains(.,'Отправить')]]";


    public SeoSpringsHandler() {
        this.sitename = "seosprings";
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
        if (webElement == null) {
            return false;
        }
        processing.scrollPageDown(300);
        if (!expandTheListToEarn()) {
            return false;
        }
        webElement = processing.getElementByXpathWithCount(GO_RUTUBE, 30);
        if (webElement == null) {
            return false;
        }
        try {
            processing.clickElement(webElement);
            pause(2000);
            WebElement checkPage = processing.getElementByXpathWithCount(RUTUBE_CONTROL, 30);
            if (checkPage == null) {
                return false;
            }
            processing.scrollPageDown(200);
            List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath(TABLE_RUTUBE));
            if (dataElements.isEmpty()) {
                return true;
            }
            for (WebElement element : dataElements) {
                WebElement el = processing.getElementByXpathWithCount(element, CLICK_LINK_RUTUBE, 1);
                if (el == null) {
                    continue;
                }
                processing.clickElement(el);
                try {
                    el = processing.getElementByXpathWithCount(element, CLICK_VIEW_RUTUBE, 30);
                    if (el == null) {
                        continue;
                    }
                    processing.clickElement(el);
                    pause(5000);
                } catch (Exception e) {
                    continue;
                }
                if (processing.isMore1TabsWithCount(30)) {
                    Object[] windowsHandles = WEB_DRIVER.getWindowHandles().toArray();
                    WEB_DRIVER.switchTo().window((String) windowsHandles[1]);
                    pause(10000);
                    processing.mouseLeftClick(400, 300, 100, 100);
                    pause(7000);
                    moveSlider();
                    processing.closeAllTabs(30);
                }
                processing.scrollPageDown(59);
            }
        } catch (TimeoutException e) {
            return false;
        }
        return true;
    }

    private boolean goToPaidSurfing() {
        processing.scrollPageDown(300);
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
            processing.scrollPageDown(550);
            for (WebElement element : dataElements) {
                if (element.getText().isEmpty()) {
                    continue;
                }
                WebElement parent = element.findElement(By.xpath(".."));
                pause(500);
                processing.clickElement(element);
                try {
                    WebElement child = processing.getElementByXpathWithCount(parent, GET_VIEW, 30);
                    if (child == null) {
                        continue;
                    }
                    processing.clickElement(child);
                    pause(4000);
                } catch (Exception e) {
                    continue;
                }
                if (processing.isMore1TabsWithCount(30)) {
                    Object[] windowsHandles = WEB_DRIVER.getWindowHandles().toArray();
                    WEB_DRIVER.switchTo().window((String) windowsHandles[1]);
                    confirm();
                    processing.closeAllTabs(30);
                }
                processing.scrollPageDown(59);
            }
        } catch (TimeoutException e) {
            return false;
        }
        return true;
    }

    private boolean confirm() {
        WebElement webElement = processing.getElementByXpathWithCount(GET_FRAME, 30);
        if (webElement == null) {
            return false;
        }
        WEB_DRIVER.switchTo().frame(webElement);
        webElement = processing.getElementByXpathWithCount(CONFIRM, 220);
        if (webElement == null) {
            return false;
        }
        processing.clickElement(webElement);
        pause(15000);
        return true;
    }

    private boolean moveSlider() {
        WebElement webElement = processing.getElementByXpathWithCount(FORM_INPUT, 30);
        if (webElement == null) {
            return false;
        }
        String max = webElement.getAttribute("max");
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) WEB_DRIVER;
        javascriptExecutor.executeScript("arguments[0].setAttribute('value', '" + max + "');", webElement);
        pause(200);
        javascriptExecutor.executeScript("arguments[0].setAttribute('wfd-id', 'id0');", webElement);
        webElement = processing.getElementByXpathWithCount(FORM_BUTTON, 5);
        processing.clickInteractable(webElement, 200);
        pause(5000);
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
        if (webElement == null) {
            return false;
        }
        processing.scrollPageDown(250);
        if (enterAccountData()) {
            webElement = processing.getElementByXpathWithCount(WELL_COME, 30);
            if (webElement == null) {
                return false;
            }
            //closeMailMessage();
            return true;
        }
        return false;
    }

    private void closeMailMessage() {
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
        //if (resolveCaptcha()) {
        webElement = processing.getElementByXpathWithCount(ENTER_BUTTON, 15);
        if (webElement == null) {
            return false;
        }
        processing.clickElement(webElement);
        pause(5000);
        return true;
        //}
        //return false;
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
