package ru.mycompany.awt;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.interactions.Actions;

import java.awt.*;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Manager implements Runnable {
    private final GeneralWindow generalWindow;
    private boolean isStop;
    private final Processing processing;
    private final List<String> sites;
    private final Map<String, Handler> handlers;
    private final Map<String, List<User>> accounts;
    private final Map<String, String> browsers;
    private ChromeOptions chromeOptions;
    private EdgeOptions edgeOptions;

    public Manager(List<String> sites, GeneralWindow generalWindow) {
        this.generalWindow = generalWindow;
        this.sites = sites;
        this.isStop = true;
        try {
            this.processing = new Processing();
        } catch (AWTException e) {
            throw new RuntimeException(e);
        }
        this.handlers = new HashMap<>();
        this.accounts = new HashMap<>();
        this.browsers = new HashMap<>();

        for (String str : sites) {
            accounts.put(str, SQLiteProvider.getInstance().getAccountsList(str));
            browsers.put(str, SQLiteProvider.getInstance().getBrowser(str));
            String hname = SQLiteProvider.getInstance().getHandler(str);
            try {
                handlers.put(str, getHandler(hname));
            } catch (AWTException | IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }

    public void run() throws WebDriverException {
//     = Arrays.asList(
//            "seosprings",
//            "goldenclicks",//+
//            "seojump"---
//            "seogold",//+
//            "profitcentr24",//+
//            "seobux",
//            "prodvisots",
//            "seo24"====
//            "soofast",
//            "profitcentr",
//            "seofast"
//            "seoclub",
//            "wmrfast"
//            "sarseo"
//    );


        while (true) {
            for (String site : sites) {
                Handler handler = handlers.get(site);
                for (User us : accounts.get(site)) {
                    WebDriver webDriver = getWebDriver(browsers.get(site));
                    processing.setProperty(webDriver, new Actions(webDriver, Duration.ofSeconds(1)));
                    handler.setProperty(webDriver, processing, us);
                    Thread performanceTask = new Thread(new Task(handler, us));
                    performanceTask.start();
                    try {
                        performanceTask.join();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (isStop) {
                        generalWindow.setLabelButton();
                        return;
                    }
                }
            }
        }
    }

    private Handler getHandler(String handlerName) throws AWTException, IOException {
        return switch (handlerName) {
            case "AVISO" -> new AvisoHandler();
            case "DELIONIX" -> new DelionixHandler();
            case "GOLDENCLICKS" -> new GoldenClicksHandler();
            case "PRODVISOTS" -> new ProdvisotsHandler();
            case "PROFITCENTR24" -> new ProfitCentr24Handler();
            case "PROFITCENTR" -> new ProfitCentrHandler();
            case "SARSEO" -> new SarSeoHandler();
            case "SEO24" -> new Seo24Handler();
            case "SEOBUX" -> new SeoBuxHandler();
            case "SEOCLUB" -> new SeoClubHandler();
            case "SEOFAST" -> new SeoFastHandlerRobot();
            case "SEOGOLD" -> new SeoGoldHandler();
            case "SEOJUMP" -> new SeoJumpHandler();
            case "SEOSPRINGS" -> new SeoSpringsHandler();
            case "SOCPUBLIC" -> new SocPublicHandler();
            case "SOOFAST" -> new SoofastHandler();
            case "WMRFAST" -> new WMRFastHandler();
            default -> throw new IllegalStateException("Unexpected value: " + handlerName);
        };
    }

    private WebDriver getWebDriver(String browser) {
        switch (browser) {
            case "CHROME":
                if (chromeOptions == null) {
                    chromeOptions = new ChromeOptions();
                    chromeOptions.addArguments("--window-size=1300,768");// size browser
                    chromeOptions.addArguments("--window-position=0,0");// Позиционирование браузера
                    //edgeOptions.addArguments("force-device-scale-factor=0.7");// Установка масштаба контента браузера
                    chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});// Убрать служебную надпись
                    chromeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);// Не ждем полной загрузки страницы
                }
                return new ChromeDriver(chromeOptions);
            case "EDGE":
                if (edgeOptions == null) {
                    edgeOptions = new EdgeOptions();
                    edgeOptions.addArguments("--window-size=1300,768");// size browser
                    edgeOptions.addArguments("--window-position=0,0");// Позиционирование браузера
                    //edgeOptions.addArguments("force-device-scale-factor=0.7");// Установка масштаба контента браузера
                    edgeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});// Убрать служебную надпись
                    edgeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);// Не ждем полной загрузки страницы
                }
                return new EdgeDriver(edgeOptions);
        }
        return null;
    }
}

