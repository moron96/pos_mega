package model;

import java.util.Date;

/**
 * Created by ASUS on 18/04/2017.
 */
public class Order {
    private Integer id;
    private String customer_code;
    private Date date;
    private Integer status_id;
    private Integer payment_method_id;
    private Double promo_amount;
    private Date deleted_at;
    private String notes;

    public Order(Integer id, String customer_code, Date date, Integer status_id, Integer payment_method_id, Double promo_amount, Date deleted_at, String notes){
        setId(id);
        setCustomer_code(customer_code);
        setDate(date);
        setStatus_id(status_id);
        setPayment_method_id(payment_method_id);
        setPromo_amount(promo_amount);
        setDeleted_at(deleted_at);
        setNotes(notes);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCustomer_code() {
        return customer_code;
    }

    public void setCustomer_code(String customer_code) {
        this.customer_code = customer_code;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Integer getStatus_id() {
        return status_id;
    }

    public void setStatus_id(Integer status_id) {
        this.status_id = status_id;
    }

    public Integer getPayment_method_id() {
        return payment_method_id;
    }

    public void setPayment_method_id(Integer payment_method_id) {
        this.payment_method_id = payment_method_id;
    }

    public Double getPromo_amount() {
        return promo_amount;
    }

    public void setPromo_amount(Double promo_amount) {
        this.promo_amount = promo_amount;
    }

    public Date getDeleted_at() {
        return deleted_at;
    }

    public void setDeleted_at(Date deleted_at) {
        this.deleted_at = deleted_at;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
