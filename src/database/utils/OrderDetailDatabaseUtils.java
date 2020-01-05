package database.utils;

import database.DBHelper;
import model.Order_Detail;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static database.DB_KEYS.*;

/**
 * Created by Michael on 4/20/2017.
 */
public class OrderDetailDatabaseUtils {

    DBHelper databaseHelper;

    public OrderDetailDatabaseUtils(DBHelper helper) {
        super();
        databaseHelper = helper;
    }

    public OrderDetailDatabaseUtils(String serverName, int port, String user, String password, String database) {
        super();
        databaseHelper = new DBHelper(serverName, port, user, password, database);
    }


    public ArrayList<Order_Detail> getOrderDetailsByOrderId(int orderId) {
        String sql = "SELECT * FROM " + TABLE_ORDER_DETAILS + " WHERE " + COLUMN_ORDER_DETAILS_ORDER_ID + " = " + orderId;
        ResultSet result;
        System.out.println(sql);
        ArrayList<Order_Detail> list = new ArrayList<>();
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            while(result.next()) {
                int id = result.getInt(COLUMN_ORDER_DETAILS_ID);
                int OId = result.getInt(COLUMN_ORDER_DETAILS_ORDER_ID);
                int menuId = result.getInt(COLUMN_ORDER_DETAILS_MENU_ID);
                int qty = result.getInt(COLUMN_ORDER_DETAILS_QTY);
                double price = result.getDouble(COLUMN_ORDER_DETAILS_PRICE);
                String notes = result.getString(COLUMN_ORDER_DETAILS_NOTES);
                int foodStatusId = result.getInt(COLUMN_ORDER_DETAILS_FOOD_STATUS_ID);

                list.add(new Order_Detail(id, OId, menuId, qty, price, notes, foodStatusId));
            }

            result.close();
            databaseHelper.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Order Detail List Query failed!", e);
            return null;
        }
    }

    public Order_Detail getOrderDetailByMenuIdAndOrderId(int MId, int OId) {
        String sql = "SELECT * FROM " + TABLE_ORDER_DETAILS + " WHERE " + COLUMN_ORDER_DETAILS_MENU_ID + " = " + MId + " AND " + COLUMN_ORDER_DETAILS_ORDER_ID + " = " + OId + ";";

        ResultSet result;
        System.out.println(sql);
        Order_Detail order_detail = null;
        try {
            databaseHelper.open();
            result = databaseHelper.getStatement().executeQuery(sql);

            if(result.next()) {
                int id = result.getInt(COLUMN_ORDER_DETAILS_ID);
                int orderId = result.getInt(COLUMN_ORDER_DETAILS_ORDER_ID);
                int menuId = result.getInt(COLUMN_ORDER_DETAILS_MENU_ID);
                int qty = result.getInt(COLUMN_ORDER_DETAILS_QTY);
                double price = result.getDouble(COLUMN_ORDER_DETAILS_PRICE);
                String notes = result.getString(COLUMN_ORDER_DETAILS_NOTES);
                int foodStatusId = result.getInt(COLUMN_ORDER_DETAILS_FOOD_STATUS_ID);

                order_detail = new Order_Detail(id, orderId, menuId, qty, price, notes, foodStatusId);
            }

            result.close();
            databaseHelper.close();
            return order_detail;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Order Detail List Query failed!", e);
            return null;
        }
    }

    public ArrayList<Order_Detail> getOrderDetailByMenuId(int MId) {
        String sql = "SELECT * FROM " + TABLE_ORDER_DETAILS + " WHERE " + COLUMN_ORDER_DETAILS_MENU_ID + " = " + MId + ";";

        ResultSet result;
        System.out.println(sql);
        ArrayList<Order_Detail> list = new ArrayList<>();
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            while(result.next()) {
                int id = result.getInt(COLUMN_ORDER_DETAILS_ID);
                int OId = result.getInt(COLUMN_ORDER_DETAILS_ORDER_ID);
                int menuId = result.getInt(COLUMN_ORDER_DETAILS_MENU_ID);
                int qty = result.getInt(COLUMN_ORDER_DETAILS_QTY);
                double price = result.getDouble(COLUMN_ORDER_DETAILS_PRICE);
                String notes = result.getString(COLUMN_ORDER_DETAILS_NOTES);
                int foodStatusId = result.getInt(COLUMN_ORDER_DETAILS_FOOD_STATUS_ID);

                list.add(new Order_Detail(id, OId, menuId, qty, price, notes, foodStatusId));
            }

            result.close();
            databaseHelper.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Order Detail List Query failed!", e);
            return null;
        }
    }

    public void insertNewOrderDetails(int orderId, int menuId, int qty, double price, String notes, int foodStatusId) {
        String sql = "INSERT INTO " + TABLE_ORDER_DETAILS + " ( " + COLUMN_ORDER_DETAILS_ORDER_ID + ", " + COLUMN_ORDER_DETAILS_MENU_ID + ", " + COLUMN_ORDER_DETAILS_QTY + ", " + COLUMN_ORDER_DETAILS_PRICE + ", " + COLUMN_ORDER_DETAILS_NOTES + ", " + COLUMN_ORDER_DETAILS_FOOD_STATUS_ID +
                ") VALUES (\"" + orderId + "\", \"" + menuId + "\", " + qty + ", " + price + ",\"" + notes + "\"," + foodStatusId +");";
        try {
            databaseHelper.open();
            databaseHelper.getStatement().execute(sql);
            databaseHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateOrderDetail(Order_Detail orderDetail) {
        String sql = "START TRANSACTION;";
        try {
            databaseHelper.open();
            System.out.println(sql);
            databaseHelper.doQuery(sql);
            sql = "UPDATE " + TABLE_ORDER_DETAILS + " SET " + COLUMN_ORDER_DETAILS_ORDER_ID + " =?, " + COLUMN_ORDER_DETAILS_MENU_ID + "=?, " + COLUMN_ORDER_DETAILS_QTY + "=?, " +
                    COLUMN_ORDER_DETAILS_PRICE + "=?, " + COLUMN_ORDER_DETAILS_NOTES + "=?, " + COLUMN_ORDER_DETAILS_FOOD_STATUS_ID + "=? " +
                    " WHERE " + COLUMN_ORDER_DETAILS_ID + " =? ;";


            PreparedStatement preparedStatement = databaseHelper.getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, orderDetail.getOrder_id());
            preparedStatement.setInt(2, orderDetail.getMenu_id());
            preparedStatement.setInt(3, orderDetail.getQty());
            preparedStatement.setDouble(4, orderDetail.getPrice());
            preparedStatement.setString(5, orderDetail.getNotes());
            preparedStatement.setInt(6, orderDetail.getFood_status_id());
            preparedStatement.setInt(7, orderDetail.getId());

            preparedStatement.execute();

            sql = "COMMIT";
            databaseHelper.doQuery(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            sql = "ROLLBACK ";
            databaseHelper.doQuery(sql);
        } finally {
            databaseHelper.close();
        }
    }

    public void deleteOrderDetailById(int id) {
        ResultSet result;
        String sql = "DELETE FROM " + TABLE_ORDER_DETAILS + " WHERE " + COLUMN_ORDER_DETAILS_ID + "=? " + ";";
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

    public void deleteOrderDetailByMenuIdAndOrderId(int menuId, int orderId) {
        ResultSet result;
        String sql = "DELETE FROM " + TABLE_ORDER_DETAILS + " WHERE " + COLUMN_ORDER_DETAILS_MENU_ID + "=? " + " AND " + COLUMN_ORDER_DETAILS_ORDER_ID + "=? " + ";";
        try {
            databaseHelper.open();

            PreparedStatement preparedStatement = databaseHelper.getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, menuId);
            preparedStatement.setInt(2, orderId);
            preparedStatement.executeUpdate();

            databaseHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void deleteOrderDetailByMenuId(int menuId) {
        ResultSet result;
        String sql = "DELETE FROM " + TABLE_ORDER_DETAILS + " WHERE " + COLUMN_ORDER_DETAILS_MENU_ID + "=? " + ";";
        try {
            databaseHelper.open();

            PreparedStatement preparedStatement = databaseHelper.getConnection().prepareStatement(sql);
            preparedStatement.setInt(1, menuId);
            preparedStatement.executeUpdate();

            databaseHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void truncateOrderDetail() {
        ResultSet result;
        String sql = "truncate table " + TABLE_ORDER_DETAILS + ";";
        try {
            databaseHelper.open();
            PreparedStatement preparedStatement = databaseHelper.getConnection().prepareStatement(sql);
            preparedStatement.executeUpdate();
            databaseHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<Map<String, String>> monthlyOrderDetailRecapMenu(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.UK);
        ResultSet result;
        String sql = "SELECT m.*, sum(od."+ COLUMN_ORDER_DETAILS_QTY +") as "+ COLUMN_ORDER_DETAILS_QTY +", o."+ COLUMN_ORDERS_DATE +", o."+ COLUMN_ORDERS_CUSTOMER_CODE +
                " from "+ TABLE_ORDER_DETAILS +" od" +
                " left join "+ TABLE_ORDERS +" o on od."+ COLUMN_ORDER_DETAILS_ORDER_ID +" = o."+ COLUMN_ORDERS_ID +
                " left join (" +
                    " select m."+ COLUMN_MENUS_ID +" as menu_id, m."+ COLUMN_MENUS_NAME +" as menu_name, c."+ COLUMN_CATEGORIES_NAME +" as category_name from "+ TABLE_MENUS +" m" +
                    " left join "+ TABLE_CATEGORIES +" c on m."+ COLUMN_MENUS_CATEGORY_ID +" = c." + COLUMN_CATEGORIES_ID +
                " ) m on m.menu_id = od." + COLUMN_ORDER_DETAILS_MENU_ID +
                " where "+ COLUMN_ORDERS_DATE +" <= last_day('"+ sdf.format(date) +"') and "+ COLUMN_ORDERS_DATE +" >= date_add(date_add(LAST_DAY('"+ sdf.format(date) +"'),interval 1 DAY),interval -1 MONTH)" +
                " group by menu_id" +
                " order by "+ COLUMN_ORDER_DETAILS_QTY +" desc;";

        System.out.println(sql);
        ArrayList<Map<String, String>> maplist = new ArrayList<>();
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            while(result.next()) {
                Map<String, String> map = new HashMap<>();

                map.put("category_name",result.getString("category_name"));
                map.put("menu_name",result.getString("menu_name"));
                map.put("qty",result.getString(COLUMN_ORDER_DETAILS_QTY));

                maplist.add(map);
            }

            result.close();
            databaseHelper.close();
            return maplist;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Monthly Order Detail Recap Query failed!", e);
            return null;
        }
    }

    public ArrayList<Map<String, String>> monthlyOrderDetailRecapCategory(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        ResultSet result;
        String sql = "SELECT m.*,sum(od."+ COLUMN_ORDER_DETAILS_QTY +") as "+ COLUMN_ORDER_DETAILS_QTY +",o."+ COLUMN_ORDERS_DATE +",o."+ COLUMN_ORDERS_CUSTOMER_CODE +
                " from "+ TABLE_ORDER_DETAILS +" od" +
                " left join "+ TABLE_ORDERS +" o on od."+ COLUMN_ORDER_DETAILS_ORDER_ID +" = o."+ COLUMN_ORDERS_ID +
                " left join (" +
                " select m."+ COLUMN_MENUS_ID +" as menu_id, m."+ COLUMN_MENUS_NAME +" as menu_name, c."+ COLUMN_CATEGORIES_NAME +" as category_name from "+ TABLE_MENUS +" m" +
                " left join "+ TABLE_CATEGORIES +" c on m."+ COLUMN_MENUS_CATEGORY_ID +" = c." + COLUMN_CATEGORIES_ID +
                " ) m on m."+ COLUMN_MENUS_ID +" = od." + COLUMN_ORDER_DETAILS_MENU_ID +
                " where "+ COLUMN_ORDERS_DATE +" <= last_day("+ sdf.format(date) +") and "+ COLUMN_ORDERS_DATE +" >= date_add(date_add(LAST_DAY("+ sdf.format(date) +"),interval 1 DAY),interval -1 MONTH)" +
                " group by category_name" +
                "order by "+ COLUMN_ORDER_DETAILS_QTY +" desc;";

        System.out.println(sql);
        ArrayList<Map<String, String>> maplist = new ArrayList<>();
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            while(result.next()) {
                Map<String, String> map = new HashMap<String, String>();

                map.put("category_name",result.getString("category_name"));
                map.put("qty",result.getString(COLUMN_ORDER_DETAILS_QTY));

                maplist.add(map);
            }

            result.close();
            databaseHelper.close();
            return maplist;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Monthly Order Detail Recap Query failed!", e);
            return null;
        }
    }
}
