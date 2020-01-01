package model;

/**
 * Created by ASUS on 18/04/2017.
 */
public class Order_Detail {
    private Integer id;
    private Integer order_id;
    private Integer menu_id;
    private Integer qty;
    private Double price;
    private String notes;
    private Integer food_status_id;

    public Order_Detail(Integer id, Integer order_id, Integer menu_id, Integer qty, Double price, String notes, Integer food_status_id){
        setId(id);
        setOrder_id(order_id);
        setMenu_id(menu_id);
        setQty(qty);
        setPrice(price);
        setNotes(notes);
        setFood_status_id(food_status_id);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public Integer getMenu_id() {
        return menu_id;
    }

    public void setMenu_id(Integer menu_id) {
        this.menu_id = menu_id;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Integer getFood_status_id() {
        return food_status_id;
    }

    public void setFood_status_id(Integer food_status_id) {
        this.food_status_id = food_status_id;
    }
}
