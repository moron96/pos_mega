package scene.bayar;

import database.DBHelper;
import database.DBHelperSingleton;
import database.utils.OrderDatabaseUtils;
import database.utils.PaymentMethodDatabaseUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.Model_Table;
import model.Order;
import model.Order_Detail;
import model.Payment_Method;
import print.pdf.PrinterTest;
import print.print.PrintBon;
import scene.history.history_controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Elbert Utama on 18/04/2017.
 */
public class bayar_controller implements Initializable{

    @FXML
    private Label labelPayment;
    @FXML
    private Label labelGrandTotal;
    @FXML
    private Label labelPaymentMethod;
    @FXML
    private Label labelAmount;
    @FXML
    private Label labelExchange;
    @FXML
    private Label labelGrandTotal2;
    @FXML
    private Label labelExchange2;
    @FXML
    private Label labelWarning;
    @FXML
    private ComboBox<String> comboBoxPaymentMethod;
    @FXML
    private TextField textFieldAmount;
    @FXML
    private Button buttonBack;
    @FXML
    private Button buttonOK;

    private DBHelper dbHelper = DBHelperSingleton.getInstance();;

    private Order order;

    private Order_Detail order_detail;

    private OrderDatabaseUtils orderDatabaseUtils;

    private history_controller historyController;

    private double total;

    private double discount;

    private double grand_total;

    private PrintBon printBon;

    private ArrayList<Model_Table> listModelTable;

    public PaymentMethodDatabaseUtils paymentMethodDatabaseUtils = new PaymentMethodDatabaseUtils(dbHelper);


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initializeComboBoxPaymentMethod();

        comboBoxPaymentMethodListener();

        textFieldAmountListener();

    }

    private void initializeComboBoxPaymentMethod(){

        for (Payment_Method pm: paymentMethodDatabaseUtils.getAllPaymentMethods()
             ) {
            comboBoxPaymentMethod.getItems().add(pm.getName());
        }
        comboBoxPaymentMethod.setValue(comboBoxPaymentMethod.getItems().get(0));

    }

    private void comboBoxPaymentMethodListener(){

        comboBoxPaymentMethod.valueProperty().addListener(new ChangeListener<String>() {
            @Override public void changed(ObservableValue ov, String t, String t1) {

                if(comboBoxPaymentMethod.getValue().equals("Cash")){

                    labelAmount.setVisible(true);
                    labelExchange.setVisible(true);
                    labelExchange2.setVisible(true);
                    textFieldAmount.setVisible(true);

                }
                else {

                    labelAmount.setVisible(false);
                    labelExchange.setVisible(false);
                    labelExchange2.setVisible(false);
                    textFieldAmount.setVisible(false);

                }

            }
        });

    }

    private void textFieldAmountListener(){

        textFieldAmount.textProperty().addListener((observable, oldValue, newValue) -> {
            try {
                double grantTotal = Double.parseDouble(labelGrandTotal2.getText());
                double amount = 0;

                if(!textFieldAmount.getText().equals("") && validateNumeric(textFieldAmount.getText()) == true) {

                    amount = Double.parseDouble(textFieldAmount.getText());

                }

                double exchange = amount - grantTotal;

                if (exchange >= 0) {

                    labelExchange2.setText(String.valueOf(exchange));

                }
                else {

                    labelExchange2.setText("");

                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });

    }

    private boolean validateNumeric(String digit) {

        Matcher matcher = Pattern.compile("^[0-9]+").matcher(digit);
        return matcher.find();

    }

    private boolean validateExchange() {

        double grantTotal = Double.parseDouble(labelGrandTotal2.getText());
        double amount = Double.parseDouble(textFieldAmount.getText());
        double exchange = amount - grantTotal;

        if (exchange >= 0) {

            return true;

        }
        else {

            return false;

        }

    }

    @FXML
    private void buttonBackAction(ActionEvent event) {

        Stage stage = (Stage) buttonBack.getScene().getWindow();
        stage.close();

    }

    @FXML
    private void buttonOKAction(ActionEvent event) {

        if(comboBoxPaymentMethod.getValue().equals("Cash") && textFieldAmount.getText().equals("")) {

            labelWarning.setText("Amount should be filled");

        }
        else if(comboBoxPaymentMethod.getValue().equals("Cash") && validateNumeric(textFieldAmount.getText()) == false) {

            labelWarning.setText("Amount should be numeric");

        }
        else if(comboBoxPaymentMethod.getValue().equals("Cash") && validateExchange() == false) {

            labelWarning.setText("Amount should be equal or greater than Grand Total");

        }
        else {

            dbHelper = DBHelperSingleton.getInstance();
            orderDatabaseUtils = new OrderDatabaseUtils(dbHelper);

            int id = order.getId().intValue();
            String customer_code = order.getCustomer_code();
            Date date = order.getDate();
            int status_id = 2;
            int payment_method_id = order.getPayment_method_id();
            double promo_amount = order.getPromo_amount();
            Date deleted_at = order.getDeleted_at();
            String notes = order.getNotes();

            payment_method_id = paymentMethodDatabaseUtils.getPaymentMethodByName(comboBoxPaymentMethod.getValue()).getId();

            Order newOrder = new Order(id, customer_code, date, status_id, payment_method_id, promo_amount, deleted_at, notes);

            orderDatabaseUtils.updateOrder(newOrder);

            Stage stage = (Stage) buttonOK.getScene().getWindow();
            stage.close();

            history_controller.getHistory_controller().initializeHistoryListView();


            printBon = new PrintBon();
            printBon.setData(listModelTable, total, discount, grand_total ,order.getCustomer_code(),order);
            printBon.doprintbon();

        }

    }

    public void initializeOrder(Order orderFromOrderController) {

        order = orderFromOrderController;

    }

    public void initializeGrandTotal(String totalFromOrderController, String discountFromOrderController, String grandTotalFromOrderController) {

        total = Double.parseDouble(totalFromOrderController);
        discount = Double.parseDouble(discountFromOrderController);
        grand_total = Double.parseDouble(grandTotalFromOrderController);

        labelGrandTotal2.setText(grandTotalFromOrderController);

    }

    public void initializeModelTable(ArrayList<Model_Table> listModelTableFromOrderController) {

        listModelTable = listModelTableFromOrderController;

    }

}
