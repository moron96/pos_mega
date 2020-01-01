package scene.menu;

import database.DBHelper;
import database.DBHelperSingleton;
import database.utils.CategoryDatabaseUtils;
import database.utils.MenuDatabaseUtils;
import database.utils.OrderDetailDatabaseUtils;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.Category;
import model.Menu;
import model.Order;
import model.Order_Detail;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Elbert Utama on 18/04/2017.
 */
public class menu_controller implements Initializable {

    @FXML
    Button exit_button;

    @FXML
    Button order_button;

    @FXML
    Button search_button;

    @FXML
    TabPane menu_tabpane;

    @FXML
    TextField textFieldAmount;

    @FXML
    TextField textFieldSearch;

    @FXML
    Label labelWarning;

    @FXML
    TextArea textAreaNotes;

    private DBHelper dbHelper;
    private CategoryDatabaseUtils categoryDatabaseUtils;
    private MenuDatabaseUtils menuDatabaseUtils;
    private OrderDetailDatabaseUtils orderDetailDatabaseUtils;
    private ObservableList<Menu> observableList;
    private ListView listView = new ListView<Menu>();
    private Order order;
    int id;

    @FXML
    void button_exit(ActionEvent event){
        Stage stage = (Stage) exit_button.getScene().getWindow();
        stage.close();
    }

    @FXML
    void button_order (ActionEvent event){
        doOrder();
    }

    public void doOrder()
    {
        Menu menu = (Menu) listView.getSelectionModel().getSelectedItem();

        if(menu == null) {

            labelWarning.setText("No selected item");

        }
        else if(textFieldAmount.getText().equals("")) {

            labelWarning.setText("Amount should be filled");

        }
        else if(validateNumeric(textFieldAmount.getText()) == false) {

            labelWarning.setText("Amount should be numeric");

        }
        else if(Integer.parseInt(textFieldAmount.getText()) <= 0) {

            labelWarning.setText("Amount should be greater than 0");

        }
        else {

            int order_id = order.getId().intValue();
            int menu_id = menu.getId().intValue();
            int qty = Integer.parseInt(textFieldAmount.getText());
            double price = menu.getPrice();
            String notes = textAreaNotes.getText();
            int food_status_id = 1;

            orderDetailDatabaseUtils = new OrderDetailDatabaseUtils(dbHelper);
            Order_Detail order_detail = orderDetailDatabaseUtils.getOrderDetailByMenuIdAndOrderId(menu_id, order_id);

            if(order_detail != null) {

                int id = order_detail.getId().intValue();
                qty += order_detail.getQty();

                Order_Detail new_order_detail = new Order_Detail(id,order_id,menu_id,qty,price,notes,food_status_id);
                orderDetailDatabaseUtils.updateOrderDetail(new_order_detail);

            }
            else{

                orderDetailDatabaseUtils.insertNewOrderDetails(order_id, menu_id, qty, price, notes, food_status_id);

            }

            Stage stage = (Stage) order_button.getScene().getWindow();
            stage.close();

        }
    }


    @FXML
    void button_search(ActionEvent event)
    {
        doSearch();
    }

    public void doSearch()
    {
        dbHelper = DBHelperSingleton.getInstance();
        menuDatabaseUtils = new MenuDatabaseUtils(dbHelper);
        categoryDatabaseUtils = new CategoryDatabaseUtils(dbHelper);

        observableList = FXCollections.observableArrayList(menuDatabaseUtils.getMenuListByCategoryIdandname(id,textFieldSearch.getText()));
        listView.setItems(observableList);

        listView.setCellFactory(new Callback<ListView<Menu>, ListCell<Menu>>() {
            @Override
            public ListCell<Menu> call(ListView<Menu> param) {
                ListCell<Menu> cell = new ListCell<Menu>(){

                    @Override
                    protected void updateItem(Menu menu, boolean bln){
                        super.updateItem(menu, bln);
                        if(menu != null){

                            setText(String.format("%s \n%s\n", menu.getName(), menu.getPrice()));

                        }
                    }

                };
                return cell;

            }
        });
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        initialize_tab();

        initialize_pane();

        textFieldAmount.setText("1");

    }


    private void initialize_tab() {

        dbHelper = DBHelperSingleton.getInstance();
        categoryDatabaseUtils = new CategoryDatabaseUtils(dbHelper);
        ArrayList<Category> categorylist = categoryDatabaseUtils.getAllCategory();

        for (int i = 0;i<categorylist.size();i++) {
            Category category = categorylist.get(i);
            if(category!=null)
            {
                Tab tab = new Tab(category.getName());
                menu_tabpane.getTabs().add(tab);
            }
        }
    }


    private void initialize_pane(){

        menu_tabpane.getSelectionModel().clearSelection();

        menu_tabpane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>() {
            @Override
            public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue) {
                System.out.println("Tab selected: " + newValue.getText());
               // if (newValue.getContent() == null) {
                    GridPane gridpane = new GridPane();
                    gridpane.addColumn(0);
                    gridpane.addRow(0);
                    gridpane.setAlignment(Pos.CENTER);
                    listView = new ListView<Menu>();
                    listView.setPrefSize(1042,560);

                    dbHelper = DBHelperSingleton.getInstance();
                    menuDatabaseUtils = new MenuDatabaseUtils(dbHelper);
                    categoryDatabaseUtils = new CategoryDatabaseUtils(dbHelper);

                    id = categoryDatabaseUtils.getCategoryByName(newValue.getText()).getId().intValue();

                    observableList = FXCollections.observableArrayList(menuDatabaseUtils.getMenuListByCategoryId(id));
                    listView.setItems(observableList);

                    listView.setOnMouseClicked(new EventHandler<MouseEvent>() {
                        @Override
                        public void handle(MouseEvent event) {

                            if(event.getClickCount() == 2){
                                doOrder();
                            }
                        }
                    });

                    listView.setCellFactory(new Callback<ListView<Menu>, ListCell<Menu>>() {
                        @Override
                        public ListCell<Menu> call(ListView<Menu> param) {
                            ListCell<Menu> cell = new ListCell<Menu>(){

                                @Override
                                protected void updateItem(Menu menu, boolean bln){
                                    super.updateItem(menu, bln);
                                    if(menu != null){

                                        setText(String.format("%s \n%s\n", menu.getName(), menu.getPrice()));

                                    }
                                }

                            };
                            return cell;

                        }
                    });


                    gridpane.getChildren().add(0, listView);
                    newValue.setContent(gridpane);
                //}
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Menu>() {

            @Override
            public void changed(ObservableValue<? extends Menu> observable, Menu oldValue, Menu newValue) {

                System.out.println("Selected item : " + newValue.getName() + " | " + newValue.getPrice());

            }

        });

    }

    private boolean validateNumeric(String digit) {

        Matcher matcher = Pattern.compile("^[0-9]+").matcher(digit);
        return matcher.find();

    }



    @FXML
    private void handleKeyPressed(KeyEvent ke){
        if (ke.getCode().equals(KeyCode.ENTER))
        {
            doSearch();
        }
    }


    public void initializeOrder(Order orderFromOrderController) {

        order = orderFromOrderController;

    }



}
