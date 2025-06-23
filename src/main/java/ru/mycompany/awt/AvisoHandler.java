package ru.mycompany.awt;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;
import java.util.Date;
import java.util.List;

public class AvisoHandler implements Handler {
    private final WebDriver WEB_DRIVER;
    private final String E_MAIL;    // alsupp@yandex.ru
    private final String PASSWORD;  // 19b650660b
    private final Actions ACTIONS;
    private final String URL;

    public AvisoHandler(WebDriver webDriver, User user) {
        this.WEB_DRIVER = webDriver;
        this.E_MAIL = user.getLogin();
        this.PASSWORD = user.getPassword();
        this.ACTIONS = new Actions(webDriver, Duration.ofSeconds(1));
        this.URL = "https://aviso.bz/login";
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

        if (!goToYouTube()) {
            WEB_DRIVER.quit();
            return;
        }
//============================
        if (!goToPaidSurfing()) {
            WEB_DRIVER.quit();
            return;
        }
        if (!goToYouTube()) {
            WEB_DRIVER.quit();
            return;
        }
//============================
        pause(5000);
        WEB_DRIVER.quit();
    }

    private boolean closeAllTabs() {
        int count = 30;
        while (count-- > 0) {
            Object[] windowsHandles = WEB_DRIVER.getWindowHandles().toArray();
            if (windowsHandles.length == 1) {
                return true;
            }
            pause(2000);
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
            if (WEB_DRIVER.getWindowHandles().toArray().length > 1) {
                return true;
            }
        }
        return false;
    }

    private boolean goToPaidSurfing() {
        if (!expandTheListToEarn()) {
            return false;
        }
        WebElement webElement = getElementByXpathWithCount("//*/div[@id='mnu_tblock1']/a[text()[contains(.,'Серфинг')]]", 30);
        if (webElement == null) {
            System.out.println("Серфинг не найдены");
            return false;
        }
        System.out.println("Переход на 'Серфинг'");
        try {
            ACTIONS.click(webElement).perform();
            pause(2000);
            String[] texts = new String[]{"Динамические ссылки."};
            int pageNum = checkPages(texts, 30);
            if (pageNum != 0) {
                return false;
            }
            List<WebElement> dataElements = WEB_DRIVER.findElements(By.xpath("//*/table[@class='work-serf']/tbody/tr/td/div/a"));
            if (dataElements.isEmpty()) {
                return true;
            }
            for (WebElement element : dataElements) {
                String content = element.getText();
                if (content.isEmpty()) {
                    continue;
                }
                WebElement parent = element.findElement(By.xpath(".."));
                pause(500);
                ACTIONS.click(element).perform();
                pause(4000);
                try {
                    WebElement child = parent.findElement(By.tagName("a"));
                    content = child.getText();
                    if (!content.equals("Приступить к просмотру")) {
                        continue;
                    }
                    ACTIONS.click(child).perform();
                    pause(2000);
                } catch (Exception e) {
                    continue;
                }
                if (isMore1TabsWithCount(5)) {
                    Object[] windowsHandles = WEB_DRIVER.getWindowHandles().toArray();
                    WEB_DRIVER.switchTo().window((String) windowsHandles[1]);
                    confirmViewing();
                    closeAllTabs();
                }
            }
        } catch (TimeoutException e) {
            return false;
        }
        return true;
    }

    private boolean goToYouTube() {
        if (!expandTheListToEarn()) {
            return false;
        }
        WebElement webElement = getElementByXpathWithCount("//*/div[@id='mnu_tblock1']/a[text()[contains(.,'YouTube')]]", 30);
        if (webElement == null) {
            System.out.println("YouTube не найдены");
            return false;
        }
        System.out.println("Переход на 'YouTube'");
        try {
            ACTIONS.click(webElement).perform();
            pause(2000);
            String[] texts = new String[]{"Выполнение Youtube"};
            int pageNum = checkPages(texts, 30);
            if (pageNum != 0) {
                return false;
            }
            List<WebElement> dataElementsLink = WEB_DRIVER.findElements(By.xpath("//*/table[@class='work-serf']/tbody/tr/td[2]/div[1]/span[1]"));
            List<WebElement> dataElementsImage = WEB_DRIVER.findElements(By.xpath("//*/table[@class='work-serf']/tbody/tr/td[1]/div"));
            if (dataElementsLink.isEmpty()) {
                return true;
            }
            for (int i = 0; i < dataElementsLink.size(); i++) {
                if (!dataElementsImage.get(i).getAttribute("title").equals("Просмотр видео")) {
                    continue;
                }
                WebElement parent = dataElementsLink.get(i).findElement(By.xpath(".."));
                parent = parent.findElement(By.xpath(".."));
                pause(500);
                ACTIONS.click(dataElementsLink.get(i)).perform();
                pause(2000);
                WebElement cont = getElementByXpathWithCount("//*[text()[contains(.,'Начать просмотр')]]", 5);
                if (cont != null) {
                    ACTIONS.click(cont).perform();
                    pause(5000);
                }
                if (isMore1TabsWithCount(5)) {
                    waitTime("Youtube заработок", 300);
                    closeAllTabs();
                }
                pause(1000);
                WebElement child = getElementByXpathWithCountByElement(parent, "./div[3]/div/span/span[contains(text(),'Подтвердить просмотр')]", 200);
                if (child == null) {
                    continue;
                }
                try {
                    ACTIONS.click(child).perform();
                    pause(4000);
                } catch (JavascriptException ignored) {
                }
            }
        } catch (TimeoutException e) {
            return false;
        }
        return true;
    }

    private boolean confirmViewing() {
        WebElement webElement = getElementByXpathWithCount("//*/frame[@name='frminfo']", 10);
        if (webElement == null) {
            return false;
        }
        WEB_DRIVER.switchTo().frame(webElement);
        webElement = getElementByXpathWithCount("//*/tbody/tr/td/a[@class='btn_capt']", 300);
        if (webElement == null) {
            return false;
        }
        ACTIONS.click(webElement).perform();
        pause(2000);
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
        String[] texts = new String[]{"Вход в аккаунт", "Выполнение заданий"};
        int pageNum = checkPages(texts, 30);
        switch (pageNum) {
            case 0 -> {
                System.out.println("На странице авторизации");
                if (enterAccountData()) {
                    texts = new String[]{"Выполнение заданий"};
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
     * Авторизовываемся на сайте своими данными
     *
     * @return true - успех
     * false - провал
     */
    private boolean enterAccountData() {
        WebElement webElement = getElementByXpathWithCount("//*/form/div/input[@name='username']", 30);
        if (webElement == null) {
            return false;
        }
        ACTIONS.sendKeys(webElement, E_MAIL).perform();
        webElement = getElementByXpathWithCount("//*/form/div/input[@name='password']", 30);
        if (webElement == null) {
            return false;
        }
        ACTIONS.sendKeys(webElement, PASSWORD).perform();
        webElement = getElementByXpathWithCount("//*/div[@class='button-box']/button[@id='button-login']/span", 30);
        ACTIONS.click(webElement).perform();
        pause(5000);
        return true;
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

    private WebElement getElementByXpathWithCountByElement(WebElement element, String xpath, int count) {
        WebElement webElement;
        for (int i = 0; i < count; i++) {
            pause(2000);
            try {
                webElement = element.findElement(By.xpath(xpath));
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
