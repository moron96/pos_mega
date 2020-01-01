package scene.main;

import database.DBHelper;
import database.DBHelperSingleton;
import database.utils.UserDatabaseUtils;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.User;
import print.pdf.pdf_generator;
import print.print.PrintDailyReport;
import utils.SettingsUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Elbert Utama on 20/04/2017.
 */
public class Main extends Application {

    private SettingsUtils settingsUtils;
    DBHelper dbHelper;


    private UserDatabaseUtils userDatabaseUtils;

    @Override
    public void start(Stage primaryStage) throws Exception {

        checkSettingsIfExists();

        checkAdminIfExists();

//        PrintDailyReport printDailyReport = new PrintDailyReport();
//        printDailyReport.doPrintDaily();

        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle("test");
        primaryStage.setScene(new Scene(root, 800, 600));

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());
        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.show();

    }

    private void checkSettingsIfExists() {

        try {
            settingsUtils = SettingsUtils.loadSettingsFile();
            dbHelper = DBHelperSingleton.getInstance();
            dbHelper.initDatabaseTables();
        } catch (IOException io) {
            io.printStackTrace();

            try {
                Stage dialogStage = new Stage();
                Pane root = FXMLLoader.load(getClass().getResource("/scene/set_database/set_database.fxml"));
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);
                dialogStage.initModality(Modality.APPLICATION_MODAL);
                dialogStage.initStyle(StageStyle.UNDECORATED);
                dialogStage.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //checkSettingsIfExists();
        }

    }

    private void checkAdminIfExists() {
        dbHelper = DBHelperSingleton.getInstance();
        userDatabaseUtils = new UserDatabaseUtils(dbHelper);
        ArrayList<User> listUser = userDatabaseUtils.getAllUser();

        if(listUser.size() == 0) {

            try {
                Stage dialogStage = new Stage();
                Pane root = FXMLLoader.load(getClass().getResource("/scene/register_admin/register_admin.fxml"));
                Scene scene = new Scene(root);
                dialogStage.setScene(scene);
                //dialogStage.initStyle(StageStyle.UNDECORATED);
                dialogStage.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
