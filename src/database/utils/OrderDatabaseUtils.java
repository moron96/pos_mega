package database.utils;

import database.DBHelper;
import model.Order;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import static database.DB_KEYS.*;

/**
 * Created by Michael on 4/20/2017.
 */
public class OrderDatabaseUtils {

    DBHelper databaseHelper;

    public OrderDatabaseUtils(DBHelper helper) {
        super();
        databaseHelper = helper;
    }

    public OrderDatabaseUtils(String serverName, int port, String user, String password, String database) {
        super();
        databaseHelper = new DBHelper(serverName, port, user, password, database);
    }

    public ArrayList<Order> getOrderList() {
        String sql = "SELECT * FROM " + TABLE_ORDERS + " WHERE " + COLUMN_ORDERS_DELETED_AT + " IS NULL" + ";";

        ResultSet result;
        System.out.println(sql);
        ArrayList<Order> list = new ArrayList<>();
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            while(result.next()) {
                int id = result.getInt(COLUMN_ORDERS_ID);
                String customerCode = result.getString(COLUMN_ORDERS_CUSTOMER_CODE);
                Date date = result.getDate(COLUMN_ORDERS_DATE);
                int statusId = result.getInt(COLUMN_ORDERS_STATUS_ID);
                int paymentMethodId = result.getInt(COLUMN_ORDERS_PAYMENT_METHOD_ID);
                double promoAmount = result.getDouble(COLUMN_ORDERS_PROMO_AMOUNT);
                Date deletedAt = result.getDate(COLUMN_ORDERS_DELETED_AT);
                String notes = result.getString(COLUMN_ORDERS_NOTES);

                list.add(new Order(id, customerCode, date, statusId, paymentMethodId, promoAmount, deletedAt, notes));
            }

            result.close();
            databaseHelper.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Order List Query failed!", e);
            return null;
        }
    }

    public ArrayList<Order> getOrderListToday() {
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String sql = "SELECT * FROM " + TABLE_ORDERS + " WHERE " + COLUMN_ORDERS_STATUS_ID + " = 1 AND "  + COLUMN_ORDERS_DATE + " BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND " + COLUMN_ORDERS_DELETED_AT + " IS NULL;";
        ResultSet result;
        System.out.println(sql);
        ArrayList<Order> list = new ArrayList<>();
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            while(result.next()) {
                int id = result.getInt(COLUMN_ORDERS_ID);
                String customerCode = result.getString(COLUMN_ORDERS_CUSTOMER_CODE);
                Date date = result.getTimestamp(COLUMN_ORDERS_DATE);
                System.out.println(sdf.format(date));
                int statusId = result.getInt(COLUMN_ORDERS_STATUS_ID);
                int paymentMethodId = result.getInt(COLUMN_ORDERS_PAYMENT_METHOD_ID);
                double promoAmount = result.getDouble(COLUMN_ORDERS_PROMO_AMOUNT);
                Date deletedAt = result.getDate(COLUMN_ORDERS_DELETED_AT);
                String notes = result.getString(COLUMN_ORDERS_NOTES);

                list.add(new Order(id, customerCode, date, statusId, paymentMethodId, promoAmount, deletedAt, notes));
            }

            result.close();
            databaseHelper.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Order List Today Query failed!", e);
            return null;
        }
    }

    public ArrayList<Order> getOrderListTodayHistory() {
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String sql = "SELECT * FROM " + TABLE_ORDERS + " WHERE " + COLUMN_ORDERS_STATUS_ID + " = 2 AND " + COLUMN_ORDERS_DATE + " BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 1 DAY) AND " + COLUMN_ORDERS_DELETED_AT + " IS NULL;";
        ResultSet result;
        System.out.println(sql);
        ArrayList<Order> list = new ArrayList<>();
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            while(result.next()) {
                int id = result.getInt(COLUMN_ORDERS_ID);
                String customerCode = result.getString(COLUMN_ORDERS_CUSTOMER_CODE);
                Date date = result.getTimestamp(COLUMN_ORDERS_DATE);
                System.out.println(sdf.format(date));
                int statusId = result.getInt(COLUMN_ORDERS_STATUS_ID);
                int paymentMethodId = result.getInt(COLUMN_ORDERS_PAYMENT_METHOD_ID);
                double promoAmount = result.getDouble(COLUMN_ORDERS_PROMO_AMOUNT);
                Date deletedAt = result.getDate(COLUMN_ORDERS_DELETED_AT);
                String notes = result.getString(COLUMN_ORDERS_NOTES);

                list.add(new Order(id, customerCode, date, statusId, paymentMethodId, promoAmount, deletedAt, notes));
            }

            result.close();
            databaseHelper.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Order List Today Query failed!", e);
            return null;
        }
    }

    public ArrayList<Order> getOrderListHistoryByDate(Date selectedDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        String sql = "SELECT * FROM " + TABLE_ORDERS + " WHERE " + COLUMN_ORDERS_STATUS_ID + " = 2 AND " + COLUMN_ORDERS_DATE + " BETWEEN '"+ sdf.format(selectedDate) +"' AND DATE_ADD('"+ sdf.format(selectedDate) +"', INTERVAL 1 DAY) AND " + COLUMN_ORDERS_DELETED_AT + " IS NULL;";
        ResultSet result;
        System.out.println(sql);
        ArrayList<Order> list = new ArrayList<>();
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            while(result.next()) {
                int id = result.getInt(COLUMN_ORDERS_ID);
                String customerCode = result.getString(COLUMN_ORDERS_CUSTOMER_CODE);
                Date date = result.getTimestamp(COLUMN_ORDERS_DATE);
                System.out.println(sdf.format(date));
                int statusId = result.getInt(COLUMN_ORDERS_STATUS_ID);
                int paymentMethodId = result.getInt(COLUMN_ORDERS_PAYMENT_METHOD_ID);
                double promoAmount = result.getDouble(COLUMN_ORDERS_PROMO_AMOUNT);
                Date deletedAt = result.getDate(COLUMN_ORDERS_DELETED_AT);
                String notes = result.getString(COLUMN_ORDERS_NOTES);

                list.add(new Order(id, customerCode, date, statusId, paymentMethodId, promoAmount, deletedAt, notes));
            }

            result.close();
            databaseHelper.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Order List By Date Query failed!", e);
            return null;
        }
    }

    public double getOrderTotalByID(int id) {
        String sql = "SELECT sum(qty*price) as total , sum(qty) as qty FROM " + TABLE_ORDER_DETAILS + " WHERE " + COLUMN_ORDER_DETAILS_ORDER_ID + " = " + String.valueOf(id);
        ResultSet result;
        System.out.println(sql);
        double total = 0;
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            while(result.next()) {
                total = result.getDouble("total");
            }

            result.close();
            databaseHelper.close();
            return total;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Order Total By ID Query failed!", e);
            return 0;
        }
    }

    public int getOrderQuantityByID(int id) {
        String sql = "SELECT sum(qty*price) as total , sum(qty) as qty FROM " + TABLE_ORDER_DETAILS + " WHERE " + COLUMN_ORDER_DETAILS_ORDER_ID + " = " + String.valueOf(id);
        ResultSet result;
        System.out.println(sql);
        int qty = 0;
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            while(result.next()) {
                qty = result.getInt("qty");
            }

            result.close();
            databaseHelper.close();
            return qty;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Order Quantity By ID Query failed!", e);
            return 0;
        }
    }

    public void insertNewOrder(String customerCode, Date date, int statusId, int paymentMethodId, double promoAmount, String notes) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
        String sql = "INSERT INTO " + TABLE_ORDERS + " ( " + COLUMN_ORDERS_CUSTOMER_CODE + ", " + COLUMN_ORDERS_DATE + ", " + COLUMN_ORDERS_STATUS_ID + ", " + COLUMN_ORDERS_PAYMENT_METHOD_ID + ", " + COLUMN_ORDERS_PROMO_AMOUNT + ", " + COLUMN_ORDERS_NOTES +
                ") VALUES (\"" + customerCode + "\", \"" + sdf.format(date) + "\", " + statusId + ", " + paymentMethodId + ", " + promoAmount + ",\"" + notes +"\");";
        try {
            databaseHelper.open();
            databaseHelper.getStatement().execute(sql);
            databaseHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void updateOrder(Order order) {
        String sql = "START TRANSACTION;";
        try {
        databaseHelper.open();
        databaseHelper.doQuery(sql);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        //String sql = "";
        sql = "UPDATE " + TABLE_ORDERS + " SET " + COLUMN_ORDERS_CUSTOMER_CODE + " =?, " + COLUMN_ORDERS_DATE + "=?, " + COLUMN_ORDERS_STATUS_ID + "=?, " +
                COLUMN_ORDERS_PAYMENT_METHOD_ID + "=?, " + COLUMN_ORDERS_PROMO_AMOUNT + "=?, " + COLUMN_ORDERS_DELETED_AT + "=?, " + COLUMN_ORDERS_NOTES + "=? " +
                " WHERE " + COLUMN_ORDERS_ID + " =? ;";



            PreparedStatement preparedStatement = databaseHelper.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, order.getCustomer_code());
            preparedStatement.setString(2, sdf.format(order.getDate()));
            preparedStatement.setInt(3, order.getStatus_id());
            preparedStatement.setInt(4, order.getPayment_method_id());
            preparedStatement.setDouble(5, order.getPromo_amount());
            if(order.getDeleted_at() != null){

                preparedStatement.setString(6, sdf.format(order.getDeleted_at()));

            }
            else {

                preparedStatement.setString(6, null);

            }
            preparedStatement.setString(7, order.getNotes());
            preparedStatement.setInt(8, order.getId().intValue());

            preparedStatement.execute();



        sql = "COMMIT";
        databaseHelper.doQuery(sql);
        databaseHelper.close();

        } catch (SQLException e) {
            e.printStackTrace();
            sql = "ROLLBACK ";
            databaseHelper.doQuery(sql);
        }
    }

    public void softDeleteOrderById(int id) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//dd/MM/yyyy
        ResultSet result;
        String sql = "UPDATE " + TABLE_ORDERS + " SET " + COLUMN_ORDERS_DELETED_AT + " =? " + " WHERE " + COLUMN_ORDERS_ID + "=? " + ";";
        try {
            databaseHelper.open();

            PreparedStatement preparedStatement = databaseHelper.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, sdf.format(new Date()));
            preparedStatement.setInt(2, id);

            preparedStatement.executeUpdate();

            databaseHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void forceDeleteOrderById(int id) {
        ResultSet result;
        String sql = "DELETE FROM " + TABLE_ORDERS + " WHERE " + COLUMN_ORDERS_ID + "=? " + ";";
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


    public void truncateorder() {
        ResultSet result;
        String sql = "truncate table " + TABLE_ORDERS + ";";
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
