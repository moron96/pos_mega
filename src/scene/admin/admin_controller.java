package scene.admin;

import database.DBHelper;
import database.DBHelperSingleton;
import database.utils.UserDatabaseUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import model.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by ASUS on 21/04/2017.
 */
public class admin_controller implements Initializable{

    @FXML
    private Label labelWarning;

    @FXML
    private TextField textFieldUsername;

    @FXML
    private PasswordField passwordFieldPassword;

    @FXML
    private Button buttonBack;

    @FXML
    private Button buttonOK;

    private UserDatabaseUtils userDatabaseUtils;

    private DBHelper dbHelper;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void buttonBackAction(ActionEvent event) {

        Stage stage = (Stage) buttonBack.getScene().getWindow();
        stage.close();

    }

    @FXML
    private void buttonOKAction(ActionEvent event) throws IOException {

        String username = textFieldUsername.getText();
        String password = passwordFieldPassword.getText();

        if(username.equals("")) {

            labelWarning.setText("Username should be filled");

        }
        else if(password.equals("")) {

            labelWarning.setText("Password should be filled");

        }
        else {

            dbHelper = DBHelperSingleton.getInstance();
            userDatabaseUtils = new UserDatabaseUtils(dbHelper);
            User user = userDatabaseUtils.getUserByNameAndPassword(username,password);

            if(user == null) {

                labelWarning.setText("Incorrect username or password");

            }
            else {

                Stage stage = (Stage) buttonOK.getScene().getWindow();
                Pane root = FXMLLoader.load(getClass().getResource("/scene/setting/setting.fxml"));
                Scene scene = new Scene(root);
                stage.setScene(scene);

            }
        }
    }
}
