package model;

/**
 * Created by ASUS on 26/04/2017.
 */
public class Model_Table {
    private Integer id;
    private Integer no;
    private String name;
    private Double price;
    private Integer qty;
    private Double sub_total;

    public Model_Table(Integer id,Integer no, String name, Double price, Integer qty, Double sub_total) {
        setId(id);
        setNo(no);
        setName(name);
        setPrice(price);
        setQty(qty);
        setSub_total(sub_total);
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNo() {
        return no;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Double getSub_total() {
        return price * qty;
    }

    public void setSub_total(Double sub_total) {
        this.sub_total = sub_total;
    }
}
