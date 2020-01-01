package database.utils;

import database.DBHelper;
import model.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static database.DB_KEYS.*;

/**
 * Created by Michael on 4/20/2017.
 */
public class UserDatabaseUtils {

    DBHelper databaseHelper;

    public UserDatabaseUtils(DBHelper helper) {
        super();
        databaseHelper = helper;
    }

    public UserDatabaseUtils(String serverName, int port, String user, String password, String database) {
        super();
        databaseHelper = new DBHelper(serverName, port, user, password, database);
    }

    public ArrayList<User> getAllUser() {
        String sql = "SELECT * FROM " + TABLE_USERS + ";";

        ResultSet result;
        System.out.println(sql);
        ArrayList<User> list = new ArrayList<>();
        try {
            databaseHelper.open();
            result = databaseHelper.getStatement().executeQuery(sql);

            //insert data to ArrayList

            while(result.next()) {
                int id = result.getInt(COLUMN_USERS_ID);
                String name = result.getString(COLUMN_USERS_NAME);
                String password = result.getString(COLUMN_USERS_PASSWORD);
                String secretCode = result.getString(COLUMN_USERS_SECRET_CODE);

                list.add(new User(id, name, password, secretCode));
            }

            result.close();
            databaseHelper.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get User List Query failed!", e);
            return list;
        }
    }

    public User getUserByNameAndPassword(String name, String password) {
        String sql = "SELECT * FROM " + TABLE_USERS + " WHERE " + COLUMN_USERS_NAME + " = \"" + name + "\" && " + COLUMN_USERS_PASSWORD + " = \"" + password + "\";";

        ResultSet result;
        System.out.println(sql);
        User user = null;
        try {
            databaseHelper.open();
            result = databaseHelper.getStatement().executeQuery(sql);

            if(result.next()) {
                int id = result.getInt(COLUMN_USERS_ID);
                String user_name = result.getString(COLUMN_USERS_NAME);
                String user_password = result.getString(COLUMN_USERS_PASSWORD);
                String secretCode = result.getString(COLUMN_USERS_SECRET_CODE);

                user = new User(id, user_name, user_password, secretCode);
            }

            result.close();
            databaseHelper.close();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get User List Query failed!", e);
            return null;
        }
    }

    public void insertNewUser(String name, String password, String secretCode) {
        String sql = "INSERT INTO " + TABLE_USERS + " ( " + COLUMN_USERS_NAME + ", " + COLUMN_USERS_PASSWORD + ", " + COLUMN_USERS_SECRET_CODE +
                ") VALUES (?,?,?);";
        try {
            databaseHelper.open();

            PreparedStatement preparedStatement = databaseHelper.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, password);
            preparedStatement.setString(3, secretCode);
            preparedStatement.execute();

            databaseHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateUser(User user) {
        String sql = "START TRANSACTION;";
        databaseHelper.doQuery(sql);
        sql = "UPDATE " + TABLE_USERS + " SET " + COLUMN_USERS_NAME + " =?, " + COLUMN_USERS_PASSWORD + "=?, " + COLUMN_USERS_SECRET_CODE + "=? " +
                " WHERE " + COLUMN_USERS_ID + " =? ;";
        try {
            databaseHelper.open();

            PreparedStatement preparedStatement = databaseHelper.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, user.getName());
            preparedStatement.setString(2, user.getPassword());
            preparedStatement.setString(3, user.getSecret_code());
            preparedStatement.setInt(4, user.getId());
            preparedStatement.execute();

            databaseHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
            sql = "ROLLBACK ";
            databaseHelper.doQuery(sql);
        }

        sql = "COMMIT";
        databaseHelper.doQuery(sql);
    }


    public void deleteUserById(int id) {
        ResultSet result;
        String sql = "DELETE FROM " + TABLE_USERS + " WHERE " + COLUMN_USERS_ID + "=? " + ";";
        try {
            databaseHelper.open();

            PreparedStatement preparedStatement = databaseHelper.getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();

            databaseHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}