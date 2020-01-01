package scene.order;

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
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import model.Menu;
import model.Model_Table;
import model.Order;
import model.Order_Detail;
import scene.bayar.bayar_controller;
import scene.history.history_controller;
import scene.menu.menu_controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Elbert Utama on 18/04/2017.
 */
public class order_controller implements Initializable {

    @FXML
    ListView<Order> order_listview;

    @FXML
    TableView<Model_Table> order_tableview;

    @FXML
    TableColumn<Model_Table, Integer> order_tableview_no;

    @FXML
    TableColumn<Model_Table, String> order_tableview_name;

    @FXML
    TableColumn<Model_Table, Double> order_tableview_price;

    @FXML
    TableColumn<Model_Table, Integer> order_tableview_quantity;

    @FXML
    TableColumn<Model_Table, Double> order_tableview_subtotal;

    @FXML
    Label order_label_totalvalue;

    @FXML
    Label order_label_discountvalue;

    @FXML
    Label order_label_grandtotalvalue;

    @FXML
    Button order_button_listplus;

    @FXML
    Button order_button_listminus;

    @FXML
    Button order_button_menuadd;

    @FXML
    Button order_button_menuremove;

    @FXML
    Button order_button_menuplus;

    @FXML
    Button order_button_menuminus;

    @FXML
    Button order_button_menu_bayar;

    @FXML
    Button order_button_list_apply;

    @FXML
    TextField order_textfield_customer_code;

    @FXML
    TextField order_textfield_discount;

    private DBHelper dbHelper;

    private OrderDatabaseUtils orderDatabaseUtils;

    private OrderDetailDatabaseUtils orderDetailDatabaseUtils;

    private MenuDatabaseUtils menuDatabaseUtils;

    private ObservableList<Order> observableListOrder;

    private ObservableList<Model_Table> observableListModelTable;

    private ArrayList<Order> listOrder;

    private ArrayList<Model_Table> listModelTable;

    private menu_controller menuController;

    private bayar_controller bayarController;

    private history_controller historyController;

    private boolean validateNumeric(String digit) {

        Matcher matcher = Pattern.compile("^[0-9]+").matcher(digit);
        return matcher.find();

    }

