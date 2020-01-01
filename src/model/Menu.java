package model;

public class Menu {
    private Integer id;
    private String name;
    private Integer category_id;
    private Double price;
    private String image_url;

    public Menu(Integer id, String name, Integer category_id, Double price, String image_url){
        super();
        setId(id);
        setName(name);
        setCategory_id(category_id);
        setPrice(price);
        setImage_url(image_url);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCategory_id() {
        return category_id;
    }

    public void setCategory_id(Integer category_id) {
        this.category_id = category_id;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }
}