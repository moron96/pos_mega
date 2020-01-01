package scene.register_admin;

import database.DBHelper;
import database.DBHelperSingleton;
import database.utils.UserDatabaseUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by ASUS on 21/04/2017.
 */
public class register_admin_controller implements Initializable{

    @FXML
    private Label labelRegisterAdmin;
    @FXML
    private Label labelUsername;
    @FXML
    private Label labelPassword;
    @FXML
    private Label labelConfirmPassword;
    @FXML
    private Label labelSecurityCode;
    @FXML
    private Label labelWarning;
    @FXML
    private TextField textFieldUsername;
    @FXML
    private TextField textFieldSecurityCode;
    @FXML
    private PasswordField passwordFieldPassword;
    @FXML
    private PasswordField passwordFieldConfirmPassword;
    @FXML
    private Button buttonOK;

    private UserDatabaseUtils userDatabaseUtils;

    private DBHelper dbHelper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void buttonOKAction(ActionEvent event) {

        String username = textFieldUsername.getText();
        String password = passwordFieldPassword.getText();
        String confirmPassword = passwordFieldConfirmPassword.getText();
        String securityCode = textFieldSecurityCode.getText();

        if(username.equals("")) {

            labelWarning.setText("Username should be filled");

        }
        else if(password.equals("")) {

            labelWarning.setText("Password should be filled");

        }
        else if(confirmPassword.equals("")) {

            labelWarning.setText("Confirm Password should be filled");

        }
        else if(!password.equals(confirmPassword)) {

            labelWarning.setText("Confirm Password doesn't match with Password");

        }
        else if(securityCode.equals("")) {

            labelWarning.setText("Security Code should be filled");

        }
        else {

            dbHelper = DBHelperSingleton.getInstance();
            userDatabaseUtils = new UserDatabaseUtils(dbHelper);
            userDatabaseUtils.insertNewUser(username, password, securityCode);
            Stage stage = (Stage) buttonOK.getScene().getWindow();
            stage.close();

        }

    }
}
