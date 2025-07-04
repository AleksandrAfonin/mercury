package ru.mycompany.awt;


import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.interactions.Actions;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Processing {
    private BufferedImage screen;
    private BufferedImage subScreen;
    private final Robot robot;
    private final Rectangle fullScreen;
    private final Tesseract tesseract;
    private WebDriver WEB_DRIVER;
    private Actions ACTIONS;

    public Rectangle getFullScreen() {
        return fullScreen;
    }

    public void setScreen(BufferedImage screen) {
        this.screen = screen;
    }

        public Processing() throws AWTException {
        GraphicsDevice[] graphicsDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
        this.robot = new Robot(graphicsDevices[0]);
        this.fullScreen = new Rectangle(Toolkit.getDefaultToolkit().getScreenSize());
        this.tesseract = new Tesseract();
        this.tesseract.setDatapath("./tessdata");

    }

    public void setProperty(WebDriver webDriver, Actions actions){
        this.WEB_DRIVER = webDriver;
        this.ACTIONS = actions;
    }

    public String resolveWMRCaptcha() {
        int transformedColor;
        for (int i = 0; i < subScreen.getHeight(); i++) {
            for (int j = 0; j < subScreen.getWidth(); j++) {
                int color = subScreen.getRGB(j, i);
                int alpha = (color >> 24) & 0xFF;
                int green = (color >> 8) & 0xFF;
                if (green < 95) {
                    transformedColor = (alpha << 24) | (0);
                } else {
                    transformedColor = (alpha << 24) | (255 << 16) | (255 << 8) | 255;
                }
                subScreen.setRGB(j, i, transformedColor);
            }
        }
        String number;
        try {
            number = tesseract.doOCR(subScreen);
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
        return number;
    }

    public String resolveSarSeoCaptcha(Point basePoint) {
        int num1;
        int num2;
        int transformedColor;
        Rectangle rectangle = new Rectangle(basePoint.x - 110, basePoint.y - 14, 100, 35);
        while (true) {
            refreshScreen(rectangle);
            for (int i = 0; i < subScreen.getHeight(); i++) {
                for (int j = 0; j < subScreen.getWidth(); j++) {
                    int color = subScreen.getRGB(j, i);
                    int alpha = (color >> 24) & 0xFF;
                    int red = (color >> 16) & 0xFF;
                    int green = (color >> 8) & 0xFF;
                    int blue = color & 0xFF;
                    int generalColor = (red + green + blue) / 3;
                    if (generalColor < 250) {
                        transformedColor = (alpha << 24) | (0);
                    } else {
                        transformedColor = (alpha << 24) | (255 << 16) | (255 << 8) | 255;
                    }
                    subScreen.setRGB(j, i, transformedColor);
                }
            }
            try {
                ImageIO.write(subScreen, "png", new File("./image/3.png"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            String number;
            try {
                number = tesseract.doOCR(subScreen);
                number = number.trim();
            } catch (TesseractException e) {
                throw new RuntimeException(e);
            }
            String[] numbers = number.split("\\+", 2);
            try {
                num1 = Integer.parseInt(numbers[0].trim());
                num2 = Integer.parseInt(numbers[1].trim());
                break;
            } catch (NumberFormatException e) {
                Point point = new Point(basePoint.x - 100, basePoint.y - 10);
                mouseLeftClick(point, 60, 20);
                mouseMove(basePoint);
                robot.delay(10000);
            }
        }
        return String.valueOf(num1 + num2);
    }

    public void refreshScreen() {
        screen = robot.createScreenCapture(fullScreen);
        try {
            ImageIO.write(screen, "png", new File("./image/2.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void refreshScreen(Rectangle rectangle) {
        subScreen = robot.createScreenCapture(rectangle);
        try {
            ImageIO.write(subScreen, "png", new File("./image/3.png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void mouseScrollUp(int wheel) {
        robot.mouseWheel(Math.abs(wheel) * -1);
        robot.delay(500);
    }

    public void mouseScrollDown(int wheel) {
        robot.mouseWheel(Math.abs(wheel));
        robot.delay(500);
    }

    public void scrollPageDown(int pixels) {
        JavascriptExecutor javascriptExecutor = (JavascriptExecutor) WEB_DRIVER;
        javascriptExecutor.executeScript("window.scrollBy(0," + pixels + ")");
        robot.delay(2000);
    }

    public void sendKey(String string) {
        StringSelection ss = new StringSelection(string);
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, null);
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_V);
        robot.keyRelease(KeyEvent.VK_CONTROL);
    }

    public void mouseMove(Point point) {
        robot.mouseMove(point.x, point.y);
    }

    public void mouseMove(int x, int y) {
        robot.mouseMove(x, y);
    }

    public void mouseRandomMove(Rectangle moveArea){
        int x = (int) (moveArea.x + Math.random() * moveArea.width);
        int y = (int) (moveArea.y + Math.random() * moveArea.height);
        robot.mouseMove(x, y);
    }

    public void mouseLeftClick(Point point, Rectangle rectangle) {
        click(new Point(point.x + rectangle.x, point.y + rectangle.y), rectangle.width, rectangle.height);
    }

    public void mouseLeftClick(Point point, int difX, int difY) {
        click(new Point(point), difX, difY);
    }

    public void mouseLeftClick(int x, int y, int difX, int difY) {
        click(new Point(x, y), difX, difY);
    }

    private void click(Point secondPoint, int difX, int difY) {
        secondPoint.x = (int) (secondPoint.x + Math.random() * difX);
        secondPoint.y = (int) (secondPoint.y + Math.random() * difY);
        mouseMove(secondPoint);
        robot.mousePress(InputEvent.BUTTON1_DOWN_MASK);
        robot.delay(100);
        robot.mouseRelease(InputEvent.BUTTON1_DOWN_MASK);
    }

    public boolean clickInteractable(WebElement webElement, int count) {
        for (int i = 0; i < count; i++) {
            try {
                ACTIONS.click(webElement).perform();
                return true;
            } catch (ElementNotInteractableException e) {
                robot.delay(2000);
            }
        }
        return false;
    }

    public void clickElement(WebElement webElement){
        ACTIONS.click(webElement).perform();
    }

    public void sendKeys(WebElement webElement, String text){
        ACTIONS.sendKeys(webElement, text).perform();
    }

    public Point find(List<BufferedImage> images, boolean isRefresh, int count) {
        for (int i = 0; i < count; i++) {
            if (count > 1) {
                robot.delay(2000);
            }
            if (isRefresh) {
                refreshScreen();
            }
            if (screen == null) {
                return null;
            }
            for (BufferedImage image : images) {
                Point point = findImage(image, screen);
                if (point != null) {
                    return point;
                }
            }
        }
        return null;
    }

    public Point findNotCount(ControlPoint controlPoint, boolean isRefresh) {
        Rectangle rectangle = controlPoint.getFindRectangle();
        if (isRefresh) {
            refreshScreen(rectangle);
        }
        if (subScreen == null) {
            return null;
        }
        for (BufferedImage image : controlPoint.getImages()) {
            Point point = findImage(image, subScreen);
            if (point != null) {
                point.x = point.x + rectangle.x;
                point.y = point.y + rectangle.y;
                return point;
            }
        }
        return null;
    }

    public Point find(ControlPoint controlPoint, boolean isRefresh, int count) {
        return find(controlPoint.getImages(), controlPoint.getFindRectangle(), isRefresh, count);
    }

    public Point find(List<BufferedImage> images, Rectangle rectangle, boolean isRefresh, int count) {
        for (int i = 0; i < count; i++) {
            if (count > 1) {
                robot.delay(2000);
            }
            if (isRefresh) {
                refreshScreen(rectangle);
            }
            if (subScreen == null) {
                return null;
            }
            for (BufferedImage image : images) {
                Point point = findImage(image, subScreen);
                if (point != null) {
                    point.x = point.x + rectangle.x;
                    point.y = point.y + rectangle.y;
                    return point;
                }
            }

        }
        return null;
    }

    private Point findImage(BufferedImage image, BufferedImage screen) {
        for (int i = 0; i <= screen.getHeight() - image.getHeight(); i++) {
            for (int j = 0; j <= screen.getWidth() - image.getWidth(); j++) {
                if (compareImage(image, screen, j, i)) {
                    return new Point(j, i);
                }
            }
        }
        return null;
    }

    private boolean compareImage(BufferedImage image, BufferedImage screen, int xField, int yField) {
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                if (screen.getRGB(xField + j, yField + i) != image.getRGB(j, i)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean closeAllTabs(int count) {
        if (count <= 0) {//30
            count = 1;
        }
        for (int j = 0; j < count; j++) {
            Object[] windowsHandles = WEB_DRIVER.getWindowHandles().toArray();
            if (windowsHandles.length == 1) {
                return true;
            }
            robot.delay(1000);
            for (int i = 1; i < windowsHandles.length; i++) {
                try {
                    WEB_DRIVER.switchTo().window((String) windowsHandles[i]);
                    WEB_DRIVER.close();
                } catch (WebDriverException ignored) {
                }
            }
            WEB_DRIVER.switchTo().window((String) windowsHandles[0]);
            robot.delay(500);
        }
        return false;
    }

    public boolean isMore1TabsWithCount(int count) {
        for (int i = 0; i < count; i++) {
            robot.delay(2000);
            if (WEB_DRIVER.getWindowHandles().toArray().length > 1) {
                return true;
            }
        }
        return false;
    }

    public WebElement getElementByXpathWithCount(String xpath, int count) {
        int wait;
        if (count == 0) {
            wait = 0;
            count = 1;
        } else {
            wait = 2000;
        }
        WebElement webElement;
        for (int i = 0; i < count; i++) {
            robot.delay(wait);
            try {
                webElement = WEB_DRIVER.findElement(By.xpath(xpath));
                return webElement;
            } catch (NoSuchElementException ignored) {
            }
        }
        return null;
    }

    public WebElement getElementByXpathWithCount(WebElement element, String xpath, int count) {
        int wait;
        if (count == 0) {
            wait = 0;
            count = 1;
        } else {
            wait = 2000;
        }
        WebElement webElement;
        for (int i = 0; i < count; i++) {
            robot.delay(wait);
            try {
                webElement = element.findElement(By.xpath(xpath));
                return webElement;
            } catch (NoSuchElementException ignored) {
            }
        }
        return null;
    }

    public int oneElementFromXpaths(String[] xpaths, int count) {
        if (xpaths == null || xpaths.length == 0) {
            return -1;
        }
        WebElement element;
        for (int i = 0; i < count; i++) {
            robot.delay(2000);
            for (int j = 0; j < xpaths.length; j++) {
                element = getElementByXpathWithCount(xpaths[j], 1);
                if (element != null) {
                    return j;
                }
            }
        }
        return -1;
    }

    public boolean waitTimeOnTitlePage(String titleName, int maxCount) {
        for (int i = 0; i < maxCount; i++) {
            robot.delay(2000);
            String title = WEB_DRIVER.getTitle();
            if (titleName.equals(title)) {
                return true;
            }
        }
        return false;
    }

    public String getBrowserName(WebDriver webDriver) {
        if (webDriver instanceof ChromeDriver) {
            return Browser.CHROME.name();
        }
        if (webDriver instanceof EdgeDriver) {
            return Browser.EDGE.name();
        }
        return "";
    }

    public void saveScreenShot(String fullName) {
        File path = new File(new File(".", "image"), "im");
        File newFile = new File(path, fullName);
        refreshScreen();
        try {
            ImageIO.write(screen, "png", newFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public int findOneControlPointFromList(List<ControlPoint> controlPoints, int count){
        Point point;
        for (int i = 0; i <= count; i++) {
            robot.delay(2000);
            int number = 0;
            for (ControlPoint cp : controlPoints){
                point = findNotCount(cp, true);
                if (point != null) {
                    System.out.println(cp.getName() + " Ok");
                    return number; // Нашли
                } else if (i == count) {
                    saveScreenShot(cp.getFullName());
                    System.out.println(cp.getName() + " control point is not found");
                }
                number ++;
            }
        }
        return -1; // Ничего не нашли
    }
}