    @FXML
    void button_listplus(ActionEvent event)throws Exception{

        try {
            Stage dialogStage = new Stage();
            GridPane root = FXMLLoader.load(getClass().getResource("/scene/add_order/add_order.fxml"));
            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

            initializeOrderListView();

            resetGrandTotal();

            order_textfield_customer_code.setText("");
            order_textfield_discount.setText("");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @FXML
    void button_listminus(ActionEvent event)throws Exception{

        Order order = order_listview.getSelectionModel().getSelectedItem();

        if(order != null) {

            Stage stage = new Stage();
            Pane pane = new Pane();

            pane.setPrefSize(300, 200);

            Label labelText = new Label("Are you sure want to delete?");
            labelText.setPrefWidth(200);
            labelText.setAlignment(Pos.CENTER);
            labelText.setLayoutX(50);
            labelText.setLayoutY(70);

            Button buttonOK = new Button("OK");
            buttonOK.setPrefWidth(80);
            buttonOK.setLayoutX(60);
            buttonOK.setLayoutY(140);

            buttonOK.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    orderDatabaseUtils.softDeleteOrderById(order.getId().intValue());

                    initializeOrderListView();

                    resetGrandTotal();

                    order_textfield_customer_code.setText("");
                    order_textfield_discount.setText("");

                    stage.close();

                }
            });

            Button buttonCancel = new Button("Cancel");
            buttonCancel.setPrefWidth(80);
            buttonCancel.setLayoutX(160);
            buttonCancel.setLayoutY(140);

            buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    stage.close();

                }
            });

            pane.getChildren().add(labelText);
            pane.getChildren().add(buttonOK);
            pane.getChildren().add(buttonCancel);

            stage.setScene(new Scene(pane));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();

        }

    }

    @FXML
    void button_menuadd(ActionEvent event)throws Exception{

        Order order = order_listview.getSelectionModel().getSelectedItem();

        if(order != null) {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/menu/menu.fxml"));
                Parent add = (Parent) fxmlLoader.load();
                menu_controller controller = fxmlLoader.getController();
                controller.initializeOrder(order);
                Stage stage = new Stage();
                stage.setScene(new Scene(add));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.showAndWait();

                updateGrandTotal();

                initializeOrderTableView(order.getId().intValue());

            } catch (Exception e) {
                e.printStackTrace();
            }

        }


    }

    @FXML
    void button_menuremove(ActionEvent event)throws Exception{

        Model_Table model_table = order_tableview.getSelectionModel().getSelectedItem();
        Order order = order_listview.getSelectionModel().getSelectedItem();

        if(model_table != null && order != null) {

            Stage stage = new Stage();
            Pane pane = new Pane();

            pane.setPrefSize(300, 200);

            Label labelText = new Label("Are you sure want to delete?");
            labelText.setPrefWidth(200);
            labelText.setAlignment(Pos.CENTER);
            labelText.setLayoutX(50);
            labelText.setLayoutY(70);

            Button buttonOK = new Button("OK");
            buttonOK.setPrefWidth(80);
            buttonOK.setLayoutX(60);
            buttonOK.setLayoutY(140);

            buttonOK.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    Menu menu = menuDatabaseUtils.getMenuById(model_table.getId().intValue());

                    orderDetailDatabaseUtils.deleteOrderDetailByMenuIdAndOrderId(menu.getId().intValue(), order.getId().intValue());

                    initializeOrderTableView(order.getId().intValue());

                    updateGrandTotal();

                    stage.close();

                }
            });

            Button buttonCancel = new Button("Cancel");
            buttonCancel.setPrefWidth(80);
            buttonCancel.setLayoutX(160);
            buttonCancel.setLayoutY(140);

            buttonCancel.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    stage.close();

                }
            });

            pane.getChildren().add(labelText);
            pane.getChildren().add(buttonOK);
            pane.getChildren().add(buttonCancel);

            stage.setScene(new Scene(pane));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.showAndWait();

        }

    }

    @FXML
    void button_menuplus(ActionEvent event)throws Exception{

        Model_Table model_table = order_tableview.getSelectionModel().getSelectedItem();

        if(model_table != null) {

            Menu menu = menuDatabaseUtils.getMenuById(model_table.getId().intValue());
            Order order = order_listview.getSelectionModel().getSelectedItem();

            Order_Detail order_detail = orderDetailDatabaseUtils.getOrderDetailByMenuIdAndOrderId(menu.getId().intValue(), order.getId().intValue());

            int id = order_detail.getId().intValue();
            int order_id = order_detail.getOrder_id();
            int menu_id = order_detail.getMenu_id();
            int qty = order_detail.getQty() + 1;
            double price = order_detail.getPrice();
            String notes = order_detail.getNotes();
            int food_status_id = order_detail.getFood_status_id();

            Order_Detail new_order_detail = new Order_Detail(id, order_id, menu_id, qty, price, notes, food_status_id);

            orderDetailDatabaseUtils.updateOrderDetail(new_order_detail);

            initializeOrderTableView(order.getId().intValue());

            updateGrandTotal();

        }

    }

    @FXML
    void button_menuminus(ActionEvent event)throws Exception{

        Model_Table model_table = order_tableview.getSelectionModel().getSelectedItem();

        if(model_table != null) {

            Menu menu = menuDatabaseUtils.getMenuById(model_table.getId().intValue());
            Order order = order_listview.getSelectionModel().getSelectedItem();

            Order_Detail order_detail = orderDetailDatabaseUtils.getOrderDetailByMenuIdAndOrderId(menu.getId().intValue(), order.getId().intValue());

            int qty = order_detail.getQty();

            if(qty > 1) {

                int id = order_detail.getId().intValue();
                int order_id = order_detail.getOrder_id();
                int menu_id = order_detail.getMenu_id();
                qty = order_detail.getQty() - 1;
                double price = order_detail.getPrice();
                String notes = order_detail.getNotes();
                int food_status_id = order_detail.getFood_status_id();

                Order_Detail new_order_detail = new Order_Detail(id, order_id, menu_id, qty, price, notes, food_status_id);

                orderDetailDatabaseUtils.updateOrderDetail(new_order_detail);

            }
            else {

                orderDetailDatabaseUtils.deleteOrderDetailByMenuIdAndOrderId(menu.getId().intValue(), order.getId().intValue());

            }

            initializeOrderTableView(order.getId().intValue());

            updateGrandTotal();

        }

    }

    @FXML
    void button_menu_bayar(ActionEvent event)throws Exception{

        Order order = order_listview.getSelectionModel().getSelectedItem();
        double grand_total = Double.parseDouble(order_label_grandtotalvalue.getText());

        if(order != null && grand_total >= 0) {

            try {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/scene/bayar/bayar.fxml"));
                Parent bayar = fxmlLoader.load();
                bayar_controller controller = fxmlLoader.getController();
                controller.initializeOrder(order);
                controller.initializeModelTable(listModelTable);
                controller.initializeGrandTotal(order_label_totalvalue.getText(), order_label_discountvalue.getText(), order_label_grandtotalvalue.getText());
                Stage stage = new Stage();
                stage.setScene(new Scene(bayar));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.initStyle(StageStyle.UNDECORATED);
                stage.showAndWait();

                initializeOrderListView();
                initializeOrderTableView(0);
                resetGrandTotal();

                order_textfield_customer_code.setText("");
                order_textfield_discount.setText("");

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }

    @FXML
    void button_list_apply(ActionEvent event)throws Exception{

        Order order = order_listview.getSelectionModel().getSelectedItem();

        if(order != null && !order_textfield_customer_code.getText().equals("") && !order_textfield_discount.getText().equals("") && validateNumeric(order_textfield_discount.getText()) == true) {

            int id = order.getId().intValue();
            String customer_code = order_textfield_customer_code.getText();
            Date date = order.getDate();
            int status_id = order.getStatus_id();
            int payment_method_id = order.getPayment_method_id();
            double promo_amount = Double.parseDouble(order_textfield_discount.getText());
            Date deleted_at = order.getDeleted_at();
            String notes = order.getNotes();

            Order new_order = new Order(id, customer_code, date, status_id, payment_method_id, promo_amount, deleted_at, notes);

            orderDatabaseUtils.updateOrder(new_order);

            order_textfield_customer_code.setText("");
            order_textfield_discount.setText("");

            initializeOrderListView();

            resetGrandTotal();

        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initializeOrderListView();

    }

    private void initializeOrderListView() {

        dbHelper = DBHelperSingleton.getInstance();
        orderDatabaseUtils = new OrderDatabaseUtils(dbHelper);
        listOrder = orderDatabaseUtils.getOrderListToday();

        observableListOrder = FXCollections.observableArrayList(listOrder);

        //history_listview.setPrefSize(200, 200);
        //history_listview.setEditable(true);

        order_listview.setItems(observableListOrder);

        order_listview.setCellFactory(new Callback<ListView<Order>, ListCell<Order>>() {

            @Override
            public ListCell<Order> call(ListView<Order> paramater) {

                ListCell<Order> cell = new ListCell<Order>(){

                    @Override
                    protected void updateItem(Order order, boolean bln){
                        super.updateItem(order, bln);
                        if(order != null){

                            setText(String.format("%-100s", order.getCustomer_code()));

                        }
                    }

                };

                return cell;

            }

        });

        order_listview.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Order>() {
            @Override
            public void changed(ObservableValue<? extends Order> observable, Order oldValue, Order newValue) {

                if(newValue != null) {

                    initializeOrderTableView(newValue.getId().intValue());

                    order_textfield_customer_code.setText(newValue.getCustomer_code());
                    order_textfield_discount.setText(String.valueOf(newValue.getPromo_amount()));

                    updateGrandTotal();

                    System.out.println("Selected item : " + newValue.getCustomer_code());

                }
                else {

                    initializeOrderTableView(0);
                    /*order_textfield_customer_code.setText("");
                    order_textfield_discount.setText("");
                    resetGrandTotal();*/

                }

            }
        });

    }

    private void initializeOrderTableView(int id) {

        if(id>0) {
            orderDetailDatabaseUtils = new OrderDetailDatabaseUtils(dbHelper);
            menuDatabaseUtils = new MenuDatabaseUtils(dbHelper);

            ArrayList<Order_Detail> listOrderDetail = orderDetailDatabaseUtils.getOrderDetailsByOrderId(id);

            listModelTable = new ArrayList<>();

            for (int i = 0; i < listOrderDetail.size(); i++) {

                int no = i + 1;
                String name = "NO NAME";

                if (menuDatabaseUtils.getMenuById(listOrderDetail.get(i).getMenu_id()) != null) {
                    name = menuDatabaseUtils.getMenuById(listOrderDetail.get(i).getMenu_id()).getName();
                }

                double price = listOrderDetail.get(i).getPrice();
                int qty = listOrderDetail.get(i).getQty();
                double sub_total = price * qty;

                listModelTable.add(new Model_Table(listOrderDetail.get(i).getMenu_id(),no, name, price, qty, sub_total));
            }

            observableListModelTable = FXCollections.observableArrayList(listModelTable);

            order_tableview_no.setCellValueFactory(new PropertyValueFactory<Model_Table, Integer>("no"));
            order_tableview_no.setMaxWidth(30);
            order_tableview_no.setMinWidth(30);
            order_tableview_no.setPrefWidth(30);
            order_tableview_name.setCellValueFactory(new PropertyValueFactory<Model_Table, String>("name"));
            order_tableview_name.setMaxWidth(200);
            order_tableview_name.setMinWidth(200);
            order_tableview_name.setPrefWidth(200);
            order_tableview_price.setCellValueFactory(new PropertyValueFactory<Model_Table, Double>("price"));
            order_tableview_price.setMaxWidth(100);
            order_tableview_price.setMinWidth(100);
            order_tableview_price.setPrefWidth(100);
            order_tableview_quantity.setCellValueFactory(new PropertyValueFactory<Model_Table, Integer>("qty"));
            order_tableview_quantity.setMaxWidth(50);
            order_tableview_quantity.setMinWidth(50);
            order_tableview_quantity.setPrefWidth(50);
            order_tableview_subtotal.setCellValueFactory(new PropertyValueFactory<Model_Table, Double>("sub_total"));
            order_tableview_subtotal.setMaxWidth(100);
            order_tableview_subtotal.setMinWidth(100);
            order_tableview_subtotal.setPrefWidth(100);/*
            order_tableview_no.prefWidthProperty().bind(order_tableview.widthProperty().divide(10));
            order_tableview_name.prefWidthProperty().bind(order_tableview.widthProperty().divide(2));
            order_tableview_price.prefWidthProperty().bind(order_tableview.widthProperty().divide(10));
            order_tableview_quantity.prefWidthProperty().bind(order_tableview.widthProperty().divide(10));
            order_tableview_subtotal.prefWidthProperty().bind(order_tableview.widthProperty().divide(5));
*/
            order_tableview.setItems(observableListModelTable);
        }
        else {
            order_tableview.setItems(null);
        }
    }

    private void resetGrandTotal() {

        order_label_totalvalue.setText("0.0");
        order_label_discountvalue.setText("0.0");
        order_label_grandtotalvalue.setText("0.0");

    }

    private void updateGrandTotal() {

        Order order = order_listview.getSelectionModel().getSelectedItem();

        ArrayList<Order_Detail> listOrderDetail = orderDetailDatabaseUtils.getOrderDetailsByOrderId(order.getId().intValue());

        double total = 0;

        for(int i = 0; i < listOrderDetail.size(); i++) {

            total += listOrderDetail.get(i).getPrice() * listOrderDetail.get(i).getQty();

        }

        double discount = order.getPromo_amount();
        double grand_total = total - discount;

        order_label_totalvalue.setText(String .valueOf(total));
        order_label_discountvalue.setText(String.valueOf(discount));
        order_label_grandtotalvalue.setText(String .valueOf(grand_total));

    }

}
