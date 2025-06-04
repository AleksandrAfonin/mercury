package ru.mycompany.awt;

import ru.mycompany.examples.User;

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
}
