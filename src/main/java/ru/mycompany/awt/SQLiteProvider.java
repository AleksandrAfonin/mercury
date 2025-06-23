package ru.mycompany.awt;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SQLiteProvider {
    private static SQLiteProvider sqLiteProvider;
    private final String DATABASE_URL = "jdbc:sqlite:database/mercury.sqlite";
    private Connection connection;
    private String GET_NAMES_SITES = "SELECT * FROM sites ORDER BY sitename;";
    private PreparedStatement getNamesSitesStatement;
    private final String GET_ACCOUNTS_LIST = """
          SELECT accounts.id, login, password, browser, handler, nexttime, intervalminutes FROM accounts, sites 
          WHERE accounts.sitename = sites.id 
          AND sites.sitename = ? AND accounts.isenable = 'true';""";
    private PreparedStatement getAccountLislStatement;
    private final String CHECK_IMAGE_IN_DELIONIX_TABLE = "SELECT * FROM delionix WHERE name = ? AND content = ?;";
    private PreparedStatement checkImageInDelionixTableStatement;
    private final String CHECK_IMAGE_IN_PROFIT_CENTR_TABLE = "SELECT * FROM profitcentr WHERE name = ? AND content = ?;";
    private PreparedStatement checkImageInProfitCentrTableStatement;
    private final String CHECK_IMAGE_IN_SEO_CLUB_TABLE = "SELECT * FROM seoclub WHERE name = ? AND content = ?;";
    private PreparedStatement checkImageInSeoClubTableStatement;
    private final String SET_NEXT_TIME = "UPDATE accounts SET nexttime = ? WHERE id = ?;";
    private PreparedStatement setNextTimeStatement;
    private final String GET_SPRITES = "SELECT data FROM sprites WHERE sitename = ? AND browser = ? AND name = ?;";
    private PreparedStatement getSpritesStatement;
    private final String SAVE_PIC = "INSERT INTO sprites (sitename, browser, name, data) VALUES (?, ?, ?, ?);";
    private PreparedStatement savePicStatement;

    private SQLiteProvider() {
    }

    public static SQLiteProvider getInstance() {
        if (sqLiteProvider == null) {
            sqLiteProvider = new SQLiteProvider();
            sqLiteProvider.initialise();
        }
        return sqLiteProvider;
    }

    private void initialise() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
            getNamesSitesStatement = connection.prepareStatement(GET_NAMES_SITES);
            getAccountLislStatement = connection.prepareStatement(GET_ACCOUNTS_LIST);
            checkImageInDelionixTableStatement = connection.prepareStatement(CHECK_IMAGE_IN_DELIONIX_TABLE);
            checkImageInProfitCentrTableStatement = connection.prepareStatement(CHECK_IMAGE_IN_PROFIT_CENTR_TABLE);
            checkImageInSeoClubTableStatement = connection.prepareStatement(CHECK_IMAGE_IN_SEO_CLUB_TABLE);
            setNextTimeStatement = connection.prepareStatement(SET_NEXT_TIME);
            getSpritesStatement = connection.prepareStatement(GET_SPRITES);
            savePicStatement = connection.prepareStatement(SAVE_PIC);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<String> getNameSites() {
        List<String> nameSites = new ArrayList<>();
        try {
            ResultSet resultSet = getNamesSitesStatement.executeQuery();
            while (resultSet.next()) {
                nameSites.add(resultSet.getString("sitename"));
            }
            return nameSites;
        } catch (SQLException e) {
            e.printStackTrace();
            return nameSites;
        }
    }

    public List<User> getAccountsList(String siteName){
        List<User> accountList = new ArrayList<>();
        try{
            getAccountLislStatement.setString(1, siteName);
            ResultSet resultSet = getAccountLislStatement.executeQuery();
            while (resultSet.next()){
                accountList.add(new User(
                        resultSet.getInt("id"),
                        resultSet.getString("login"),
                        resultSet.getString("password"),
                        resultSet.getString("browser"),
                        resultSet.getString("handler"),
                        resultSet.getLong("nexttime"),
                        resultSet.getInt("intervalminutes")
                ));
            }
            return accountList;
        }catch (Exception e){
            e.printStackTrace();
            return accountList;
        }
    }

    public boolean checkImageInDelionixTable(String name, String value){
        if (value == null || value.isBlank() || name == null || name.isBlank()){
            return false;
        }
        try{
            checkImageInDelionixTableStatement.setString(1, name);
            checkImageInDelionixTableStatement.setString(2, value);
            ResultSet resultSet = checkImageInDelionixTableStatement.executeQuery();
            return resultSet.next();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkImageInProfitCentrTable(String name, String value){
        if (value == null || value.isBlank() || name == null || name.isBlank()){
            return false;
        }
        try{
            checkImageInProfitCentrTableStatement.setString(1, name);
            checkImageInProfitCentrTableStatement.setString(2, value);
            ResultSet resultSet = checkImageInProfitCentrTableStatement.executeQuery();
            return resultSet.next();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public boolean checkImageInSeoClubTable(String name, String value){
        if (value == null || value.isBlank() || name == null || name.isBlank()){
            return false;
        }
        try{
            checkImageInSeoClubTableStatement.setString(1, name);
            checkImageInSeoClubTableStatement.setString(2, value);
            ResultSet resultSet = checkImageInSeoClubTableStatement.executeQuery();
            return resultSet.next();
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    public void setNextTime(User user, long nextTime){
        try{
            setNextTimeStatement.setLong(1, nextTime);
            setNextTimeStatement.setInt(2, user.getId());
            setNextTimeStatement.executeUpdate();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public List<BufferedImage> getSprites(String sitename, String browser, String name){
        List<BufferedImage> sprites = new ArrayList<>();
        try{
            getSpritesStatement.setString(1, sitename);
            getSpritesStatement.setString(2, browser);
            getSpritesStatement.setString(3, name);
            ResultSet resultSet = getSpritesStatement.executeQuery();
            while (resultSet.next()){
                byte[] bytes = resultSet.getBytes("data");
                sprites.add(ImageIO.read(new ByteArrayInputStream(bytes)));
            }
            return sprites;
        } catch (SQLException | IOException e) {
            return sprites;
        }
    }

    public void picToDB(File path, String sitename, String browser, String name){
        List<BufferedImage> bufferedImages = getSprites(sitename, browser, name);
        BufferedImage image;
        try {
            File[] files = path.listFiles();
            image = ImageIO.read(files[0]);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (BufferedImage im : bufferedImages){
            if (compareImage(im, image)){
                System.out.println("exist");
                return;
            }
        }
        try {
            savePicStatement.setString(1, sitename);
            savePicStatement.setString(2, browser);
            savePicStatement.setString(3, name);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            savePicStatement.setBytes(4, bytes);
            savePicStatement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Ok");
    }

    public void insertSprite(BufferedImage image, String[] strings){
        List<BufferedImage> bufferedImages = getSprites(strings[0], strings[1], strings[2]);
        for (BufferedImage im : bufferedImages){
            if (compareImage(im, image)){
                System.out.println("exist");
                return ;
            }
        }
        try {
            savePicStatement.setString(1, strings[0]);
            savePicStatement.setString(2, strings[1]);
            savePicStatement.setString(3, strings[2]);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            savePicStatement.setBytes(4, bytes);
            savePicStatement.execute();
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Ok");
    }

    private boolean compareImage(BufferedImage im, BufferedImage image){
        int widthImage = image.getWidth();
        int heightImage = image.getHeight();
        int width = im.getWidth();
        int height = im.getHeight();
        if (widthImage == width && heightImage == height){
            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    if (image.getRGB(j, i) != im.getRGB(j, i)){
                        return false;
                    }
                }
            }
            return true;
        }
        return false;
    }
}
