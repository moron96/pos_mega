package database.utils;

import database.DBHelper;
import model.Menu;

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
public class MenuDatabaseUtils {

    DBHelper databaseHelper;

    public MenuDatabaseUtils(DBHelper helper) {
        super();
        databaseHelper = helper;
    }

    public MenuDatabaseUtils(String serverName, int port, String user, String password, String database) {
        super();
        databaseHelper = new DBHelper(serverName, port, user, password, database);
    }

    public Menu getMenuById(int menu_id) {
        String sql = "SELECT * FROM " + TABLE_MENUS + " WHERE " + COLUMN_MENUS_ID + " = " + menu_id + ";";
        ResultSet result;
        System.out.println(sql);
        Menu menu = null;
        try {
            databaseHelper.open();
            result = databaseHelper.getStatement().executeQuery(sql);

            if(result.next()) {
                int id = result.getInt(COLUMN_MENUS_ID);
                String name = result.getString(COLUMN_MENUS_NAME);
                int categoryId = result.getInt(COLUMN_MENUS_CATEGORY_ID);
                double price = result.getDouble(COLUMN_MENUS_PRICE);
                String imageUrl = result.getString(COLUMN_MENUS_IMAGE_URL);

                menu = new Menu(id, name, categoryId, price, imageUrl);
            }

            result.close();
            databaseHelper.close();
            return menu;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Menu List Query failed!", e);
            return null;
        }
    }

    public Menu getMenuByName(String menu_name) {
        String sql = "SELECT * FROM " + TABLE_MENUS + " WHERE " + COLUMN_MENUS_NAME + " = \"" + menu_name + "\";";
        ResultSet result;
        System.out.println(sql);
        Menu menu = null;
        try {
            databaseHelper.open();
            result = databaseHelper.getStatement().executeQuery(sql);

            if(result.next()) {
                int id = result.getInt(COLUMN_MENUS_ID);
                String name = result.getString(COLUMN_MENUS_NAME);
                int categoryId = result.getInt(COLUMN_MENUS_CATEGORY_ID);
                double price = result.getDouble(COLUMN_MENUS_PRICE);
                String imageUrl = result.getString(COLUMN_MENUS_IMAGE_URL);

                menu = new Menu(id, name, categoryId, price, imageUrl);
            }

            result.close();
            databaseHelper.close();
            return menu;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Menu List Query failed!", e);
            return null;
        }
    }

    public ArrayList<Menu> getMenuList() {
        String sql = "SELECT * FROM " + TABLE_MENUS + ";";
        ResultSet result;
        System.out.println(sql);
        ArrayList<Menu> list = new ArrayList<>();
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            while(result.next()) {
                int id = result.getInt(COLUMN_MENUS_ID);
                String name = result.getString(COLUMN_MENUS_NAME);
                int categoryId = result.getInt(COLUMN_MENUS_CATEGORY_ID);
                double price = result.getDouble(COLUMN_MENUS_PRICE);
                String imageUrl = result.getString(COLUMN_MENUS_IMAGE_URL);

                list.add(new Menu(id, name, categoryId, price, imageUrl));
            }

            result.close();
            databaseHelper.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Menu List Query failed!", e);
            return null;
        }
    }

    public ArrayList<Menu> getMenuListByCategoryId(int category_id) {
        String sql = "SELECT * FROM " + TABLE_MENUS + " WHERE " + COLUMN_MENUS_CATEGORY_ID + " = " + category_id + " order by " + COLUMN_MENUS_NAME + ";";
        ResultSet result;
        System.out.println(sql);
        ArrayList<Menu> list = new ArrayList<>();
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            while(result.next()) {
                int id = result.getInt(COLUMN_MENUS_ID);
                String name = result.getString(COLUMN_MENUS_NAME);
                int categoryId = result.getInt(COLUMN_MENUS_CATEGORY_ID);
                double price = result.getDouble(COLUMN_MENUS_PRICE);
                String imageUrl = result.getString(COLUMN_MENUS_IMAGE_URL);

                list.add(new Menu(id, name, categoryId, price, imageUrl));
            }

            result.close();
            databaseHelper.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Menu List Query failed!", e);
            return null;
        }
    }



    public ArrayList<Menu> getMenuListByCategoryIdandname(int category_id,String search) {

        String cari = "%"+search+"%";

        String sql = "SELECT * FROM " + TABLE_MENUS + " WHERE " + COLUMN_MENUS_CATEGORY_ID + " = " + category_id + " and "+COLUMN_MENUS_NAME+" like '" + cari + "' order by " + COLUMN_MENUS_NAME + ";";
        ResultSet result;
        System.out.println(sql);
        ArrayList<Menu> list = new ArrayList<>();
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            while(result.next()) {
                int id = result.getInt(COLUMN_MENUS_ID);
                String name = result.getString(COLUMN_MENUS_NAME);
                int categoryId = result.getInt(COLUMN_MENUS_CATEGORY_ID);
                double price = result.getDouble(COLUMN_MENUS_PRICE);
                String imageUrl = result.getString(COLUMN_MENUS_IMAGE_URL);

                list.add(new Menu(id, name, categoryId, price, imageUrl));
            }

            result.close();
            databaseHelper.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Menu List Query failed!", e);
            return null;
        }
    }


    public void insertNewMenu(String name, int category_id, double price, String imageUrl) {
        String sql = "INSERT INTO " + TABLE_MENUS + " ( " + COLUMN_MENUS_NAME + ", " + COLUMN_MENUS_CATEGORY_ID + ", " + COLUMN_MENUS_PRICE + ", " + COLUMN_MENUS_IMAGE_URL +
                ") VALUES (\"" + name + "\", " + category_id + ", " + price + ", \"" + imageUrl +"\");";
        try {
            databaseHelper.open();
            databaseHelper.getStatement().execute(sql);
            databaseHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteMenuById(int id) {
        ResultSet result;
        String sql = "DELETE FROM " + TABLE_MENUS + " WHERE " + COLUMN_MENUS_ID + "=? " + ";";
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

    public void deleteMenuByCategoryId(int id) {
        ResultSet result;
        String sql = "DELETE FROM " + TABLE_MENUS + " WHERE " + COLUMN_MENUS_CATEGORY_ID + "=? " + ";";
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

    public void updateMenu(Menu menu) {
        String sql = "START TRANSACTION;";
        databaseHelper.doQuery(sql);
        sql = "UPDATE " + TABLE_MENUS + " SET " + COLUMN_MENUS_CATEGORY_ID + " =?, " + COLUMN_MENUS_NAME +
                " =?, " + COLUMN_MENUS_PRICE + " =?, " + COLUMN_MENUS_IMAGE_URL + "=? " +
                " WHERE " + COLUMN_CATEGORIES_ID + " =? ;";
        try {
            databaseHelper.open();
            PreparedStatement preparedStatement = databaseHelper.getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, menu.getCategory_id());
            preparedStatement.setString(2, menu.getName());
            preparedStatement.setDouble(3, menu.getPrice());
            preparedStatement.setString(4, menu.getImage_url());
            preparedStatement.setInt(5, menu.getId());

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
