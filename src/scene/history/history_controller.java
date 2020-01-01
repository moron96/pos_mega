package scene.history;

import database.DBHelper;
import database.DBHelperSingleton;
import database.utils.MenuDatabaseUtils;
import database.utils.OrderDatabaseUtils;
import database.utils.OrderDetailDatabaseUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import model.History_Listview;
import model.Model_Table;
import model.Order;
import model.Order_Detail;
import print.pdf.PrinterTest;
import print.print.PrintBon;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Elbert Utama on 18/04/2017.
 */
public class history_controller implements Initializable {

    static history_controller history_controller;

    @FXML
    ListView<Order> history_listview;

    @FXML
    TableView<Model_Table> history_tableview;

    @FXML
    TableColumn<Model_Table, Integer> history_tableview_no;

    @FXML
    TableColumn<Model_Table, String> history_tableview_name;

    @FXML
    TableColumn<Model_Table, Double> history_tableview_price;

    @FXML
    TableColumn<Model_Table, Integer> history_tableview_quantity;

    @FXML
    TableColumn<Model_Table, Double> history_tableview_subtotal;

    @FXML
    Button history_button_print;

    @FXML
    Label history_label_customercodevalue;

    @FXML
    Label history_label_totalvalue;

    @FXML
    Label history_label_discountvalue;

    @FXML
    Label history_label_grandtotalvalue;

    private DBHelper dbHelper;

    private OrderDatabaseUtils orderDatabaseUtils;

    private OrderDetailDatabaseUtils orderDetailDatabaseUtils;

    private MenuDatabaseUtils menuDatabaseUtils;

    private ObservableList<Order> observableListOrder;

    private ObservableList<Order_Detail> observableListOrderDetail;

    private ObservableList<Model_Table> observableListModelTable;

    private History_Listview history_listview_model;

    private PrintBon printBon;

    private ArrayList<Model_Table> listModelTable;

    private double total;

    private double discount;

    private double grand_total;

    private Order order;

    @FXML
    void button_print(ActionEvent event)throws Exception{

        if(listModelTable != null) {

            printBon = new PrintBon();
            printBon.setData(listModelTable, total, discount, grand_total,order.getCustomer_code());
            printBon.doprintbon();

        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initializeHistoryListView();
        history_controller = this;

    }

    public void initializeHistoryListView() {

        dbHelper = DBHelperSingleton.getInstance();
        orderDatabaseUtils = new OrderDatabaseUtils(dbHelper);
        ArrayList<Order> listOrder = orderDatabaseUtils.getOrderListTodayHistory();


        observableListOrder = FXCollections.observableArrayList(listOrder);

        history_listview.setItems(observableListOrder);

        history_listview.setCellFactory(new Callback<ListView<Order>, ListCell<Order>>() {

            @Override
            public ListCell<Order> call(ListView<Order> paramater) {

                ListCell<Order> cell = new ListCell<Order>(){

                    @Override
                    protected void updateItem(Order order, boolean bln){
                        super.updateItem(order, bln);
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK);
                        if(order != null){

                            setText(String.format("%-25s | %25s", sdf.format(order.getDate()), order.getCustomer_code()));

                        }
                    }

                };

                return cell;

            }

        });

        history_listview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Order>() {
            @Override
            public void changed(ObservableValue<? extends Order> observable, Order oldValue, Order newValue) {

                if(newValue != null) {

                    initializeHistoryTableView(newValue.getId().intValue());

                    updateGrandTotal();

                    System.out.println("Selected item : " + newValue.getDate() + " | " + newValue.getCustomer_code());

                }

            }
        });

    }

    private void initializeHistoryTableView(int id) {

        orderDetailDatabaseUtils = new OrderDetailDatabaseUtils(dbHelper);
        menuDatabaseUtils = new MenuDatabaseUtils(dbHelper);

        ArrayList<Order_Detail> listOrderDetail = orderDetailDatabaseUtils.getOrderDetailsByOrderId(id);

        listModelTable = new ArrayList<>();

        for(int i = 0; i < listOrderDetail.size(); i++) {

            int no = i+1;
            String name = "NO NAME";

            if(menuDatabaseUtils.getMenuById(listOrderDetail.get(i).getMenu_id()) != null) {
                name = menuDatabaseUtils.getMenuById(listOrderDetail.get(i).getMenu_id()).getName();
            }

            double price = listOrderDetail.get(i).getPrice();
            int qty = listOrderDetail.get(i).getQty();
            double sub_total = price * qty;

            listModelTable.add(new Model_Table(listOrderDetail.get(i).getMenu_id(),no, name, price, qty, sub_total));
        }

        observableListModelTable = FXCollections.observableArrayList(listModelTable);

        history_tableview_no.setCellValueFactory(new PropertyValueFactory<Model_Table, Integer>("no"));
        history_tableview_no.setMinWidth(30);
        history_tableview_no.setPrefWidth(30);
        history_tableview_name.setCellValueFactory(new PropertyValueFactory<Model_Table, String>("name"));
        history_tableview_name.setMinWidth(200);
        history_tableview_name.setPrefWidth(200);
        history_tableview_price.setCellValueFactory(new PropertyValueFactory<Model_Table, Double>("price"));
        history_tableview_price.setMinWidth(100);
        history_tableview_price.setPrefWidth(100);
        history_tableview_quantity.setCellValueFactory(new PropertyValueFactory<Model_Table, Integer>("qty"));
        history_tableview_quantity.setMinWidth(50);
        history_tableview_quantity.setPrefWidth(50);
        history_tableview_subtotal.setCellValueFactory(new PropertyValueFactory<Model_Table, Double>("sub_total"));
        history_tableview_subtotal.setMinWidth(100);
        history_tableview_subtotal.setPrefWidth(100);

        history_tableview.setItems(observableListModelTable);

    }

    private void updateGrandTotal() {

        order = history_listview.getSelectionModel().getSelectedItem();

        ArrayList<Order_Detail> listOrderDetail = orderDetailDatabaseUtils.getOrderDetailsByOrderId(order.getId().intValue());

        total = 0;

        for(int i = 0; i < listOrderDetail.size(); i++) {

            total += listOrderDetail.get(i).getPrice() * listOrderDetail.get(i).getQty();

        }

        String customer_code = order.getCustomer_code();
        discount = order.getPromo_amount();
        grand_total = total - discount;

        history_label_customercodevalue.setText(customer_code);
        history_label_totalvalue.setText(String.valueOf(total));
        history_label_discountvalue.setText(String.valueOf(discount));
        history_label_grandtotalvalue.setText(String.valueOf(grand_total));

    }

    public static scene.history.history_controller getHistory_controller() {
        if(history_controller == null) {
            history_controller = new history_controller();
        }
        return history_controller;
    }

    public void setHistory_controller(scene.history.history_controller history_controller) {
        this.history_controller = history_controller;
    }
}
