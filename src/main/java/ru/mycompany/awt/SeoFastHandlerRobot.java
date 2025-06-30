package ru.mycompany.awt;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.Actions;

import java.awt.*;
import java.time.Duration;
import java.util.Date;

public class SeoFastHandlerRobot implements Handler {
    private final String sitename;
    private final String browser;
    private final WebDriver WEB_DRIVER;
    private String E_MAIL;    // alsupp@yandex.ru
    private String PASSWORD;  // 19b650660b
    private final Actions ACTIONS;
    private final String URL;
    private final Processing processing;
    private final Rectangle header;

    private final ControlPoint enterControlPoint;
    private final ControlPoint earnControlPoint;
    private final ControlPoint toVisitsControlPoint;
    private final ControlPoint hereVisitsControlPoint;
    private final ControlPoint visitsHereControlPoint;
    private final ControlPoint roundLincControlPoint;
    private final ControlPoint idControlPoint;
    private final ControlPoint visitControlPoint;
    private final ControlPoint filedControlPoint;
    private final ControlPoint closePassivePageControlPoint;
    private final ControlPoint closeActivePageControlPoint;
    private final ControlPoint completedControlPoint;
    private final ControlPoint confirmCompletedControlPoint;

    public SeoFastHandlerRobot(WebDriver webDriver, User user) throws AWTException {
        this.sitename = "seofast";
        this.WEB_DRIVER = webDriver;
        this.ACTIONS = new Actions(webDriver, Duration.ofSeconds(1));
        this.processing = new Processing(webDriver, ACTIONS);
        this.E_MAIL = user.getLogin();
        this.PASSWORD = user.getPassword();
        this.header = new Rectangle(390, 15, 80, 13);
        this.URL = SQLiteProvider.getInstance().getUrlSite(sitename);
        browser = processing.getBrowserName(webDriver);

        // Контрольна точка входа в акаунт
        this.enterControlPoint = new ControlPoint(sitename, browser, "enter", 15, 17, new Rectangle(520, 280, 280, 120));
        this.enterControlPoint.addRectangleClick("_", new Rectangle(-97, -4, 250, 26));
        this.enterControlPoint.addRectangleClick("e-mail", new Rectangle(-115, -78, 280, 18));
        this.enterControlPoint.addRectangleClick("password", new Rectangle(-115, -42, 280, 18));
        // Контрольна точка "заработать"
        this.earnControlPoint = new ControlPoint(sitename, browser, "earn", 9, 10, new Rectangle(40, 300, 300, 300));
        // Контрольная точка перехода на оплачиваемые посещения
        this.toVisitsControlPoint = new ControlPoint(sitename, browser, "toVisits", 19, 10, new Rectangle(100, 150, 250, 200));
        this.toVisitsControlPoint.addRectangleClick("click", new Rectangle(-104, 2, 170, 6));
        // Контрольная точка раздела оплачиваемых посещений
        this.hereVisitsControlPoint = new ControlPoint(sitename, browser, "hereVisits", 11, 10, new Rectangle(500, 270, 260, 490));
        // Контрольная точка присутствия ссылок
        this.visitsHereControlPoint = new ControlPoint(sitename, browser, "visitsHere", 11, 11, new Rectangle(490, 250, 260, 510));
        // Контрольная точка ссылки
        this.roundLincControlPoint = new ControlPoint(sitename, browser, "round-linc", 32, 32, null);
        this.roundLincControlPoint.addRectangleClick("clickLinc", new Rectangle(60, 0, 6, 13));
        // Контрольная точка айдишника при ссылке
        this.idControlPoint = new ControlPoint(sitename, browser, "id", 15, 10, null);
        // Контрольная точка подтверждения перехода по ссылке
        this.visitControlPoint = new ControlPoint(sitename, browser, "to-visit", 13, 14, null);
        this.visitControlPoint.addRectangleClick("click", new Rectangle(0, -5, 256, 27));
        // Контрольная точка битой ссылки
        this.filedControlPoint = new ControlPoint(sitename, browser, "filed", 13, 14, null);
        // Крестик закрытия пассивной страницы
        this.closePassivePageControlPoint = new ControlPoint(sitename, browser, "close-passive-page", 8, 8, new Rectangle(150, 0, 170, 40));
        // Крестик закрытия активной страницы
        this.closeActivePageControlPoint = new ControlPoint(sitename, browser, "close-active-page", 8, 8, new Rectangle(270, 2, 900, 30));
        this.closeActivePageControlPoint.addRectangleClick("click", new Rectangle(0, 0, 8, 8));
        // Подтвеждение завершения просмотра
        this.completedControlPoint = new ControlPoint(sitename, browser, "completed", 12, 9, new Rectangle(270, 2, 900, 30));
        // Подтверждение получения оплаты или "чего то не так"
        this.confirmCompletedControlPoint = new ControlPoint(sitename, browser, "confirmCompleted", 14, 12, null);

    }

