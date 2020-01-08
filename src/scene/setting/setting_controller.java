package scene.setting;

import database.DBHelper;
import database.DBHelperSingleton;
import database.utils.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.Duration;
import model.*;
import model.Menu;
import org.controlsfx.control.Notifications;
import print.pdf.PrinterTest;
import print.pdf.pdf_generator;
import print.print.PrintDailyReport;
import print.print.PrinterSetting;
import utils.SettingsUtils;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ASUS on 21/04/2017.
 */
public class setting_controller implements Initializable {

    private DBHelper dbHelper = DBHelperSingleton.getInstance();
    private CategoryDatabaseUtils categoryDatabaseUtils = new CategoryDatabaseUtils(dbHelper);
    private UserDatabaseUtils userDatabaseUtils = new UserDatabaseUtils(dbHelper);
    private MenuDatabaseUtils menuDatabaseUtils = new MenuDatabaseUtils(dbHelper);
    private PaymentMethodDatabaseUtils paymentMethodDatabaseUtils = new PaymentMethodDatabaseUtils(dbHelper);
    private OrderDatabaseUtils orderDatabaseUtils = new OrderDatabaseUtils(dbHelper);
    private OrderDetailDatabaseUtils orderDetailDatabaseUtils = new OrderDetailDatabaseUtils(dbHelper);
    private SettingsUtils settingsUtils;
    private ObservableList<Category> categorylist;
    private ObservableList<Menu> menulist;
    private ObservableList<Payment_Method> paymentlist;

    //--------------------------------------------------------------------------------------------- Settings
    @FXML
    GridPane main_grid_pane;
    @FXML
    Button setting_button_close;

    @FXML
    void close_button(ActionEvent event){
        Stage stage = (Stage) setting_button_close.getScene().getWindow();
        stage.close();
    }


    //--------------------------------------------------------------------------------------------- History
    @FXML
    ListView<Order> setting_history_listview;
    @FXML
    Button setting_history_button_deleteall;
    @FXML
    Button setting_history_button_printrecap;
    @FXML
    Button setting_history_button_printsettings;
    @FXML
    DatePicker setting_history_datepicker;

