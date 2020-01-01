package database;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

import static database.DB_KEYS.*;


/**
 * Created by Michael on 4/18/2017.
 */
public class DBHelper {


    //private MysqlDataSource dataSource = new MysqlDataSource();
    private Connection connection;
    private Statement statement;
    private ResultSet resultSet;
    private static BasicDataSource dbcp = new BasicDataSource();

    private static String driverName = "com.mysql.jdbc.Driver";

    private static String location = "";
    private static int port = -1;
    private static String user = "";
    private static String password = "";
    private static String database = "";


    public DBHelper(String location, int port, String user, String password, String database) {

        this.location = location;
        this.port = port;
        this.user = user;
        this.password = password;
        this.database = database;

    }

    public void test() throws SQLException {
        connection = dbcp.getConnection();
        statement = (Statement) connection.createStatement();
    }

    public void open() throws SQLException {
        connection = dbcp.getConnection();
        statement = (Statement) connection.createStatement();
    }

    public void close() {
        try {
            connection.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.WARNING, "Cannot Close Connection. Probably already closed", e);
            System.out.println("Cannot Close Connection. Probably already closed");
        }
    }

    public void closePool() {
        try {
            dbcp.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void startPool() {
        dbcp = new BasicDataSource();

        dbcp.setDriverClassName(driverName);
        dbcp.setUsername(user);
        dbcp.setPassword(password);
        dbcp.setUrl("jdbc:mysql://" + location  + ":" + port + "/" + database);
    }
    public ResultSet doQuery(String query) {
        try {
            return statement.executeQuery(query);
        } catch (SQLException e) {
            e.printStackTrace();
            Logger.getLogger("Log SQL").log(Level.SEVERE, "Query Failed", e);
            return null;
        }
    }

    public Connection getConnection() {
        return this.connection;
    }

    public Statement getStatement() {
        return this.statement;
    }

    public void initDatabaseTables() {
        System.out.println("INIT TABLES");
        try {
            open();
            getStatement().execute(
                    "CREATE TABLE IF NOT EXISTS " + TABLE_MENUS + "(" +
                            COLUMN_MENUS_ID + " int NOT NULL AUTO_INCREMENT, " +
                            COLUMN_MENUS_NAME + " varchar(255) DEFAULT NULL, " +
                            COLUMN_MENUS_CATEGORY_ID + " int DEFAULT 0, " +
                            COLUMN_MENUS_PRICE + " double DEFAULT 0, " +
                            COLUMN_MENUS_IMAGE_URL + " TEXT DEFAULT NULL, " +
                            "PRIMARY KEY (" + COLUMN_MENUS_ID + ")" +
                            ");"
            );
            System.out.println("TABLE MENUS EXECUTED");

            getStatement().execute(
                    "CREATE TABLE IF NOT EXISTS " + TABLE_ORDERS + "(" +
                            COLUMN_ORDERS_ID + " int NOT NULL AUTO_INCREMENT, " +
                            COLUMN_ORDERS_CUSTOMER_CODE + " varchar(255) DEFAULT NULL, " +
                            COLUMN_ORDERS_DATE + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                            COLUMN_ORDERS_STATUS_ID + " int DEFAULT 0, " +
                            COLUMN_ORDERS_PAYMENT_METHOD_ID + " int DEFAULT 0, " +
                            COLUMN_ORDERS_PROMO_AMOUNT + " double DEFAULT 0, " +
                            COLUMN_ORDERS_NOTES + " TEXT, " +
                            COLUMN_ORDERS_DELETED_AT + " DATETIME DEFAULT NULL, " +
                            "PRIMARY KEY (" + COLUMN_ORDERS_ID + ")" +
                            ");"
            );
            System.out.println("TABLE ORDERS EXECUTED");

            getStatement().execute(
                    "CREATE TABLE IF NOT EXISTS " + TABLE_ORDER_DETAILS + "(" +
                            COLUMN_ORDER_DETAILS_ID + " int NOT NULL AUTO_INCREMENT, " +
                            COLUMN_ORDER_DETAILS_ORDER_ID + " int DEFAULT -1, " +
                            COLUMN_ORDER_DETAILS_MENU_ID + " int DEFAULT -1, " +
                            COLUMN_ORDER_DETAILS_QTY + " int DEFAULT 0, " +
                            COLUMN_ORDER_DETAILS_PRICE + " double DEFAULT 0, " +
                            COLUMN_ORDER_DETAILS_NOTES + " TEXT DEFAULT NULL, " +
                            COLUMN_ORDER_DETAILS_FOOD_STATUS_ID + " int DEFAULT 0, " +
                            "PRIMARY KEY (" + COLUMN_ORDER_DETAILS_ID + "));"
            );
            System.out.println("TABLE ORDER DETAILS EXECUTED");

            getStatement().execute(
                    "CREATE TABLE IF NOT EXISTS " + TABLE_USERS + "(" +
                            COLUMN_USERS_ID + " int NOT NULL AUTO_INCREMENT, " +
                            COLUMN_USERS_NAME + " varchar(255) DEFAULT NULL, " +
                            COLUMN_USERS_PASSWORD + " TEXT DEFAULT NULL, " +
                            COLUMN_USERS_SECRET_CODE + " TEXT, " +
                            "PRIMARY KEY (" + COLUMN_USERS_ID + ")" +
                            ");"
            );
            System.out.println("TABLE USERS EXECUTED");

            getStatement().execute(
                    "CREATE TABLE IF NOT EXISTS " + TABLE_STATUSES + "(" +
                            COLUMN_STATUSES_ID + " int NOT NULL AUTO_INCREMENT, " +
                            COLUMN_STATUSES_SLUG + " VARCHAR(255) DEFAULT NULL, " +
                            COLUMN_STATUSES_NAME + " VARCHAR(255) DEFAULT NULL, " +
                            "PRIMARY KEY (" + COLUMN_STATUSES_ID + ")" +
                            ");"
            );
            System.out.println("TABLE STATUSES EXECUTED");

            getStatement().execute(
                    "CREATE TABLE IF NOT EXISTS " + TABLE_PAYMENT_METHODS + "(" +
                            COLUMN_PAYMENT_METHODS_ID + " int NOT NULL AUTO_INCREMENT, " +
                            COLUMN_PAYMENT_METHODS_SLUG + " VARCHAR(255) DEFAULT NULL, " +
                            COLUMN_PAYMENT_METHODS_NAME + " VARCHAR(255) DEFAULT NULL, " +
                            "PRIMARY KEY (" + COLUMN_PAYMENT_METHODS_ID + ")" +
                            ");"
            );
            System.out.println("TABLE PAYMENT METHODS EXECUTED");

            getStatement().execute(
                    "CREATE TABLE IF NOT EXISTS " + TABLE_FOOD_STATUSES + "(" +
                            COLUMN_FOOD_STATUSES_ID + " int NOT NULL AUTO_INCREMENT, " +
                            COLUMN_FOOD_STATUSES_SLUG + " VARCHAR(255) DEFAULT NULL, " +
                            COLUMN_FOOD_STATUSES_NAME + " VARCHAR(255) DEFAULT NULL, " +
                            "PRIMARY KEY (" + COLUMN_FOOD_STATUSES_ID + ")" +
                            ");"
            );
            System.out.println("TABLE FOOD STATUSES EXECUTED");

            getStatement().execute(
                    "CREATE TABLE IF NOT EXISTS " + TABLE_CATEGORIES + "(" +
                            COLUMN_CATEGORIES_ID + " int NOT NULL AUTO_INCREMENT, " +
                            COLUMN_CATEGORIES_SLUG + " VARCHAR(255) DEFAULT NULL, " +
                            COLUMN_CATEGORIES_NAME + " VARCHAR(255) DEFAULT NULL, " +
                            "PRIMARY KEY (" + COLUMN_CATEGORIES_ID + ")" +
                            ");"
            );
            System.out.println("TABLE CATEGORIES EXECUTED");

            close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
