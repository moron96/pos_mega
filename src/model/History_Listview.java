package model;

import javafx.collections.ObservableList;

/**
 * Created by Elbert Utama on 02/05/2017.
 */
public class History_Listview {

    private ObservableList<Order> data ;

    public void setData(ObservableList<Order> data) {
        this.data = data ;
    }

    public ObservableList<Order> getData() {
        return data;
    }

}
