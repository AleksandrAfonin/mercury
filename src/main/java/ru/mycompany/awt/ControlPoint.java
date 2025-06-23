package ru.mycompany.awt;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class ControlPoint {
    private final String siteName;
    private final String browser;
    private final String nameControlPoint;
    private final int widthSprite;
    private final int heightSprite;
    private final Map<String, Rectangle> rectanglesMap;
    private final java.util.List<BufferedImage> sprites;

    public ControlPoint(String siteName, String browser, String nameControlPoint, int widthSprite, int heightSprite , int rectangleDifX, int rectangleDifY, int widthRectangle, int heightRectangle){
        this.siteName = siteName;
        this.browser = browser;
        this.nameControlPoint = nameControlPoint;
        this.widthSprite = widthSprite;
        this.heightSprite = heightSprite;
        this.rectanglesMap = new HashMap<>();
        this.rectanglesMap.put("_", new Rectangle(rectangleDifX, rectangleDifY, widthRectangle, heightRectangle));
        this.sprites = SQLiteProvider.getInstance().getSprites(siteName, browser, nameControlPoint);
    }

    public ControlPoint(String siteName, String browser, String nameControlPoint, int widthSprite, int heightSprite){
        this.siteName = siteName;
        this.browser = browser;
        this.nameControlPoint = nameControlPoint;
        this.widthSprite = widthSprite;
        this.heightSprite = heightSprite;
        this.rectanglesMap = new HashMap<>();
        this.sprites = SQLiteProvider.getInstance().getSprites(siteName, browser, nameControlPoint);
    }

    public int getWidthSprite(){
        return widthSprite;
    }

    public  int getHeightSprite(){
        return heightSprite;
    }

    public Rectangle getRectangleClick(String nameRectangle){
            return rectanglesMap.get(nameRectangle);
    }

    public java.util.List<BufferedImage> getImages(){
        return sprites;
    }

    public String getFullName(){
        return siteName + "_" + browser + "_" + nameControlPoint + "_" + widthSprite + "x" + heightSprite + ".png";
    }

    public void addRectangleClick(String nameRectangle, Rectangle rectangleClick){
        if (rectanglesMap.containsKey(nameRectangle)){
            System.out.println("Already exist !");
            return;
        }
        rectanglesMap.put(nameRectangle, rectangleClick);
    }
}