    public void setUser(User user){
        this.E_MAIL = user.getLogin();
        this.PASSWORD = user.getPassword();
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
        pause(5000, 0);
        if (!authorization()) {
            WEB_DRIVER.quit();
            return;
        }
        pause(5000, 0);
        if (!performingClicksOnLinks(goVisitsPage())) {
            WEB_DRIVER.quit();
            return;
        }


//        WEB_DRIVER.get(WEB_DRIVER.getCurrentUrl());
//        pause(15000, 5000);
//        performingClicks();
//===============================
        pause(5000, 0);
        WEB_DRIVER.quit();
    }

    private Rectangle goVisitsPage(){
        Point point = processing.find(earnControlPoint, true, 10);
        if (point == null) {
            processing.saveScreenShot(earnControlPoint.getFullName());
            System.out.println("Earn control point is not found");
            return null;
        }
        System.out.println("Earn control point is Ok");
        processing.scrollPageDown(point.y - 100);
        point = processing.find(toVisitsControlPoint, true, 10);
        if (point == null) {
            processing.saveScreenShot(toVisitsControlPoint.getFullName());
            System.out.println("toVisits control point is not found");
            return null;
        }
        processing.mouseLeftClick(point, toVisitsControlPoint.getRectangleClick("click"));
        System.out.println("toVisits control point is Ok");
        point = processing.find(hereVisitsControlPoint, true, 10);
        if (point == null) {
            processing.saveScreenShot(hereVisitsControlPoint.getFullName());
            System.out.println("hereVisits control point is not found");
            return null;
        }
        processing.scrollPageDown(point.y - 100);
        System.out.println("hereVisits control point is Ok");
        point = processing.find(visitsHereControlPoint, true, 10);
        if (point == null) {
            processing.saveScreenShot(visitsHereControlPoint.getFullName());
            System.out.println("visitsHere control point is not found");
            return null;
        }
        processing.scrollPageDown(point.y - 100);
        point.y = 100;
        System.out.println("visitsHere control point is Ok");
        return new Rectangle(point.x - 316, point.y + 10, 45, 75);
    }

