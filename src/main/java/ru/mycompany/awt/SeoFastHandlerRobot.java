package ru.mycompany.awt;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.interactions.Actions;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SeoFastHandlerRobot implements Handler {
    private final String sitename;
    private String browser;
    private final WebDriver WEB_DRIVER;
    private final String E_MAIL;    // alsupp@yandex.ru
    private final String PASSWORD;  // 19b650660b
    private final Actions ACTIONS;
    private final String URL;
    private final Processing processing;
    private final Rectangle header;

    private final List<BufferedImage> email;
    private final ControlPoint enterControlPoint;
    private final List<BufferedImage> visits;
    private final List<BufferedImage> roundLinc;
    private final List<BufferedImage> id;
    private final List<BufferedImage> to_visit;
    private final List<BufferedImage> closeActivePage;
    private final List<BufferedImage> closePassivePage;
    private final List<BufferedImage> completed;

    public SeoFastHandlerRobot(WebDriver webDriver, User user) throws AWTException {
        this.sitename = "seofast";
        this.WEB_DRIVER = webDriver;
        this.ACTIONS = new Actions(webDriver, Duration.ofSeconds(1));
        this.processing = new Processing(webDriver, ACTIONS);
        this.E_MAIL = user.getLogin();
        this.PASSWORD = user.getPassword();
        this.header = new Rectangle(270, 2, 900, 30);
        this.URL = "https://seo-fast.ru/login";

        browser = processing.getBrowserName(webDriver);

        this.email = SQLiteProvider.getInstance().getSprites(sitename, browser, "e-mail");
        this.enterControlPoint = new ControlPoint(sitename, browser, "enter", 15, 17, -130, -4, 265, 26);
        this.enterControlPoint.addRectangleClick("e-mail", new Rectangle(-140, -78, 286, 18));
        this.enterControlPoint.addRectangleClick("password", new Rectangle(-140, -42, 286, 18));
        this.visits = SQLiteProvider.getInstance().getSprites(sitename, browser,"visits");
        this.roundLinc = SQLiteProvider.getInstance().getSprites(sitename, browser,"round-linc");
        this.id = SQLiteProvider.getInstance().getSprites(sitename, browser,"id");
        this.to_visit = SQLiteProvider.getInstance().getSprites(sitename, browser,"to-visit");
        this.closeActivePage = SQLiteProvider.getInstance().getSprites(sitename, browser,"close-active-page");
        this.closePassivePage = SQLiteProvider.getInstance().getSprites(sitename, browser,"close-passive-page");
        this.completed = SQLiteProvider.getInstance().getSprites(sitename, browser,"completed");

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
        pause(10000, 0);
        if (!authorization()) {
            WEB_DRIVER.quit();
            return;
        }
        processing.mouseScrollDown(3);
        pause(2000, 1000);
        Point point = processing.find(visits, true, 5);
        if (point == null) {
            WEB_DRIVER.quit();
            return;
        }
        processing.mouseLeftClick(point, 80, 10);
        pause(15000, 5000);

        performingClicks();
        WEB_DRIVER.get(WEB_DRIVER.getCurrentUrl());
        pause(15000, 5000);
        performingClicks();
//===============================
        pause(5000, 0);
        WEB_DRIVER.quit();
    }


    private void performingClicks() {
        boolean isFinish = false;
        Point point = new Point();
        while (true) {
            point.x = 550;
            point.y = 400;
            processing.mouseMove(point);
            processing.mouseScrollDown(6);
            pause(1000, 1000);
            List<Point> points = getPointsList();
            if (points.isEmpty()) {
                if (isFinish) {
                    return;
                }
                isFinish = true;
                continue;
            }
            isFinish = false;
            for (Point p : points) {
                p.x = p.x + 65;
                processing.mouseLeftClick(p, 8, 8);
                if (!checkVisit(p)) {
                    continue;
                }
                processing.find(completed, header, true, 90);
                closeAllTabs();
                processing.refreshScreen();
            }
        }
    }

    private boolean checkVisit(Point p) {
        Rectangle rectangle = new Rectangle(p.x, p.y, 250, 30);
        for (int i = 0; i < 10; i++) {
            pause(2000, 0);
            Point point = processing.find(to_visit, rectangle, true, 1);
            if (point != null) {
                pause(1000, 1000);
                processing.mouseMove(point);
                processing.mouseLeftClick(point, 200, 10);
                pause(10000, 0);
                return true;
            }
            point = processing.find(closeActivePage, header, true, 1);
            if (point != null) {
                return true;
            }
        }
        return false;
    }

    private List<Point> getPointsList() {
        List<Point> points = new ArrayList<>();
        int yPoint = processing.getFullScreen().height;
        Rectangle field = new Rectangle(300, 0, 100, yPoint);
        while (true) {
            Point point = processing.find(roundLinc, field, true, 1);
            if (point == null) {
                break;
            }
            Rectangle rectangle = new Rectangle(point.x, point.y, 100, 32);
            Point pointId = processing.find(id, rectangle, true, 1);
            if (pointId == null) {
                field.y = point.y + 10;
                field.height = yPoint - field.y;
                continue;
            }
            points.add(point);
            field.y = point.y + 10;
            field.height = yPoint - field.y;
        }
        return points;
    }

    private void closeAllTabs() {
        while (true) {
            Point point = processing.find(closeActivePage, header, true, 2);
            if (point == null) {
                return;
            }
            processing.mouseLeftClick(point, 7, 7);
            pause(4000, 2000);
        }
    }
// TODO in processing
    private boolean authorization() {
        Point point = processing.find(enterControlPoint, true, 5);
        if (point == null) {
            processing.saveScreenShot(enterControlPoint.getFullName());
            System.out.println("Enter control point is not found");
            return false;
        }
        Rectangle eMailRectangle = enterControlPoint.getRectangleClick("e-mail");
        processing.mouseLeftClick(point, eMailRectangle);
        processing.sendKey(E_MAIL);
        pause(500, 500);
        Rectangle passwordRectangle = enterControlPoint.getRectangleClick("password");
        processing.mouseLeftClick(point, passwordRectangle);
        processing.sendKey(PASSWORD);
        pause(500, 500);
        Rectangle enterRectangle = enterControlPoint.getRectangleClick("_");
        processing.mouseLeftClick(point, enterRectangle);
        pause(5000, 5000);
        return true;
    }

    private boolean start() {
        try {
            WEB_DRIVER.manage().window().maximize();
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
