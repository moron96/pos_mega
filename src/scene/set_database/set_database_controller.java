package scene.set_database;

import database.DBHelper;
import database.DBHelperSingleton;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import utils.SettingsUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ASUS on 21/04/2017.
 */
public class set_database_controller implements Initializable{

    @FXML
    private Label labelWarning;
    @FXML
    private TextField textFieldLocation;
    @FXML
    private TextField textFieldPort;
    @FXML
    private TextField textFieldUser;
    @FXML
    private TextField textFieldPassword;
    @FXML
    private TextField textFieldDatabase;
    @FXML
    private Button buttonOK;
    @FXML
    Button exit_button;

    private DBHelper dbHelper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private boolean validateNumeric(String digit) {

        Matcher matcher = Pattern.compile("^[0-9]+").matcher(digit);
        return matcher.find();

    }


    @FXML
    void exitbtn(ActionEvent event) throws Exception{
        Stage primarystage = (Stage)labelWarning.getScene().getWindow();
        DBHelper dbHelper = DBHelperSingleton.getInstance();
        if(dbHelper!=null) {
            dbHelper.closePool();
        }
        primarystage.close();
        Platform.exit();
    }

    @FXML
    private void buttonOKAction(ActionEvent event) throws IOException {

        if(textFieldLocation.getText().equals("")) {

            labelWarning.setText("Location should be filled");

        }
        else if(textFieldPort.getText().equals("")) {

            labelWarning.setText("Port should be filled");

        }
        else if(validateNumeric(textFieldPort.getText()) == false) {

            labelWarning.setText("Port should be numeric");

        }
        else if(Integer.parseInt(textFieldPort.getText()) < 0 || Integer.parseInt(textFieldPort.getText()) > 65535) {

            labelWarning.setText("Port should be between 0 and 65535");

        }
        else if(textFieldUser.getText().equals("")) {

            labelWarning.setText("User should be filled");

        }
        else if(textFieldPassword.getText().equals("")) {

            labelWarning.setText("Password should be filled");

        }
        else if(textFieldDatabase.getText().equals("")) {

            labelWarning.setText("Database should be filled");

        }
        else {

            String location = textFieldLocation.getText();
            int port = Integer.parseInt(textFieldPort.getText());
            String user = textFieldUser.getText();
            String password = textFieldPassword.getText();
            String database = textFieldDatabase.getText();

            dbHelper = new DBHelper(location,port,user,password,database);
            dbHelper.startPool();

            try {
                dbHelper.test();
                dbHelper.initDatabaseTables();

                SettingsUtils settingsUtils = new SettingsUtils(location, port, user, password, database);
                settingsUtils.writeSettingsFile();

                Stage stage = (Stage) buttonOK.getScene().getWindow();
                dbHelper.closePool();
                stage.close();

            } catch (SQLException e) {
                e.printStackTrace();
                labelWarning.setText("Unable to connect to database");
            }

        }

    }

}
