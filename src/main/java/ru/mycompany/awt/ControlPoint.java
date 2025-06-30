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
    private Rectangle findRectangle;
    private final Map<String, Rectangle> rectanglesMap;
    private final java.util.List<BufferedImage> sprites;

//    public ControlPoint(String siteName, String browser, String nameControlPoint, int widthSprite, int heightSprite , int rectangleFindX, int rectangleFindY, int widthRectangleFind, int heightRectangleFind){
//        this.siteName = siteName;
//        this.browser = browser;
//        this.nameControlPoint = nameControlPoint;
//        this.widthSprite = widthSprite;
//        this.heightSprite = heightSprite;
//        this.findRectangle = new Rectangle(rectangleFindX, rectangleFindY, widthRectangleFind, heightRectangleFind);
//        this.rectanglesMap = new HashMap<>();
//        this.sprites = SQLiteProvider.getInstance().getSprites(siteName, browser, nameControlPoint);
//    }

    public ControlPoint(String siteName, String browser, String nameControlPoint, int widthSprite, int heightSprite, Rectangle findRectangle){
        this.siteName = siteName;
        this.browser = browser;
        this.nameControlPoint = nameControlPoint;
        this.widthSprite = widthSprite;
        this.heightSprite = heightSprite;
        this.findRectangle = findRectangle;
        this.rectanglesMap = new HashMap<>();
        this.sprites = SQLiteProvider.getInstance().getSprites(siteName, browser, nameControlPoint);
    }

    public void setFindRectangle(Rectangle newFindRectangle){
        this.findRectangle = newFindRectangle;
    }

    public int getWidthSprite(){
        return widthSprite;
    }

    public  int getHeightSprite(){
        return heightSprite;
    }

    public Rectangle getFindRectangle(){
        return findRectangle;
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
