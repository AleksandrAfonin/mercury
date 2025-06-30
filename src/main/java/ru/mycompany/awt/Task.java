package ru.mycompany.awt;

import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.awt.*;
import java.io.IOException;
import java.util.List;

public class Task implements Runnable {
  private ChromeOptions chromeOptions;
  private EdgeOptions edgeOptions;
  private final List<User> accounts;


  public Task(String siteName) {
    this.accounts = SQLiteProvider.getInstance().getAccountsList(siteName);
  }

  @Override
  public void run() {
    for (User user : accounts) {
      long currentTime = System.currentTimeMillis();
      long nextTime = user.getNextTime();
      if (currentTime < nextTime) {
        continue;
      }
      WebDriver webDriver = getWebDriver(user);// Получаем драйвер
      //webDriver.manage().window().maximize();// Устанавливаем размеры окна браузера
      try {
        getHandler(webDriver, user).run();
        //setNextTime(user);
      } catch (Exception e) {
        e.printStackTrace();
        webDriver.quit();
      }
      try {
        Thread.sleep(5000);// Ожидание 30
      } catch (InterruptedException ignored) {
      }
    }
  }

  private void setNextTime(User user) {
    long nextTime = System.currentTimeMillis() + user.getIntervalMinutes() * 60000L;
    SQLiteProvider.getInstance().setNextTime(user, nextTime);
    user.setNextTime(nextTime);
  }

  private WebDriver getWebDriver(User user) {
    switch (user.getBrowser()) {
      case CHROME:
        if (chromeOptions == null) {
          chromeOptions = new ChromeOptions();
          chromeOptions.addArguments("--window-size=1300,768");// size browser
          chromeOptions.addArguments("--window-position=0,0");// Позиционирование браузера
          //edgeOptions.addArguments("force-device-scale-factor=0.7");// Установка масштаба контента браузера
          chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});// Убрать служебную надпись
          chromeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);// Не ждем полной загрузки страницы
        }
        return new ChromeDriver(chromeOptions);
      case EDGE:
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

  private Handler getHandler(WebDriver webDriver, User user) throws AWTException, IOException {
    return switch (user.getHandler()) {
      case AVISO -> new AvisoHandler(webDriver, user);
      case DELIONIX -> new DelionixHandler(webDriver, user);
      case GOLDENCLICKS -> new GoldenClicksHandler(webDriver, user);
      case PRODVISOTS -> new ProdvisotsHandler(webDriver, user);
      case PROFITCENTR24 -> new ProfitCentr24Handler(webDriver, user);
      case PROFITCENTR -> new ProfitCentrHandler(webDriver, user);
      case SARSEO -> new SarSeoHandler(webDriver, user);
      case SEO24 -> new Seo24Handler(webDriver, user);
      case SEOBUX -> new SeoBuxHandler(webDriver, user);
      case SEOCLUB -> new SeoClubHandler(webDriver, user);
      case SEOFAST -> new SeoFastHandlerRobot(webDriver, user);
      case SEOGOLD -> new SeoGoldHandler(webDriver, user);
      case SEOJUMP -> new SeoJumpHandler(webDriver, user);
      case SEOSPRINGS -> new SeoSpringsHandler(webDriver, user);
      case SOCPUBLIC -> new SocPublicHandler(webDriver, "", "");
      case SOOFAST -> new SoofastHandler(webDriver, user);
      case WMRFAST -> new WMRFastHandler(webDriver, user);
    };
  }
}
