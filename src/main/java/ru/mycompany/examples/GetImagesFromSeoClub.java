package ru.mycompany.examples;

import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchWindowException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.edge.EdgeDriver;

import java.util.List;

public class GetImagesFromSeoClub {
  public static void main(String[] args) {
    SQLiteProvider sqLiteProvider = SQLiteProvider.getInstance();

    EdgeDriver driver = new EdgeDriver();

    String nameImage; // Название картинки nameImage
    String picture;   // Собственно сама картинка

    int count = 20;
    try {
      while (count >= 0) {
        driver.get("https://seoclub.su/login");
        pauseSec(10);
        WebElement webElement = driver.findElement(By.className("out-capcha-title")).findElement(By.tagName("b"));
        nameImage = webElement.getText();
        nameImage = nameImage.substring(nameImage.lastIndexOf(" ") + 1);

        List<WebElement> webElements = driver.findElements(By.xpath("//*/tr/td[@colspan='2']/div[@class='out-capcha']/label[@class='out-capcha-lab']"));
//        System.out.println(webElements.size());
//        pauseSec(60);
        for (WebElement element : webElements) {
          WebElement elementInput = element.findElement(By.tagName("input"));
          boolean isSelected = elementInput.isSelected();
          if (isSelected) {
            picture = element.getAttribute("style");
            if (!sqLiteProvider.isExistImageInSeoClubTable(picture)) {
              sqLiteProvider.addImageInSeoClubTable(nameImage, picture);
              System.out.println(nameImage + ", added.");
              count = 20;
            }
          }
        }
        System.out.println("NEXT => " + count--);
      }
    }catch (NoSuchWindowException ignored){
    }
    driver.quit();
  }

  private static void pauseSec(int sec) {
    try {
      Thread.sleep(sec * 1000L);
    } catch (InterruptedException ignored) {
    }
  }
}
