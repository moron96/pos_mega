package utils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by Michael on 4/21/2017.
 */
public class SettingsUtils {

    public static final String SETTINGS_FOLDER_PATH = "POS/";
    public static final String SETTINGS_FILE_NAME = "settings.json";
    public static final String SETTINGS_FILE_PATH = SETTINGS_FOLDER_PATH + SETTINGS_FILE_NAME;

    public static final String JSON_DB_USER = "db_user";
    public static final String JSON_DB_PASSWORD = "db_password";
    public static final String JSON_DB_NAME = "db_name";
    public static final String JSON_DB_LOCATION = "db_location";
    public static final String JSON_DB_PORT = "db_port";

    private static JSONParser parser = new JSONParser();

    private String db_user;
    private String db_password;
    private String db_name;
    private String db_location;
    private int db_port;

    public String getDb_user() {
        return db_user;
    }

    public void setDb_user(String db_user) {
        this.db_user = db_user;
    }

    public String getDb_password() {
        return db_password;
    }

    public void setDb_password(String db_password) {
        this.db_password = db_password;
    }

    public String getDb_name() {
        return db_name;
    }

    public void setDb_name(String db_name) {
        this.db_name = db_name;
    }

    public String getDb_location() {
        return db_location;
    }

    public void setDb_location(String db_location) {
        this.db_location = db_location;
    }

    public int getDb_port() {
        return db_port;
    }

    public void setDb_port(int db_port) {
        this.db_port = db_port;
    }

    /**
     * Buat settings baru ke file settings.json
     * @param dbLocation
     * @param dbPort
     * @param dbUser
     * @param dbPassword
     * @param dbName
     */
    public SettingsUtils(String dbLocation, int dbPort, String dbUser, String dbPassword, String dbName) {

        db_location = dbLocation;
        db_port = dbPort;
        db_user = dbUser;
        db_password = dbPassword;
        db_name = dbName;

        File file = new File(SETTINGS_FILE_PATH);
        //make directory if not exists
        if(!file.getParentFile().exists()) {
            if(!file.getParentFile().exists()) {
                file.getParentFile().mkdir();
                try {
                    file.createNewFile();
                } catch (IOException f) {
                    f.printStackTrace();
                }
            }
        }
    }

    /**
     * Jadi kalau mau load settings dari file pake ini bikin classnya, kalau throw exception brarti setting file nya ga ada atau ga lengkap
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static SettingsUtils loadSettingsFile() throws IOException, JSONException {
        FileReader fileReader = new FileReader(SETTINGS_FILE_PATH);
        Object obj = null;
        try {
            obj = parser.parse(fileReader);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(obj != null) {
            JSONObject jsonObject;
            jsonObject = new JSONObject(obj.toString());
            String db_location = jsonObject.getString(JSON_DB_LOCATION);
            int db_port = jsonObject.getInt(JSON_DB_PORT);
            String db_user = jsonObject.getString(JSON_DB_USER);
            String db_password = jsonObject.getString(JSON_DB_PASSWORD);
            String db_name = jsonObject.getString(JSON_DB_NAME);
            return new SettingsUtils(db_location, db_port, db_user, db_password, db_name);
        } else {
            throw new IOException("JSON FILE IS NULL");
        }
    }

    public void writeSettingsFile() throws IOException {
        FileWriter fileWriter = new FileWriter(SETTINGS_FILE_PATH);

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_DB_NAME, db_name);
        jsonObject.put(JSON_DB_LOCATION, db_location);
        jsonObject.put(JSON_DB_PORT, db_port);
        jsonObject.put(JSON_DB_USER, db_user);
        jsonObject.put(JSON_DB_PASSWORD, db_password);
        fileWriter.write(jsonObject.toString());
        fileWriter.flush();

        System.out.println(SETTINGS_FILE_PATH + " written");
    }

    public void updateSettingsFile(String dbLocation, int dbPort, String dbUser, String dbPassword, String dbName) throws IOException {
        FileWriter fileWriter = new FileWriter(SETTINGS_FILE_PATH);

        db_location = dbLocation;
        db_port = dbPort;
        db_user = dbUser;
        db_password = dbPassword;
        db_name = dbName;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_DB_NAME, db_name);
        jsonObject.put(JSON_DB_LOCATION, db_location);
        jsonObject.put(JSON_DB_PORT, db_port);
        jsonObject.put(JSON_DB_USER, db_user);
        jsonObject.put(JSON_DB_PASSWORD, db_password);
        fileWriter.write(jsonObject.toString());
        fileWriter.flush();

        System.out.println(SETTINGS_FILE_PATH + " written");
    }


    public void updateSettingsFile(SettingsUtils settingsUtils) throws IOException {
        FileWriter fileWriter = new FileWriter(SETTINGS_FILE_PATH);

        db_location = settingsUtils.getDb_location();
        db_port = settingsUtils.getDb_port();
        db_user = settingsUtils.getDb_user();
        db_password = settingsUtils.getDb_password();
        db_name = settingsUtils.getDb_name();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put(JSON_DB_NAME, db_name);
        jsonObject.put(JSON_DB_LOCATION, db_location);
        jsonObject.put(JSON_DB_PORT, db_port);
        jsonObject.put(JSON_DB_USER, db_user);
        jsonObject.put(JSON_DB_PASSWORD, db_password);
        fileWriter.write(jsonObject.toString());
        fileWriter.flush();

        System.out.println(SETTINGS_FILE_PATH + " written");
    }

}
