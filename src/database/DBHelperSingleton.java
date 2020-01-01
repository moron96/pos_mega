package database;

import utils.SettingsUtils;

import java.io.IOException;

/**
 * Created by ASUS on 25/04/2017.
 */
public class DBHelperSingleton {

    private static DBHelper dbHelper;
    private static SettingsUtils settingsUtils;

    public static DBHelper getInstance() {
        if(dbHelper==null)
        {
            try {
                settingsUtils = SettingsUtils.loadSettingsFile();
                dbHelper = new DBHelper(settingsUtils.getDb_location(), settingsUtils.getDb_port(), settingsUtils.getDb_user(), settingsUtils.getDb_password(), settingsUtils.getDb_name());
                dbHelper.startPool();

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Load Settings File Failed!");
                //TODO:error dialog buat masukkin database info dan tulis ke settings
                //settingsUtils = new utils.SettingsUtils()
                //dbHelper = new DBHelper(settingsUtils.getDb_location(), settingsUtils.getDb_port(), settingsUtils.getDb_user(), settingsUtils.getDb_password(), settingsUtils.getDb_name());

            }
        }

        return dbHelper;
    }

    private DBHelperSingleton() {

    }
}