    private boolean performingClicksOnLinks(Rectangle startField) {
        if (startField == null){
            return false;
        }
        Rectangle findField = new Rectangle(startField);
        Rectangle findId = new Rectangle(0, 0, 33, 30);
        Rectangle area = new Rectangle(0, 0, 460, 53);
        boolean isFirstLink = true;
        while (true) {
            while (findField.y < 768 - findField.height) {
                roundLincControlPoint.setFindRectangle(findField);
                Point pointRL = processing.findNotCount(roundLincControlPoint, true);
                if (pointRL == null) {
                    if (isFirstLink) {
                        processing.saveScreenShot(roundLincControlPoint.getFullName());
                        System.out.println("round-linc control point is not found");
                        return false; // Нет ничего
                    }
                    return true; // Последующих ссылок нет
                }
                findId.x = pointRL.x + 31;
                findId.y = pointRL.y + 11;
                idControlPoint.setFindRectangle(findId);
                Point pointId = processing.findNotCount(idControlPoint, true);
                if (pointId == null) {
                    if (isFirstLink) {
                        processing.saveScreenShot(idControlPoint.getFullName());
                        System.out.println("id control point is not found");
                        return false; // Нет ничего
                    }
                    findField.y = findField.y + 55;
                    continue; // Айдишника при ссылке нет
                }
                isFirstLink = false;
                processing.mouseLeftClick(pointRL, roundLincControlPoint.getRectangleClick("clickLinc"));
                area.x = pointRL.x + 60;
                area.y = pointRL.y - 12;
                visitControlPoint.setFindRectangle(area);
                filedControlPoint.setFindRectangle(area);
                int result = verifyGo(60);
                if (result == 0) {
                    return false;
                }
                if (result == 1 || result == 2) {
                    Point point = processing.find(completedControlPoint, true, 8);
                    if (point == null){
                        processing.mouseRandomMove(header);
                        point = processing.find(completedControlPoint, true, 90);
                        if (point == null) {
                            processing.saveScreenShot(completedControlPoint.getFullName());
                            System.out.println("completed control point is not found");
                        }else{
                            System.out.println("completed control point is Ok");
                        }
                    }
                    Point pointClose = processing.find(closeActivePageControlPoint, true, 2);
                    if (pointClose == null) {
                        processing.saveScreenShot(closeActivePageControlPoint.getFullName());
                        System.out.println("closeActivePage control point is not found");
                        return false;
                    }
                    processing.mouseLeftClick(pointClose, closeActivePageControlPoint.getRectangleClick("click"));
                    confirmCompletedControlPoint.setFindRectangle(area);
                    point = processing.find(confirmCompletedControlPoint, true, 40);
                    if (point == null) {
                        processing.saveScreenShot(confirmCompletedControlPoint.getFullName());
                        System.out.println("confirmCompleted control point is not found");
                    }else{
                        System.out.println("confirmCompleted control point is Ok");
                    }
                }
                findField.y = findField.y + 55;
            }
            processing.scrollPageDown(findField.y - startField.y);
            findField.y = startField.y;
        }
    }

    private int verifyGo(int count) {
        Point point;
        for (int i = 0; i <= count; i++) {
            pause(2000, 0);
            point = processing.findNotCount(closePassivePageControlPoint, true);
            if (point != null) {
                System.out.println("closePassivePage Ok");
                return 1; // Нашли пассивную страницу
            } else if (i == count) {
                processing.saveScreenShot(closePassivePageControlPoint.getFullName());
                System.out.println("closePassivePage control point is not found");
            }
            point = processing.findNotCount(visitControlPoint, true);
            if (point != null) {
                processing.mouseLeftClick(point, visitControlPoint.getRectangleClick("click"));
                System.out.println("visit Ok");
                return 2; // Нашли подтверждение перехода и подтвердили
            } else if (i == count) {
                processing.saveScreenShot(visitControlPoint.getFullName());
                System.out.println("visit control point is not found");
            }
            point = processing.findNotCount(filedControlPoint, true);
            if (point != null) {
                System.out.println("filed Ok");
                return 3; // Нашли ломанную ссылку
            } else if (i == count) {
                processing.saveScreenShot(filedControlPoint.getFullName());
                System.out.println("filed control point is not found");
            }
        }
        return 0; // Ничего не нашли
    }

    private boolean authorization() {
        Point point = processing.find(enterControlPoint, true, 30);
        if (point == null) {
            processing.saveScreenShot(enterControlPoint.getFullName());
            System.out.println("Enter control point is not found");
            return false;
        }
        Rectangle eMailRectangle = enterControlPoint.getRectangleClick("e-mail");
        processing.mouseLeftClick(point, eMailRectangle);
        processing.sendKey(E_MAIL);
        pause(700, 100);
        Rectangle passwordRectangle = enterControlPoint.getRectangleClick("password");
        processing.mouseLeftClick(point, passwordRectangle);
        processing.sendKey(PASSWORD);
        pause(700, 100);
        Rectangle enterRectangle = enterControlPoint.getRectangleClick("_");
        processing.mouseLeftClick(point, enterRectangle);
        pause(5000, 500);
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

    private void pause(long millis, long dif) {
        if (dif < 0) {
            dif = 0;
        }
        millis = (long) (millis + Math.random() * dif);
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

}
