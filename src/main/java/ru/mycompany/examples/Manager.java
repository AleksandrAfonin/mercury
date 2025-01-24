package ru.mycompany.examples;

import org.openqa.selenium.Dimension;
import org.openqa.selenium.PageLoadStrategy;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;

import java.awt.*;
import java.util.List;

public class Manager {

  public void start() throws InterruptedException, WebDriverException {
    List<User> accountsDelionix = SQLiteProvider.getInstance().getAccountsList("delionix");
    List<User> accountsProfitCentr = SQLiteProvider.getInstance().getAccountsList("profitcentr");
    List<User> accountsSoeFast = SQLiteProvider.getInstance().getAccountsList("seo-fast");
    List<User> accountsSeoClub = SQLiteProvider.getInstance().getAccountsList("seoclub");
    List<User> accountsSocPublic = SQLiteProvider.getInstance().getAccountsList("socpublic");
    List<User> accountsWMRFast = SQLiteProvider.getInstance().getAccountsList("wmrfast");
    List<User> accountsSarSeo = SQLiteProvider.getInstance().getAccountsList("sarseo");
    List<User> accountsProdvisots = SQLiteProvider.getInstance().getAccountsList("prodvisots");

    // Получить размеры экрана
    java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    int width = (int) screenSize.getWidth();
    int height = (int) screenSize.getHeight();

//    ChromeOptions options = new ChromeOptions();
//    FirefoxOptions firefoxOptions = new FirefoxOptions();

    Thread thread1 = new Thread(() -> { // SocPublic ========================================================
      EdgeOptions edgeOptions = new EdgeOptions();
      //edgeOptions.addArguments("force-device-scale-factor=0.7");// Установка масштаба контента браузера
      edgeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});// Убрать служебную надпись
      //edgeOptions.addArguments("--window-position=0,0");// Позиционирование браузера
      edgeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);// Не ждем полной загрузки страницы
      while (true) {
        for (User user : accountsDelionix) {
          long currentTime = System.currentTimeMillis();
          long nextTime = user.getNextTime();
          if (currentTime < nextTime){
            continue;
          }
          EdgeDriver webDriver = new EdgeDriver(edgeOptions);// Получаем драйвер
          webDriver.manage().window().maximize();// Устанавливаем размеры окна браузера
          try{
            new DelionixHandler(webDriver, user).run();// Handler
            //setNextTime(user);
          }catch (Exception e){
            e.printStackTrace();
            webDriver.quit();
          }
          try {
            Thread.sleep(10000);// Ожидание 30
          } catch (InterruptedException ignored) {
          }
        }
        try {
          Thread.sleep(60000);// Ожидание 60
        } catch (InterruptedException ignored) {
        }
      }
    });

    Thread thread2 = new Thread(() -> {// ProfitCentr // Aviso // SeoClub ============================
      EdgeOptions edgeOptions = new EdgeOptions();
      ChromeOptions chromeOptions = new ChromeOptions();

      chromeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});
      chromeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);

      //edgeOptions.addArguments("force-device-scale-factor=0.5");// Установка масштаба контента браузера
      edgeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});// Убрать служебную надпись
      //edgeOptions.addArguments("--window-position=" + width + ",0");// Позиционирование браузера
      edgeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);// Не ждем полной загрузки страницы
      while (true) {
        for (User user : accountsProdvisots) {
          long currentTime = System.currentTimeMillis();
          long nextTime = user.getNextTime();
          if (currentTime < nextTime){
            continue;
          }
          EdgeDriver webDriver = new EdgeDriver(edgeOptions);// Получаем драйвер
          webDriver.manage().window().maximize();// Устанавливаем размеры окна браузера
          try{
            new ProdvisotsHandler(webDriver, user).run();// Handler
            setNextTime(user);
          }catch (Exception e){
            e.printStackTrace();
            webDriver.quit();
          }
          try {
            Thread.sleep(10000);// Ожидание 30
          } catch (InterruptedException ignored) {
          }
        }

        for (User user : accountsProfitCentr) {
          long currentTime = System.currentTimeMillis();
          long nextTime = user.getNextTime();
          if (currentTime < nextTime){
            continue;
          }
          EdgeDriver webDriver = new EdgeDriver(edgeOptions);// Получаем драйвер
          webDriver.manage().window().maximize();// Устанавливаем размеры окна браузера
          try {
            new ProfitCentrHandler(webDriver, user).run();// Handler
            setNextTime(user);
          } catch (Exception e) {
            e.printStackTrace();
            webDriver.quit();
          }
          try {
            Thread.sleep(10000);// Ожидание 30 секунд
          } catch (InterruptedException ignored) {
          }
        }

        for (User user : accountsSoeFast) {
          long currentTime = System.currentTimeMillis();
          long nextTime = user.getNextTime();
          if (currentTime < nextTime){
            continue;
          }
          ChromeDriver webDriver = new ChromeDriver(chromeOptions);// Получаем драйвер
          //webDriver.manage().window().setSize(new Dimension(width, height));// Устанавливаем размеры окна браузера
          try {
            new SeoFastHandlerRobot(webDriver, user).run();// Handler
            setNextTime(user);
          } catch (Exception e) {
            e.printStackTrace();
            webDriver.quit();
          }
          try {
            Thread.sleep(10000);// Ожидание 30 секунд
          } catch (InterruptedException ignored) {
          }
        }

        for (User user : accountsSeoClub) {
          long currentTime = System.currentTimeMillis();
          long nextTime = user.getNextTime();
          if (currentTime < nextTime){
            continue;
          }
          EdgeDriver webDriver = new EdgeDriver(edgeOptions);// Получаем драйвер
          webDriver.manage().window().maximize();// Устанавливаем размеры окна браузера
          try {
            new SeoClubHandler(webDriver, user).run();// Handler
            setNextTime(user);
          } catch (Exception e) {
            e.printStackTrace();
            webDriver.quit();
          }
          try {
            Thread.sleep(10000);// Ожидание 30 минут
          } catch (InterruptedException ignored) {
          }
        }

        for (User user : accountsWMRFast) {
          long currentTime = System.currentTimeMillis();
          long nextTime = user.getNextTime();
          if (currentTime < nextTime){
            continue;
          }
          EdgeDriver webDriver = new EdgeDriver(edgeOptions);// Получаем драйвер
          webDriver.manage().window().maximize();// Устанавливаем размеры окна браузера
          try {
            new WMRFastHandler(webDriver, user).run();// Handler
            setNextTime(user);
          } catch (Exception e) {
            e.printStackTrace();
            webDriver.quit();
          }
          try {
            Thread.sleep(10000);// Ожидание 30 минут
          } catch (InterruptedException ignored) {
          }
        }

        for (User user : accountsSarSeo) {
          long currentTime = System.currentTimeMillis();
          long nextTime = user.getNextTime();
          if (currentTime < nextTime){
            continue;
          }
          EdgeDriver webDriver = new EdgeDriver(edgeOptions);// Получаем драйвер
          webDriver.manage().window().maximize();// Устанавливаем размеры окна браузера
          try{
            new SarSeoHandler(webDriver, user).run();// Handler
            setNextTime(user);
          }catch (Exception e){
            e.printStackTrace();
            webDriver.quit();
          }
          try {
            Thread.sleep(10000);// Ожидание 30
          } catch (InterruptedException ignored) {
          }
        }


        try {
          Thread.sleep(60000);// Ожидание 60
        } catch (InterruptedException ignored) {
        }
      }
    });

    Thread thread3 = new Thread(() -> {//  ===================================================
      EdgeOptions edgeOptions = new EdgeOptions();
      //edgeOptions.addArguments("force-device-scale-factor=0.5");// Установка масштаба контента браузера
      edgeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});// Убрать служебную надпись
      //edgeOptions.addArguments("--window-position=0," + height);// Позиционирование браузера
      edgeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);// Не ждем полной загрузки страницы
      while (true) {
        for (User user : accountsWMRFast) {
          long currentTime = System.currentTimeMillis();
          long nextTime = user.getNextTime();
          if (currentTime < nextTime){
            continue;
          }
          EdgeDriver webDriver = new EdgeDriver(edgeOptions);// Получаем драйвер
          webDriver.manage().window().maximize();// Устанавливаем размеры окна браузера
          try {
            new WMRFastHandler(webDriver, user).run();// Handler
            //setNextTime(user);
          } catch (Exception e) {
            e.printStackTrace();
            webDriver.quit();
          }
          try {
            Thread.sleep(15000);// Ожидание 30 секунд
          } catch (InterruptedException ignored) {
          }
        }
        try {
          Thread.sleep(60000);// Ожидание 60
        } catch (InterruptedException ignored) {
        }
      }
    });

    Thread thread4 = new Thread(() -> {// accounts4 ======================================================
      ChromeOptions edgeOptions = new ChromeOptions();
      //edgeOptions.addArguments("force-device-scale-factor=0.5");// Установка масштаба контента браузера
      edgeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});// Убрать служебную надпись
      //edgeOptions.addArguments("--window-position=-2000,0");// Позиционирование браузера
      edgeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);// Не ждем полной загрузки страницы
      while (true) {
        for (User user : accountsSoeFast) {
          long currentTime = System.currentTimeMillis();
          long nextTime = user.getNextTime();
          if (currentTime < nextTime){
            continue;
          }
          ChromeDriver webDriver = new ChromeDriver(edgeOptions);// Получаем драйвер
          webDriver.manage().window().maximize();// Устанавливаем размеры окна браузера
          try {
            new SeoFastHandlerRobot(webDriver, user).run();// Handler
            //setNextTime(user);
          } catch (Exception e) {
            e.printStackTrace();
            webDriver.quit();
          }
          //setNextTime(user);
          try {
            Thread.sleep(10000);// Ожидание 30 секунд
          } catch (InterruptedException ignored) {
          }
        }

        try {
          Thread.sleep(60000);// Ожидание 60
        } catch (InterruptedException ignored) {
        }
      }
    });

    Thread thread5 = new Thread(() -> { // SocPublic ========================================================
      EdgeOptions edgeOptions = new EdgeOptions();
      //edgeOptions.addArguments("force-device-scale-factor=0.7");// Установка масштаба контента браузера
      edgeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});// Убрать служебную надпись
      //edgeOptions.addArguments("--window-position=0,0");// Позиционирование браузера
      edgeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);// Не ждем полной загрузки страницы
      while (true) {
        for (User user : accountsSeoClub) {
          long currentTime = System.currentTimeMillis();
          long nextTime = user.getNextTime();
          if (currentTime < nextTime){
            continue;
          }
          EdgeDriver webDriver = new EdgeDriver(edgeOptions);// Получаем драйвер
          webDriver.manage().window().maximize();// Устанавливаем размеры окна браузера
          try{
            new SeoClubHandler(webDriver, user).run();// Handler
            //setNextTime(user);
          }catch (Exception e){
            e.printStackTrace();
            webDriver.quit();
          }
          try {
            Thread.sleep(10000);// Ожидание 30
          } catch (InterruptedException ignored) {
          }
        }
        try {
          Thread.sleep(60000);// Ожидание 60
        } catch (InterruptedException ignored) {
        }
      }
    });

    Thread thread6 = new Thread(() -> { // SocPublic ========================================================
      EdgeOptions edgeOptions = new EdgeOptions();
      //edgeOptions.addArguments("force-device-scale-factor=0.7");// Установка масштаба контента браузера
      edgeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});// Убрать служебную надпись
      //edgeOptions.addArguments("--window-position=0,0");// Позиционирование браузера
      edgeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);// Не ждем полной загрузки страницы
      while (true) {
        for (User user : accountsSarSeo) {
          long currentTime = System.currentTimeMillis();
          long nextTime = user.getNextTime();
          if (currentTime < nextTime){
            continue;
          }
          EdgeDriver webDriver = new EdgeDriver(edgeOptions);// Получаем драйвер
          webDriver.manage().window().maximize();// Устанавливаем размеры окна браузера
          try{
            new SarSeoHandler(webDriver, user).run();// Handler
            //setNextTime(user);
          }catch (Exception e){
            e.printStackTrace();
            webDriver.quit();
          }
          try {
            Thread.sleep(10000);// Ожидание 30
          } catch (InterruptedException ignored) {
          }
        }
        try {
          Thread.sleep(10000);// Ожидание 60
        } catch (InterruptedException ignored) {
        }
      }
    });

    Thread thread7 = new Thread(() -> { // SocPublic ========================================================
      EdgeOptions edgeOptions = new EdgeOptions();
      //edgeOptions.addArguments("force-device-scale-factor=0.7");// Установка масштаба контента браузера
      edgeOptions.setExperimentalOption("excludeSwitches", new String[]{"enable-automation"});// Убрать служебную надпись
      //edgeOptions.addArguments("--window-position=0,0");// Позиционирование браузера
      edgeOptions.setPageLoadStrategy(PageLoadStrategy.NONE);// Не ждем полной загрузки страницы
      while (true) {
        for (User user : accountsProdvisots) {
          long currentTime = System.currentTimeMillis();
          long nextTime = user.getNextTime();
          if (currentTime < nextTime){
            continue;
          }
          EdgeDriver webDriver = new EdgeDriver(edgeOptions);// Получаем драйвер
          webDriver.manage().window().maximize();// Устанавливаем размеры окна браузера
          try{
            new ProdvisotsHandler(webDriver, user).run();// Handler
            //setNextTime(user);
          }catch (Exception e){
            e.printStackTrace();
            webDriver.quit();
          }
          try {
            Thread.sleep(10000);// Ожидание 30
          } catch (InterruptedException ignored) {
          }
        }
        try {
          Thread.sleep(10000);// Ожидание 60
        } catch (InterruptedException ignored) {
        }
      }
    });

    //thread1.start();// SocPublic
    //thread2.start();// ProfitCentr // SeoFast // SeoClub
    //thread3.start();
    //thread4.start();
    //thread5.start();
    //thread6.start();
    thread7.start();

    thread1.join();
    thread2.join();
    thread3.join();
    thread4.join();
    thread5.join();
    thread6.join();
    thread7.join();
  }

  private synchronized void setNextTime(User user){
    long nextTime = System.currentTimeMillis() + user.getIntervalMinutes() * 60000L;
    SQLiteProvider.getInstance().setNextTime(user, nextTime);
    user.setNextTime(nextTime);
  }
}

