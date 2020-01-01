package scene.add_order;

import database.DBHelper;
import database.DBHelperSingleton;
import database.utils.OrderDatabaseUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by ASUS on 27/04/2017.
 */
public class add_order_controller implements Initializable{

    @FXML
    private Label labelWarning;
    @FXML
    private TextField textFieldCustomerCode;
    @FXML
    private TextArea textAreaNotes;
    @FXML
    private Button buttonOK;

    private DBHelper dbHelper;

    private OrderDatabaseUtils orderDatabaseUtils;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @FXML
    private void buttonOKAction(ActionEvent event) {
        doLogin(event);
    }

    @FXML
    private void handleKeyPressed(KeyEvent ke){
        if (ke.getCode().equals(KeyCode.ENTER))
        {
            doLogin(ke);
        }
    }

    public void doLogin(Event event){

        if(textFieldCustomerCode.getText().equals("")) {

            labelWarning.setText("Customer Code should be filled");

        }
        else {

            String customerCode = textFieldCustomerCode.getText();
            Date date = new Date();
            int statusId = 1;
            int paymentMethodId = -1;
            double promoAmount = 0;
            String notes = textAreaNotes.getText();

            dbHelper = DBHelperSingleton.getInstance();
            orderDatabaseUtils = new OrderDatabaseUtils(dbHelper);

            orderDatabaseUtils.insertNewOrder(customerCode, date, statusId, paymentMethodId, promoAmount, notes);

            Stage stage = (Stage) buttonOK.getScene().getWindow();
            stage.close();

        }
    }
}
