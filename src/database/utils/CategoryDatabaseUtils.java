package database.utils;

import database.DBHelper;
import model.Category;

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
public class CategoryDatabaseUtils {

    DBHelper databaseHelper;

    public CategoryDatabaseUtils(DBHelper helper) {
        super();
        databaseHelper = helper;
    }

    public CategoryDatabaseUtils(String serverName, int port, String user, String password, String database) {
        super();
        databaseHelper = new DBHelper(serverName, port, user, password, database);
    }

    public ArrayList<Category> getAllCategory() {
        String sql = "SELECT * FROM " + TABLE_CATEGORIES + ";";

        ResultSet result;
        System.out.println(sql);
        ArrayList<Category> list = new ArrayList<>();
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            while(result.next()) {
                int id = result.getInt(COLUMN_CATEGORIES_ID);
                String name = result.getString(COLUMN_CATEGORIES_NAME);
                String slug = result.getString(COLUMN_CATEGORIES_SLUG);

                list.add(new Category(id, slug, name));
            }

            result.close();
            databaseHelper.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Categories List Query failed!", e);
            return null;
        }
    }

    public Category getCategoryById(int id) {
        String sql = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_CATEGORIES_ID + " = " + id + ";";

        ResultSet result;
        System.out.println(sql);
        Category category = null;
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            if(result.next()) {
                int categoryId = result.getInt(COLUMN_CATEGORIES_ID);
                String name = result.getString(COLUMN_CATEGORIES_NAME);
                String slug = result.getString(COLUMN_CATEGORIES_SLUG);

                category = new Category(categoryId, slug, name);
            }

            result.close();
            databaseHelper.close();
            return category;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Category Query failed!", e);
            return null;
        }
    }


    public Category getCategoryByName(String nama) {
        String sql = "SELECT * FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_CATEGORIES_NAME + " = '" + nama + "';";

        ResultSet result;
        System.out.println(sql);
        Category category = null;
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            if(result.next()) {
                int categoryId = result.getInt(COLUMN_CATEGORIES_ID);
                String name = result.getString(COLUMN_CATEGORIES_NAME);
                String slug = result.getString(COLUMN_CATEGORIES_SLUG);

                category = new Category(categoryId, slug, name);
            }

            result.close();
            databaseHelper.close();
            return category;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Category Query failed!", e);
            return null;
        }
    }


    public void deleteCategoryById(int id) {
        ResultSet result;
        String sql = "DELETE FROM " + TABLE_CATEGORIES + " WHERE " + COLUMN_CATEGORIES_ID + "=? " + ";";
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

    public void insertNewCategory(String name, String slug) {
        String sql = "INSERT INTO " + TABLE_CATEGORIES + " ( " + COLUMN_CATEGORIES_SLUG + ", " + COLUMN_CATEGORIES_NAME +
                ") VALUES (\"" + slug + "\", \"" + name + "\");";
        try {
            databaseHelper.open();
            databaseHelper.getStatement().execute(sql);
            databaseHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updateCategory(Category category) {
        String sql = "START TRANSACTION;";
        try {
            databaseHelper.open();
            databaseHelper.doQuery(sql);
            sql = "UPDATE " + TABLE_CATEGORIES + " SET " + COLUMN_CATEGORIES_SLUG + " =?, " + COLUMN_CATEGORIES_NAME + "=? " +
                " WHERE " + COLUMN_CATEGORIES_ID + " =? ;";

            PreparedStatement preparedStatement = databaseHelper.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, category.getSlug());
            preparedStatement.setString(2, category.getName());
            preparedStatement.setInt(3, category.getId());

            preparedStatement.execute();

            sql = "COMMIT";
            databaseHelper.doQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            sql = "ROLLBACK ";
            databaseHelper.doQuery(sql);
        }

        databaseHelper.close();

    }

}
