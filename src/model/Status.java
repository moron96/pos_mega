package model;

/**
 * Created by ASUS on 18/04/2017.
 */
public class Status {
    private Integer id;
    private String slug;
    private String name;

    public Status(Integer id, String slug, String name){
        setId(id);
        setSlug(slug);
        setName(name);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
