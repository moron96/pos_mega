package database.utils;

import database.DBHelper;
import model.Status;
import model.User;

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
public class StatusDatabaseUtils {

    DBHelper databaseHelper;

    public StatusDatabaseUtils(DBHelper helper) {
        super();
        databaseHelper = helper;
    }

    public StatusDatabaseUtils(String serverName, int port, String user, String password, String database) {
        super();
        databaseHelper = new DBHelper(serverName, port, user, password, database);
    }

    public ArrayList<Status> getALlStatuses() {
        String sql = "SELECT * FROM " + TABLE_STATUSES + ";";

        ResultSet result;
        System.out.println(sql);
        ArrayList<Status> list = new ArrayList<>();
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            while(result.next()) {
                int id = result.getInt(COLUMN_STATUSES_ID);
                String name = result.getString(COLUMN_STATUSES_NAME);
                String slug = result.getString(COLUMN_STATUSES_SLUG);

                list.add(new Status(id, slug, name));
            }

            result.close();
            databaseHelper.close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Status List Query failed!", e);
            return null;
        }
    }

    public Status getStatusById(int id) {
        String sql = "SELECT * FROM " + TABLE_STATUSES + " WHERE " + COLUMN_STATUSES_ID + " = " + id + ";";

        ResultSet result;
        System.out.println(sql);
        Status status = null;
        try {
            databaseHelper.open();
            result = databaseHelper.doQuery(sql);

            //insert data to ArrayList
            if(result.next()) {
                int statusId = result.getInt(COLUMN_STATUSES_ID);
                String name = result.getString(COLUMN_STATUSES_NAME);
                String slug = result.getString(COLUMN_STATUSES_SLUG);

                status = new Status(statusId, slug, name);
            }

            result.close();
            databaseHelper.close();
            return status;
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Get Status List Query failed!", e);
            return null;
        }
    }

    public void deleteStatusById(int id) {
        ResultSet result;
        String sql = "DELETE FROM " + TABLE_STATUSES + " WHERE " + COLUMN_STATUSES_ID + "=? " + ";";
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

    public void insertNewStatus(String name, String slug) {
        String sql = "INSERT INTO " + TABLE_STATUSES + " ( " + COLUMN_STATUSES_SLUG + ", " + COLUMN_STATUSES_NAME +
                ") VALUES (\"" + slug + "\", \"" + name + "\");";
        try {
            databaseHelper.open();
            databaseHelper.getStatement().execute(sql);
            databaseHelper.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateStatus(Status status) {
        String sql = "START TRANSACTION;";
        databaseHelper.doQuery(sql);
        sql = "UPDATE " + TABLE_STATUSES + " SET " + COLUMN_STATUSES_SLUG + " =?, " + COLUMN_STATUSES_NAME + "=?, " +
                " WHERE " + COLUMN_STATUSES_ID + " =? ;";
        try {
            databaseHelper.open();
            PreparedStatement preparedStatement = databaseHelper.getConnection().prepareStatement(sql);
            preparedStatement.setString(1, status.getSlug());
            preparedStatement.setString(2, status.getName());
            preparedStatement.setInt(3, status.getId());

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
