package ru.mycompany.awt;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

public class ScalePic {
    public static void main(String[] args) {
        Image srcImage;
        File path = new File(new File(".", "image"), "im");
        File newFile = new File(path, "newImage.png");
        try {
            File[] files = path.listFiles();
            srcImage = ImageIO.read(files[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        int width = srcImage.getWidth(null) * 10;
        int height = srcImage.getHeight(null) * 10;
        BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = newImage.createGraphics();
        graphics2D.drawImage(srcImage.getScaledInstance(width, height, BufferedImage.TYPE_INT_RGB), 0, 0, null);
        graphics2D.dispose();
        try {
            ImageIO.write(newImage, "png", newFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//        SQLiteProvider.getInstance().picToDB(path, "seofast", Browser.CHROME.name(), "visits");
    }
}
