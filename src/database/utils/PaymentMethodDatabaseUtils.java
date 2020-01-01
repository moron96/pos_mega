package database.utils;

import database.DBHelper;
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
public class PaymentMethodDatabaseUtils {

    DBHelper databaseHelper;

    public PaymentMethodDatabaseUtils(DBHelper helper) {
        super();
        databaseHelper = helper;
    }

    public PaymentMethodDatabaseUtils(String serverName, int port, String user, String password, String database) {
        super();
        databaseHelper = new DBHelper(serverName, port, user, password, database);
    }

    public ArrayList<Payment_Method> getAllPaymentMethods() {
        String sql = "SELECT * FROM " + TABLE_PAYMENT_METHODS + ";";

        ResultSet result;
        System.out.println(sql);
        ArrayList<Payment_Method> list = new ArrayList<>();
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            while(result.next()) {
                int id = result.getInt(COLUMN_PAYMENT_METHODS_ID);
                String name = result.getString(COLUMN_PAYMENT_METHODS_NAME);
                String slug = result.getString(COLUMN_PAYMENT_METHODS_SLUG);

                list.add(new Payment_Method(id, slug, name));
            }

            result.close();
            databaseHelper.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Payment Methods List Query failed!", e);
            return null;
        }
    }

    public Payment_Method getPaymentMethodById(int id) {
        String sql = "SELECT * FROM " + TABLE_PAYMENT_METHODS + " WHERE " + COLUMN_PAYMENT_METHODS_ID + " = " + id + ";";

        ResultSet result;
        System.out.println(sql);
        Payment_Method paymentMethod = null;
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            if(result.next()) {
                int paymentMethodId = result.getInt(COLUMN_PAYMENT_METHODS_ID);
                String name = result.getString(COLUMN_PAYMENT_METHODS_NAME);
                String slug = result.getString(COLUMN_PAYMENT_METHODS_NAME);

                paymentMethod = new Payment_Method(paymentMethodId, slug, name);
            }

            result.close();
            databaseHelper.close();
            return paymentMethod;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Payment Method Query failed!", e);
            return null;
        }
    }

    public Payment_Method getPaymentMethodByName(String name) {
        String sql = "SELECT * FROM " + TABLE_PAYMENT_METHODS + " WHERE " + COLUMN_PAYMENT_METHODS_NAME + " = '" + name + "';";

        ResultSet result;
        System.out.println(sql);
        Payment_Method paymentMethod = null;
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            if(result.next()) {
                int paymentMethodId = result.getInt(COLUMN_PAYMENT_METHODS_ID);
                String nama = result.getString(COLUMN_PAYMENT_METHODS_NAME);
                String slug = result.getString(COLUMN_PAYMENT_METHODS_NAME);

                paymentMethod = new Payment_Method(paymentMethodId, slug, nama);
            }

            result.close();
            databaseHelper.close();
            return paymentMethod;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Payment Method Query failed!", e);
            return null;
        }
    }

    public void deletePaymentMethodById(int id) {
        ResultSet result;
        String sql = "DELETE FROM " + TABLE_PAYMENT_METHODS + " WHERE " + COLUMN_PAYMENT_METHODS_ID + "=? " + ";";
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

    public void insertNewPaymentMethod(String name, String slug) {
        String sql = "INSERT INTO " + TABLE_PAYMENT_METHODS + " ( " + COLUMN_PAYMENT_METHODS_SLUG + ", " + COLUMN_PAYMENT_METHODS_NAME +
                ") VALUES (\"" + slug + "\", \"" + name + "\");";
        try {
            databaseHelper.open();
            databaseHelper.getStatement().execute(sql);
            databaseHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public void updatePaymentMethod(Payment_Method paymentMethod) {
        String sql = "START TRANSACTION;";
        databaseHelper.doQuery(sql);
        sql = "UPDATE " + TABLE_PAYMENT_METHODS + " SET " + COLUMN_PAYMENT_METHODS_SLUG + " =?, " + COLUMN_PAYMENT_METHODS_NAME + "=?, " +
                " WHERE " + COLUMN_PAYMENT_METHODS_ID + " =? ;";
        try {
            databaseHelper.open();
            PreparedStatement preparedStatement = databaseHelper.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, paymentMethod.getSlug());
            preparedStatement.setString(2, paymentMethod.getName());
            preparedStatement.setInt(3, paymentMethod.getId());

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
