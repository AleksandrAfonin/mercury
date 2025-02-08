package ru.mycompany.examples;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SQLiteProvider {
  private static SQLiteProvider sqLiteProvider;
  private final String DATABASE_URL = "jdbc:sqlite:database/mercury.sqlite";
  private Connection connection;
  private final String GET_ACCOUNTS_LIST = """
          SELECT accounts.id, login, password, browser, handler, nexttime, intervalminutes FROM accounts, sites 
          WHERE accounts.sitename = sites.id 
          AND sites.sitename = ? AND accounts.isenable = 'true';""";
  private PreparedStatement getAccountLislStatement;
  private final String IS_EXIST_IMAGE_IN_IMAGES_TABLE = "SELECT * FROM images WHERE content = ?;";
  private PreparedStatement isExistImageInImagesTableStatement;
  private final String IS_EXIST_IMAGE_IN_PROFIT_CENTR_TABLE = "SELECT * FROM profitcentr WHERE content = ?;";
  private PreparedStatement isExistImageInProfitCentrTableStatement;
  private final String ADD_IMAGE_IN_PROFIT_CENTR_TABLE = "INSERT INTO profitcentr (name, content) VALUES (?, ?);";
  private PreparedStatement addImageInProfitCentrTableStatement;
  private final String CHECK_IMAGE_IN_PROFIT_CENTR_TABLE = "SELECT * FROM profitcentr WHERE name = ? AND content = ?;";
  private PreparedStatement checkImageInProfitCentrTableStatement;
  private final String IS_EXIST_IMAGE_IN_SEO_CLUB_TABLE = "SELECT * FROM seoclub WHERE content = ?;";
  private PreparedStatement isExistImageInSeoClubTableStatement;
  private final String ADD_IMAGE_IN_SEO_CLUB_TABLE = "INSERT INTO seoclub (name, content) VALUES (?, ?);";
  private PreparedStatement addImageInSeoClubTableStatement;
  private final String ADD_IMAGE_IN_DELIONIX_TABLE = "INSERT INTO delionix (name, content) VALUES (?, ?);";
  private PreparedStatement addImageInDelionixTableStatement;
  private final String IS_EXIST_IMAGE_IN_DELIONIX_TABLE = "SELECT * FROM delionix WHERE content = ?;";
  private PreparedStatement isExistImageInDelionixTableStatement;
  private final String CHECK_IMAGE_IN_DELIONIX_TABLE = "SELECT * FROM delionix WHERE name = ? AND content = ?;";
  private PreparedStatement checkImageInDelionixTableStatement;
  private final String CHECK_IMAGE_IN_SEO_CLUB_TABLE = "SELECT * FROM seoclub WHERE name = ? AND content = ?;";
  private PreparedStatement checkImageInSeoClubTableStatement;
  private final String SET_NEXT_TIME = "UPDATE accounts SET nexttime = ? WHERE id = ?;";
  private PreparedStatement setNextTimeStatement;


  private SQLiteProvider(){}

  public static SQLiteProvider getInstance(){
    if (sqLiteProvider == null){
      sqLiteProvider = new SQLiteProvider();
      sqLiteProvider.initialise();
    }
    return sqLiteProvider;
  }

  private void initialise(){
    try{
      connection = DriverManager.getConnection(DATABASE_URL);
      getAccountLislStatement = connection.prepareStatement(GET_ACCOUNTS_LIST);
      isExistImageInImagesTableStatement = connection.prepareStatement(IS_EXIST_IMAGE_IN_IMAGES_TABLE);
      isExistImageInProfitCentrTableStatement = connection.prepareStatement(IS_EXIST_IMAGE_IN_PROFIT_CENTR_TABLE);
      addImageInProfitCentrTableStatement = connection.prepareStatement(ADD_IMAGE_IN_PROFIT_CENTR_TABLE);
      checkImageInProfitCentrTableStatement = connection.prepareStatement(CHECK_IMAGE_IN_PROFIT_CENTR_TABLE);
      isExistImageInSeoClubTableStatement = connection.prepareStatement(IS_EXIST_IMAGE_IN_SEO_CLUB_TABLE);
      addImageInSeoClubTableStatement = connection.prepareStatement(ADD_IMAGE_IN_SEO_CLUB_TABLE);
      isExistImageInDelionixTableStatement = connection.prepareStatement(IS_EXIST_IMAGE_IN_DELIONIX_TABLE);
      addImageInDelionixTableStatement = connection.prepareStatement(ADD_IMAGE_IN_DELIONIX_TABLE);
      checkImageInDelionixTableStatement = connection.prepareStatement(CHECK_IMAGE_IN_DELIONIX_TABLE);
      checkImageInSeoClubTableStatement = connection.prepareStatement(CHECK_IMAGE_IN_SEO_CLUB_TABLE);
      setNextTimeStatement = connection.prepareStatement(SET_NEXT_TIME);
    }catch(Exception e){
      e.printStackTrace();
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

  public List<User> getAccountsList(String siteName){
    try{
      getAccountLislStatement.setString(1, siteName);
      ResultSet resultSet = getAccountLislStatement.executeQuery();
      List<User> accountList = new ArrayList<>();
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
      return null;
    }
  }

  public boolean isExistImageInImagesTable(String value){
    if (value == null || value.isBlank()){
      return false;
    }
    try{
      isExistImageInImagesTableStatement.setString(1, value);
      ResultSet resultSet = isExistImageInImagesTableStatement.executeQuery();
      return resultSet.next();
    }catch (Exception e){
      e.printStackTrace();
      return false;
    }
  }

  public boolean isExistImageInDelionixTable(String value){
    if (value == null || value.isBlank()){
      return false;
    }
    try{
      isExistImageInDelionixTableStatement.setString(1, value);
      ResultSet resultSet = isExistImageInDelionixTableStatement.executeQuery();
      return resultSet.next();
    }catch (Exception e){
      e.printStackTrace();
      return false;
    }
  }

  public boolean addImageInDelionixTable(String name, String value){
    if (value == null || value.isBlank() || name == null || name.isBlank()){
      return false;
    }
    try{
      addImageInDelionixTableStatement.setString(1, name);
      addImageInDelionixTableStatement.setString(2, value);
      addImageInDelionixTableStatement.executeUpdate();
      return true;
    }catch (Exception e){
      e.printStackTrace();
      return false;
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

  public boolean isExistImageInProfitCentrTable(String value){
    if (value == null || value.isBlank()){
      return false;
    }
    try{
      isExistImageInProfitCentrTableStatement.setString(1, value);
      ResultSet resultSet = isExistImageInProfitCentrTableStatement.executeQuery();
      return resultSet.next();
    }catch (Exception e){
      e.printStackTrace();
      return false;
    }
  }

  public boolean addImageInProfitCentrTable(String name, String value){
    if (value == null || value.isBlank() || name == null || name.isBlank()){
      return false;
    }
    try{
      addImageInProfitCentrTableStatement.setString(1, name);
      addImageInProfitCentrTableStatement.setString(2, value);
      addImageInProfitCentrTableStatement.executeUpdate();
      return true;
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

  public boolean isExistImageInSeoClubTable(String value){
    if (value == null || value.isBlank()){
      return false;
    }
    try{
      isExistImageInSeoClubTableStatement.setString(1, value);
      ResultSet resultSet = isExistImageInSeoClubTableStatement.executeQuery();
      return resultSet.next();
    }catch (Exception e){
      e.printStackTrace();
      return false;
    }
  }

  public boolean addImageInSeoClubTable(String name, String value){
    if (value == null || value.isBlank() || name == null || name.isBlank()){
      return false;
    }
    try{
      addImageInSeoClubTableStatement.setString(1, name);
      addImageInSeoClubTableStatement.setString(2, value);
      addImageInSeoClubTableStatement.executeUpdate();
      return true;
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
}
