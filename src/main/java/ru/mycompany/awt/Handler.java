package ru.mycompany.awt;

import org.openqa.selenium.WebDriver;

public interface Handler {
    void run();
    WebDriver getWebDriver();
    void setProperty(WebDriver webDriver, Processing processing, User user);
}
