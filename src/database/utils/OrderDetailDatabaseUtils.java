package database.utils;

import database.DBHelper;
import model.Order_Detail;

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

}
