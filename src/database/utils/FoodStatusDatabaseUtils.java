package database.utils;

import database.DBHelper;
import model.Food_Status;
import model.Payment_Method;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static database.DB_KEYS.*;

/**
 * Created by Michael on 4/21/2017.
 */
public class FoodStatusDatabaseUtils {

    DBHelper databaseHelper;

    public FoodStatusDatabaseUtils(DBHelper helper) {
        super();
        databaseHelper = helper;
    }

    public FoodStatusDatabaseUtils(String serverName, int port, String user, String password, String database) {
        super();
        databaseHelper = new DBHelper(serverName, port, user, password, database);
    }

    public ArrayList<Food_Status> getAllFoodStatuses() {
        String sql = "SELECT * FROM " + TABLE_FOOD_STATUSES + ";";

        ResultSet result;
        System.out.println(sql);
        ArrayList<Food_Status> list = new ArrayList<>();
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            while(result.next()) {
                int id = result.getInt(COLUMN_FOOD_STATUSES_ID);
                String name = result.getString(COLUMN_FOOD_STATUSES_NAME);
                String slug = result.getString(COLUMN_FOOD_STATUSES_SLUG);

                list.add(new Food_Status(id, slug, name));
            }

            result.close();
            databaseHelper.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Food Status List Query failed!", e);
            return null;
        }
    }

    public Food_Status getFoodStatusById(int id) {
        String sql = "SELECT * FROM " + TABLE_FOOD_STATUSES + " WHERE " + COLUMN_FOOD_STATUSES_ID + " = " + id + ";";

        ResultSet result;
        System.out.println(sql);
        Food_Status foodStatus = null;
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            if(result.next()) {
                int foodStatusId = result.getInt(COLUMN_FOOD_STATUSES_ID);
                String name = result.getString(COLUMN_FOOD_STATUSES_NAME);
                String slug = result.getString(COLUMN_FOOD_STATUSES_SLUG);

                foodStatus = new Food_Status(foodStatusId, slug, name);
            }

            result.close();
            databaseHelper.close();
            return foodStatus;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Food Status Query failed!", e);
            return null;
        }
    }

    public void deleteFoodStatusById(int id) {
        ResultSet result;
        String sql = "DELETE FROM " + TABLE_FOOD_STATUSES + " WHERE " + COLUMN_FOOD_STATUSES_ID + "=? " + ";";
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

    public void insertNewFoodStatus(String name, String slug) {
        String sql = "INSERT INTO " + TABLE_FOOD_STATUSES + " ( " + COLUMN_FOOD_STATUSES_SLUG + ", " + COLUMN_FOOD_STATUSES_NAME +
                ") VALUES (\"" + slug + "\", \"" + name + "\");";
        try {
            databaseHelper.open();
            databaseHelper.getStatement().execute(sql);
            databaseHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateFoodStatus(Food_Status foodStatus) {
        String sql = "START TRANSACTION;";
        databaseHelper.doQuery(sql);
        sql = "UPDATE " + TABLE_FOOD_STATUSES + " SET " + COLUMN_FOOD_STATUSES_SLUG + " =?, " + COLUMN_FOOD_STATUSES_NAME + "=?, " +
                " WHERE " + COLUMN_FOOD_STATUSES_ID + " =? ;";
        try {
            databaseHelper.open();
            PreparedStatement preparedStatement = databaseHelper.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, foodStatus.getSlug());
            preparedStatement.setString(2, foodStatus.getName());
            preparedStatement.setInt(3, foodStatus.getId());

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
}
