package scene.main;

import database.DBHelper;
import database.DBHelperSingleton;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Elbert Utama on 18/04/2017.
 */
public class main_controller implements Initializable{

    @FXML
    TabPane main_tabpane;

    @FXML
    BorderPane main_borderpane;

    @FXML
    Tab tab_history;

    @FXML
    Tab tab_order;

    @FXML
    HBox main_hbox;

    @FXML
    Button admin_button;

    @FXML
    Button fullscreen_button;

    @FXML
    Button exit_button;


    @FXML
    void adminbtn(ActionEvent event) throws Exception{

        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/admin/admin.fxml"));
            Parent admin = (Parent) fxmlLoader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(admin));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void fullscreenbtn(ActionEvent event) throws Exception{
        Stage primarystage = (Stage)main_borderpane.getScene().getWindow();
        primarystage.setFullScreen(true);
    }

    @FXML
    void exitbtn(ActionEvent event) throws Exception{
        Stage primarystage = (Stage)main_borderpane.getScene().getWindow();
        DBHelper dbHelper = DBHelperSingleton.getInstance();
        dbHelper.closePool();
        primarystage.close();
        Platform.exit();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