    @FXML
    void history_monthly_report_button(){
        LocalDate localDate = setting_history_datepicker.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        Date date = Date.from(instant);
        SimpleDateFormat month = new SimpleDateFormat("MMMM", Locale.UK);
        SimpleDateFormat year = new SimpleDateFormat("yyyy", Locale.UK);


        Stage stage = new Stage();
        Pane pane = new Pane();

        pane.setPrefSize(300, 200);

        Label labelText = new Label("Are you sure want to create report for "+month.format(date)+" "+year.format(date)+"?");
        labelText.setPrefWidth(200);
        labelText.setWrapText(true);
        labelText.setMaxWidth(200);
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

                pdf_generator pdf_generator = new pdf_generator();
                if (pdf_generator.generateReport(date)) {
                    notification("Create pdf success", "PDF has been saved", main_grid_pane);
                } else {
                    notification("Failed creating pdf", "Failed to create ODF", main_grid_pane);
                }

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

    @FXML
    void history_printrecap_button(){
        PrintDailyReport printDailyReport = new PrintDailyReport();
        printDailyReport.doPrintDaily();
    }

    @FXML
    void history_printsettings_button(){
        PrintDailyReport printDailyReport = new PrintDailyReport();
        printDailyReport.displayDialog();
    }

    void set_setting_history_listview(){
        setting_history_datepicker.setValue(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

        populate_setting_history_listview();
    }

    @FXML
    void populate_setting_history_listview() {
        LocalDate localDate = setting_history_datepicker.getValue();
        Instant instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        Date date = Date.from(instant);

        setting_history_listview.setItems(FXCollections.observableArrayList(orderDatabaseUtils.getOrderListHistoryByDate(date)));

        setting_history_listview.setCellFactory(new Callback<ListView<Order>, ListCell<Order>>() {
            @Override
            public ListCell<Order> call(ListView<Order> param) {
                ListCell<Order> cell = new ListCell<Order>()
                {
                    @Override
                    protected void updateItem(Order order, boolean bln){
                        super.updateItem(order, bln);
                        if(order != null){
                            Payment_Method pm = paymentMethodDatabaseUtils.getPaymentMethodById(order.getPayment_method_id());
                            String payment = "Null";
                            if (pm != null) {
                                payment = pm.getName();
                            }
                            setText(String.format("%-21s %10s - Rp %9.0f | %3d items",
                                    order.getCustomer_code().substring(0,Math.min(20,order.getCustomer_code().length())).trim(),
                                    payment.trim(),
                                    orderDatabaseUtils.getOrderTotalByID(order.getId()),
                                    orderDatabaseUtils.getOrderQuantityByID(order.getId())));
                            setFont(Font.font("Monospace"));
                        }
                    }
                };
                return cell;
            }
        });
    }

    private void notification(String title, String text, GridPane grid) {
        Notifications notification = Notifications.create()
                .title(title)
                .text(text)
                .hideAfter(Duration.seconds(2.5))
                .position(Pos.BASELINE_LEFT)
                .owner(grid)
                .position(Pos.BOTTOM_RIGHT);
        notification.showConfirm();
    }

    //--------------------------------------------------------------------------------------------- Category
    @FXML
    ComboBox<String> setting_category_combobox;
    @FXML
    Label setting_category_label_title;
    @FXML
    Label setting_category_label_warning;
    @FXML
    Label setting_category_label_category;
    @FXML
    Label setting_category_label_category_symbol;
    @FXML
    TextField setting_category_textfield_category;
    @FXML
    Button setting_category_button_add;
    @FXML
    Button setting_category_button_delete;
    @FXML
    Button setting_category_button_update;
    @FXML
    Button setting_category_button_back;
    @FXML
    ListView<Category> setting_category_listview;

    @FXML
    void category_add_button(){

        setting_category_label_warning.setTextFill(Paint.valueOf("RED"));

        if (setting_category_textfield_category.getText().equals(""))
        {
            setting_category_label_warning.setText("Name must be filled");
        }
        else if (categoryDatabaseUtils.getCategoryByName(setting_category_textfield_category.getText())!=null)
        {
            setting_category_label_warning.setText("Category already exist");
        }
        else {
            switch (setting_category_combobox.getValue())
            {
                case "View List" :
                    System.out.println(setting_category_textfield_category.getText());
                    System.out.println(setting_category_listview.getSelectionModel().getSelectedItem().getName());
                    Category category = categoryDatabaseUtils.getCategoryByName(setting_category_listview.getSelectionModel().getSelectedItem().getName());
                    System.out.println(category);
                    category.setSlug(setting_category_textfield_category.getText());
                    category.setName(setting_category_textfield_category.getText());
                    categoryDatabaseUtils.updateCategory(category);
                    setting_category_label_warning.setTextFill(Paint.valueOf("BLACK"));
                    setting_category_label_warning.setText("Completed");
                    setting_category_textfield_category.setText("");
                    break;
                case "Add New" :
                    categoryDatabaseUtils.insertNewCategory(setting_category_textfield_category.getText(),setting_category_textfield_category.getText());
                    setting_category_label_warning.setTextFill(Paint.valueOf("BLACK"));
                    setting_category_label_warning.setText("Completed");
                    setting_category_textfield_category.setText("");
                    break;
            }
            set_menu_category_combobox();
            set_setting_category_listview();
        }
    }

    @FXML
    void category_delete_button(){

        Category category = setting_category_listview.getSelectionModel().getSelectedItem();

        if(category != null) {

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

                    categoryDatabaseUtils.deleteCategoryById(category.getId().intValue());

                    menuDatabaseUtils.deleteMenuByCategoryId(category.getId().intValue());

                    set_setting_category_listview();
                    set_menu_category_combobox();
                    set_setting_menu_listview();

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
    void category_update_button(){
        Category category = setting_category_listview.getSelectionModel().getSelectedItem();

        if(category != null) {
            set_visible_off_category();
            setting_category_label_title.setText("Update Existing Category");
            setting_category_combobox.setVisible(false);
            setting_category_label_category.setVisible(true);
            setting_category_label_category_symbol.setVisible(true);
            setting_category_textfield_category.setVisible(true);
            setting_category_textfield_category.setText(category.getName());
            setting_category_button_add.setVisible(true);
            setting_category_button_add.setText("Update");
            setting_category_button_back.setVisible(true);
        }
    }

    @FXML
    void category_back_button(){
        setting_category_textfield_category.setText("");
        setting_category_label_warning.setText("");
        setting_category_button_add.setText("Add");
        set_category_visible_by_value(setting_category_combobox.getValue());
    }


    void set_setting_category_listview()
    {
        categorylist = FXCollections.observableArrayList(categoryDatabaseUtils.getAllCategory());
        setting_category_listview.setItems(categorylist);

        setting_category_listview.setCellFactory(new Callback<ListView<Category>, ListCell<Category>>() {
            @Override
            public ListCell<Category> call(ListView<Category> param) {
                ListCell<Category> cell = new ListCell<Category>()
                {
                    @Override
                    protected void updateItem(Category category, boolean bln){
                        super.updateItem(category, bln);
                        if(category != null){
                            setText(String.format("%-100s", category.getName()));
                        }
                    }
                };
                return cell;
            }
        });
    }

    void set_setting_category_combobox()
    {
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("View List");
        arrayList.add("Add New");
        setting_category_combobox.getItems().clear();
        setting_category_combobox.setItems(FXCollections.observableArrayList(arrayList));
        setting_category_combobox.setValue("Add New");

        set_category_visible_by_value(setting_category_combobox.getValue());

        setting_category_combobox.valueProperty().addListener((observable, oldValue, newValue) -> {
            set_category_visible_by_value(newValue);
            set_setting_category_listview();
        });

    }

    void set_category_visible_by_value(String value)
    {
        setting_category_label_warning.setText("");
        setting_category_textfield_category.setText("");
        set_visible_off_category();

        switch (value)
        {
            case "View List" :
                setting_category_label_title.setText("View Category List");
                setting_category_combobox.setVisible(true);
                setting_category_button_delete.setVisible(true);
                setting_category_button_update.setVisible(true);
                setting_category_listview.setVisible(true);
                break;
            case "Add New" :
                setting_category_label_title.setText("Input New Category");
                setting_category_combobox.setVisible(true);
                setting_category_label_category.setVisible(true);
                setting_category_label_category_symbol.setVisible(true);
                setting_category_textfield_category.setVisible(true);
                setting_category_button_add.setVisible(true);
                break;
        }

    }

    void set_visible_off_category()
    {
        setting_category_combobox.setVisible(false);
        setting_category_label_category.setVisible(false);
        setting_category_label_category_symbol.setVisible(false);
        setting_category_textfield_category.setVisible(false);
        setting_category_button_add.setVisible(false);
        setting_category_button_delete.setVisible(false);
        setting_category_button_update.setVisible(false);
        setting_category_button_back.setVisible(false);
        setting_category_listview.setVisible(false);
    }



    //--------------------------------------------------------------------------------------------- Menu
    @FXML
    ComboBox<String> setting_menu_category_combobox;
    @FXML
    ComboBox<String> setting_menu_combobox;
    @FXML
    Label setting_menu_label_menucategory;
    @FXML
    Label setting_menu_label_menucategory_symbol;
    @FXML
    Label setting_menu_label_menuname;
    @FXML
    Label setting_menu_label_menuname_symbol;
    @FXML
    Label setting_menu_label_menuprice;
    @FXML
    Label setting_menu_label_menuprice_symbol;
    @FXML
    Label setting_menu_label_title;
    @FXML
    Label setting_menu_label_warning;
    @FXML
    TextField setting_menu_textfield_menuname;
    @FXML
    TextField setting_menu_textfield_menuprice;
    @FXML
    Button setting_menu_button_back;
    @FXML
    Button setting_menu_button_add;
    @FXML
    Button setting_menu_button_delete;
    @FXML
    Button setting_menu_button_update;
    @FXML
    ListView<Menu> setting_menu_listview;

    @FXML
    void menu_add_button(){

        setting_menu_label_warning.setTextFill(Paint.valueOf("RED"));

        if (setting_menu_textfield_menuname.getText().equals("")||setting_menu_textfield_menuprice.getText().equals(""))
        {
            setting_menu_label_warning.setText("Menu name and price must be filled");
        }
        else if (menuDatabaseUtils.getMenuByName(setting_menu_textfield_menuname.getText())!=null)
        {
            if(menuDatabaseUtils.getMenuByName(setting_menu_textfield_menuname.getText()).getId().compareTo(
                    categoryDatabaseUtils.getCategoryByName(setting_menu_category_combobox.getValue()).getId())==0)
            {

                setting_menu_label_warning.setText("Menu name already exist");
            }
            else {
                add_menu();
            }
        }
        else {
            add_menu();
        }
    }

    @FXML
    void menu_delete_button(){

        Menu menu = setting_menu_listview.getSelectionModel().getSelectedItem();

        if(menu != null) {

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

                    menuDatabaseUtils.deleteMenuById(menu.getId().intValue());

                    set_setting_menu_listview();

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
    void menu_update_button(){
        Menu menu = setting_menu_listview.getSelectionModel().getSelectedItem();

        if(menu != null) {
            set_visible_off_menu();
            setting_menu_label_title.setText("Update Existing Category");
            setting_menu_combobox.setVisible(false);
            setting_menu_label_menuname.setVisible(true);
            setting_menu_label_menuname_symbol.setVisible(true);
            setting_menu_textfield_menuname.setVisible(true);
            setting_menu_textfield_menuname.setText(menu.getName());
            setting_menu_label_menuprice.setVisible(true);
            setting_menu_label_menuprice_symbol.setVisible(true);
            setting_menu_textfield_menuprice.setVisible(true);
            setting_menu_textfield_menuprice.setText(String.valueOf(menu.getPrice()));
            setting_menu_button_add.setVisible(true);
            setting_menu_button_add.setText("Update");
            setting_menu_button_back.setVisible(true);
            setting_menu_category_combobox.setValue(
                    categoryDatabaseUtils.getCategoryById(
                            menu.getCategory_id()).getName());
        }
    }

    @FXML
    void menu_back_button(){
        setting_menu_textfield_menuname.setText("");
        setting_menu_textfield_menuprice.setText("");
        setting_menu_label_warning.setText("");
        setting_menu_button_add.setText("Add");
        set_setting_menu_listview();
        set_menu_visible_by_value(setting_menu_combobox.getValue());
    }


    void add_menu()
    {
        switch (setting_menu_combobox.getValue())
        {
            case "View List" :
                System.out.println(setting_menu_textfield_menuname.getText());
                System.out.println(setting_menu_textfield_menuprice.getText());
                System.out.println(setting_menu_listview.getSelectionModel().getSelectedItem().getName());
                Menu menu = menuDatabaseUtils.getMenuByName(setting_menu_listview.getSelectionModel().getSelectedItem().getName());
                System.out.println(menu);
                menu.setPrice(Double.valueOf(setting_menu_textfield_menuprice.getText()));
                menu.setName(setting_menu_textfield_menuname.getText());
                menu.setCategory_id(categoryDatabaseUtils.getCategoryByName(setting_menu_category_combobox.getValue()).getId().intValue());
                menuDatabaseUtils.updateMenu(menu);
                setting_menu_label_warning.setTextFill(Paint.valueOf("BLACK"));
                setting_menu_label_warning.setText("Completed");
                setting_menu_textfield_menuname.setText("");
                setting_menu_textfield_menuprice.setText("");
                break;
            case "Add New" :
                menuDatabaseUtils.insertNewMenu(
                        setting_menu_textfield_menuname.getText(),
                        categoryDatabaseUtils.getCategoryByName(setting_menu_category_combobox.getValue()).getId().intValue(),
                        Double.parseDouble(setting_menu_textfield_menuprice.getText()),
                        null
                );
                setting_menu_label_warning.setTextFill(Paint.valueOf("BLACK"));
                setting_menu_label_warning.setText("Completed");
                setting_menu_textfield_menuname.setText("");
                setting_menu_textfield_menuprice.setText("");
                break;
        }
    }

    void set_menu_category_combobox()
    {
        ArrayList<String> list = new ArrayList<String>();
        for (Category category:categoryDatabaseUtils.getAllCategory()) {
            list.add(category.getName());
        }

        if (list.size()==0)
        {

        }
        else {
            //setting_menu_category_combobox.getItems().clear();
            setting_menu_category_combobox.setItems(FXCollections.observableArrayList(list));
            setting_menu_category_combobox.setValue(list.get(0));
            System.out.println(setting_menu_category_combobox.getValue());

            setting_menu_category_combobox.valueProperty().addListener((observable, oldValue, newValue) -> {
                set_setting_menu_listview();
            });
            list.clear();
        }

    }

    void set_setting_menu_listview()
    {
        if (setting_menu_category_combobox.getSelectionModel().getSelectedItem()==null)
        {

        }
        else {

            System.out.println("TES TES FUCK YOU " + setting_menu_category_combobox.getSelectionModel().getSelectedItem());


            menulist = FXCollections.observableArrayList(
                    menuDatabaseUtils.getMenuListByCategoryId(
                            categoryDatabaseUtils.getCategoryByName(
                                    setting_menu_category_combobox.getValue()).getId().intValue()));

            setting_menu_listview.setItems(menulist);

            setting_menu_listview.setCellFactory(new Callback<ListView<Menu>, ListCell<Menu>>() {
                @Override
                public ListCell<Menu> call(ListView<Menu> param) {
                    ListCell<Menu> cell = new ListCell<Menu>() {
                        @Override
                        protected void updateItem(Menu menu, boolean bln) {
                            super.updateItem(menu, bln);
                            if (menu != null) {
                                setText(String.format("%-80s \n%s", menu.getName(),menu.getPrice()));
                            }
                        }
                    };
                    return cell;
                }
            });
        }
    }

    void set_setting_menu_combobox()
    {
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("View List");
        arrayList.add("Add New");
        setting_menu_combobox.getItems().clear();
        setting_menu_combobox.setItems(FXCollections.observableArrayList(arrayList));
        setting_menu_combobox.setValue("Add New");

        set_menu_visible_by_value(setting_menu_combobox.getValue());

        setting_menu_combobox.valueProperty().addListener((observable, oldValue, newValue) -> {
            setting_menu_label_warning.setText("");
            set_setting_menu_listview();
            set_menu_visible_by_value(newValue);
        });

    }

    void set_menu_visible_by_value(String value)
    {
        setting_menu_label_warning.setText("");
        setting_menu_textfield_menuname.setText("");
        setting_menu_textfield_menuprice.setText("");
        set_visible_off_menu();

        switch (value)
        {
            case "View List" :
                setting_menu_label_title.setText("View Menu List");
                setting_menu_button_delete.setVisible(true);
                setting_menu_button_update.setVisible(true);
                setting_menu_listview.setVisible(true);
                break;
            case "Add New" :
                setting_menu_label_title.setText("Input New Category");
                setting_menu_label_menuname.setVisible(true);
                setting_menu_label_menuname_symbol.setVisible(true);
                setting_menu_label_menuprice.setVisible(true);
                setting_menu_label_menuprice_symbol.setVisible(true);
                setting_menu_textfield_menuname.setVisible(true);
                setting_menu_textfield_menuprice.setVisible(true);
                setting_menu_button_add.setVisible(true);
                break;
        }
    }

    void set_visible_off_menu()
    {
        setting_menu_combobox.setVisible(true);
        setting_menu_label_menuname.setVisible(false);
        setting_menu_label_menuname_symbol.setVisible(false);
        setting_menu_label_menuprice.setVisible(false);
        setting_menu_label_menuprice_symbol.setVisible(false);
        setting_menu_textfield_menuname.setVisible(false);
        setting_menu_textfield_menuprice.setVisible(false);
        setting_menu_button_back.setVisible(false);
        setting_menu_button_add.setVisible(false);
        setting_menu_button_delete.setVisible(false);
        setting_menu_button_update.setVisible(false);
        setting_menu_listview.setVisible(false);
    }



    //--------------------------------------------------------------------------------------------- Payment_method
    @FXML
    ComboBox<String> setting_payment_method_combobox;
    @FXML
    Label setting_payment_method_label_name;
    @FXML
    Label setting_payment_method_label_name_symbol;
    @FXML
    Label setting_payment_method_label_title;
    @FXML
    Label setting_payment_method_label_warning;
    @FXML
    TextField setting_payment_method_textfield_name;
    @FXML
    Button setting_payment_method_button_back;
    @FXML
    Button setting_payment_method_button_add;
    @FXML
    Button setting_payment_method_button_delete;
    @FXML
    Button setting_payment_method_button_update;
    @FXML
    ListView<Payment_Method> setting_payment_method_listview;

    @FXML
    void payment_method_add_button(){

        setting_payment_method_label_warning.setTextFill(Paint.valueOf("RED"));

        if (setting_payment_method_textfield_name.getText().equals(""))
        {
            setting_payment_method_label_warning.setText("Payment method name and price must be filled");
        }
        else if (paymentMethodDatabaseUtils.getPaymentMethodByName(setting_payment_method_textfield_name.getText())!=null)
        {
            setting_payment_method_label_warning.setText("Payment method name already exist");
        }
        else {
            add_payment_method();
        }
    }

    @FXML
    void payment_method_delete_button(){

        Payment_Method paymentMethod = setting_payment_method_listview.getSelectionModel().getSelectedItem();

        if(paymentMethod != null) {

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

                    paymentMethodDatabaseUtils.deletePaymentMethodById(paymentMethod.getId().intValue());

                    set_setting_payment_method_listview();

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
    void payment_method_update_button(){
        Payment_Method paymentMethod = setting_payment_method_listview.getSelectionModel().getSelectedItem();

        if(paymentMethod != null) {
            set_visible_off_payment_method();
            setting_payment_method_label_title.setText("Update Existing Category");
            setting_payment_method_combobox.setVisible(false);
            setting_payment_method_label_name.setVisible(true);
            setting_payment_method_label_name_symbol.setVisible(true);
            setting_payment_method_textfield_name.setVisible(true);
            setting_payment_method_textfield_name.setText(paymentMethod.getName());
            setting_payment_method_button_add.setVisible(true);
            setting_payment_method_button_add.setText("Update");
            setting_payment_method_button_back.setVisible(true);
        }
    }

    @FXML
    void payment_method_back_button(){
        setting_payment_method_textfield_name.setText("");
        setting_payment_method_label_warning.setText("");
        setting_payment_method_button_add.setText("Add");
        set_setting_payment_method_listview();
        set_payment_method_visible_by_value(setting_payment_method_combobox.getValue());
    }


    void add_payment_method()
    {
        switch (setting_payment_method_combobox.getValue())
        {
            case "View List" :
                System.out.println(setting_payment_method_textfield_name.getText());
                System.out.println(setting_payment_method_listview.getSelectionModel().getSelectedItem().getName());
                Payment_Method paymentMethod = paymentMethodDatabaseUtils.getPaymentMethodByName(setting_payment_method_listview.getSelectionModel().getSelectedItem().getName());
                System.out.println(paymentMethod);
                paymentMethod.setName(setting_payment_method_textfield_name.getText());
                paymentMethodDatabaseUtils.updatePaymentMethod(paymentMethod);
                setting_payment_method_label_warning.setTextFill(Paint.valueOf("BLACK"));
                setting_payment_method_label_warning.setText("Completed");
                setting_payment_method_textfield_name.setText("");
                break;
            case "Add New" :
                paymentMethodDatabaseUtils.insertNewPaymentMethod(
                        setting_payment_method_textfield_name.getText(),null
                );
                setting_payment_method_label_warning.setTextFill(Paint.valueOf("BLACK"));
                setting_payment_method_label_warning.setText("Completed");
                setting_payment_method_textfield_name.setText("");
                break;
        }
    }

    void set_setting_payment_method_listview()
    {
        paymentlist = FXCollections.observableArrayList(
                paymentMethodDatabaseUtils.getAllPaymentMethods());

        setting_payment_method_listview.setItems(paymentlist);

        setting_payment_method_listview.setCellFactory(new Callback<ListView<Payment_Method>, ListCell<Payment_Method>>() {
            @Override
            public ListCell<Payment_Method> call(ListView<Payment_Method> param) {
                ListCell<Payment_Method> cell = new ListCell<Payment_Method>() {
                    @Override
                    protected void updateItem(Payment_Method payment_method, boolean bln) {
                        super.updateItem(payment_method, bln);
                        if (payment_method != null) {
                            setText(String.format("%-80s", payment_method.getName()));
                        }
                    }
                };
                return cell;
            }
        });
    }

    void set_setting_payment_method_combobox()
    {
        ArrayList<String> arrayList = new ArrayList<String>();
        arrayList.add("View List");
        arrayList.add("Add New");
        setting_payment_method_combobox.getItems().clear();
        setting_payment_method_combobox.setItems(FXCollections.observableArrayList(arrayList));
        setting_payment_method_combobox.setValue("Add New");

        set_payment_method_visible_by_value(setting_payment_method_combobox.getValue());

        setting_payment_method_combobox.valueProperty().addListener((observable, oldValue, newValue) -> {
            setting_payment_method_label_warning.setText("");
            set_setting_payment_method_listview();
            set_payment_method_visible_by_value(newValue);
        });

    }

    void set_payment_method_visible_by_value(String value)
    {
        setting_payment_method_label_warning.setText("");
        setting_payment_method_textfield_name.setText("");
        set_visible_off_payment_method();

        switch (value)
        {
            case "View List" :
                setting_payment_method_label_title.setText("View Payment Method List");
                setting_payment_method_button_delete.setVisible(true);
                setting_payment_method_button_update.setVisible(true);
                setting_payment_method_listview.setVisible(true);
                break;
            case "Add New" :
                setting_payment_method_label_title.setText("Input New Category");
                setting_payment_method_label_name.setVisible(true);
                setting_payment_method_label_name_symbol.setVisible(true);
                setting_payment_method_textfield_name.setVisible(true);
                setting_payment_method_button_add.setVisible(true);
                break;
        }
    }

    void set_visible_off_payment_method()
    {
        setting_payment_method_combobox.setVisible(true);
        setting_payment_method_label_name.setVisible(false);
        setting_payment_method_label_name_symbol.setVisible(false);
        setting_payment_method_textfield_name.setVisible(false);
        setting_payment_method_button_back.setVisible(false);
        setting_payment_method_button_add.setVisible(false);
        setting_payment_method_button_delete.setVisible(false);
        setting_payment_method_button_update.setVisible(false);
        setting_payment_method_listview.setVisible(false);
    }





    //--------------------------------------------------------------------------------------------- Connection
    @FXML
    Button setting_button_connection;
    @FXML
    TextField setting_connection_textfield_location;
    @FXML
    TextField setting_connection_textfield_port;
    @FXML
    TextField setting_connection_textfield_user;
    @FXML
    PasswordField setting_connection_textfield_password;
    @FXML
    TextField setting_connection_textfield_database;
    @FXML
    Label setting_connection_label_warning;

    @FXML
    void connection_button(){

        if(setting_connection_textfield_location.getText().equals("")) {

            setting_connection_label_warning.setText("Location should be filled");

        }
        else if(setting_connection_textfield_port.getText().equals("")) {

            setting_connection_label_warning.setText("Port should be filled");

        }
        else if(validate_numeric(setting_connection_textfield_port.getText()) == false) {

            setting_connection_label_warning.setText("Port should be numeric");

        }
        else if(Integer.parseInt(setting_connection_textfield_port.getText()) < 0 || Integer.parseInt(setting_connection_textfield_port.getText()) > 65535) {

            setting_connection_label_warning.setText("Port should be between 0 and 65535");

        }
        else if(setting_connection_textfield_user.getText().equals("")) {

            setting_connection_label_warning.setText("User should be filled");

        }
        else if(setting_connection_textfield_password.getText().equals("")) {

            setting_connection_label_warning.setText("Password should be filled");

        }
        else if(setting_connection_textfield_database.getText().equals("")) {

            setting_connection_label_warning.setText("Database should be filled");

        }
        else {

            String location = setting_connection_textfield_location.getText();
            int port = Integer.parseInt(setting_connection_textfield_port.getText());
            String user = setting_connection_textfield_user.getText();
            String password = setting_connection_textfield_password.getText();
            String database = setting_connection_textfield_database.getText();

            dbHelper = new DBHelper(location,port,user,password,database);
            dbHelper.startPool();

            try {
                dbHelper.test();
                dbHelper.initDatabaseTables();

                SettingsUtils settingsUtils = new SettingsUtils(location, port, user, password, database);
                try {
                    settingsUtils.updateSettingsFile(settingsUtils);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Stage stage = (Stage) setting_button_connection.getScene().getWindow();
                stage.close();
            } catch (SQLException e) {
                e.printStackTrace();
                setting_connection_label_warning.setText("Unable to connect to database");
            }
        }
    }

    void set_connection_textfields(){

        try {
            settingsUtils =  SettingsUtils.loadSettingsFile();

            setting_connection_textfield_database.setText(settingsUtils.getDb_name());
            setting_connection_textfield_password.setText(settingsUtils.getDb_password());
            setting_connection_textfield_port.setText(String.valueOf(settingsUtils.getDb_port()));
            setting_connection_textfield_location.setText(settingsUtils.getDb_location());
            setting_connection_textfield_user.setText(settingsUtils.getDb_user());
        } catch (IOException e) {
            e.printStackTrace();
            setting_connection_label_warning.setText("Unable to connect to database");
        }
    }

    private boolean validate_numeric(String digit) {

        Matcher matcher = Pattern.compile("^[0-9]+").matcher(digit);
        return matcher.find();

    }



    //--------------------------------------------------------------------------------------------- User
    @FXML
    Button setting_button_user;
    @FXML
    TextField setting_user_textfield_username;
    @FXML
    PasswordField setting_user_textfield_password;
    @FXML
    PasswordField setting_user_textfield_newpassword;
    @FXML
    PasswordField setting_user_textfield_confirmnewpassword;
    @FXML
    PasswordField setting_user_textfield_secretcode;
    @FXML
    Label setting_user_label_warning;

    @FXML
    void user_button(){

        setting_user_label_warning.setTextFill(Paint.valueOf("RED"));

        if(setting_user_textfield_username.getText().equals(""))
        {
            setting_user_label_warning.setText("Username should be filled");
        }
        else if(setting_user_textfield_password.getText().equals(""))
        {
            setting_user_label_warning.setText("Password should be filled");
        }
        else if(setting_user_textfield_newpassword.getText().equals(""))
        {
            setting_user_label_warning.setText("New password should be filled");
        }
        else if(setting_user_textfield_confirmnewpassword.getText().equals(""))
        {
            setting_user_label_warning.setText("Confirm new password should be filled");
        }
        else if(setting_user_textfield_secretcode.getText().equals(""))
        {
            setting_user_label_warning.setText("Secret code should be filled");
        }
        else {

            User user = userDatabaseUtils.getUserByNameAndPassword(setting_user_textfield_username.getText(), setting_user_textfield_password.getText());

            if (user == null)
            {
                setting_user_label_warning.setText("Username or password incorrect");
            }
            else if (!setting_user_textfield_newpassword.getText().equals(setting_user_textfield_confirmnewpassword.getText()))
            {
                setting_user_label_warning.setText("New password doesn't match");
            }
            else if (!setting_user_textfield_secretcode.getText().equals(user.getSecret_code()))
            {
                setting_user_label_warning.setText("Secret code doesn't match");
            }
            else {
                setting_user_label_warning.setTextFill(Paint.valueOf("BLACK"));
                setting_user_label_warning.setText("Success");

                User newuser = new User(
                        user.getId().intValue(),
                        setting_user_textfield_username.getText(),
                        setting_user_textfield_newpassword.getText(),
                        setting_user_textfield_secretcode.getText());

                userDatabaseUtils.updateUser(newuser);

                setting_user_textfield_username.setText("");
                setting_user_textfield_password.setText("");
                setting_user_textfield_newpassword.setText("");
                setting_user_textfield_confirmnewpassword.setText("");
                setting_user_textfield_secretcode.setText("");

            }
        }
    }



    //--------------------------------------------------------------------------------------------- Initialize

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        set_menu_category_combobox();
        set_setting_category_combobox();
        set_setting_menu_combobox();
        set_connection_textfields();
        set_setting_category_listview();
        set_setting_history_listview();
        set_setting_payment_method_combobox();
    }


}
