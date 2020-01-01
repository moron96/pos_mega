package model;

/**
 * Created by ASUS on 18/04/2017.
 */
public class User {
    private Integer id;
    private String name;
    private String password;
    private String secret_code;

    public User(Integer id, String name, String password, String secret_code){
        setId(id);
        setName(name);
        setPassword(password);
        setSecret_code(secret_code);
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSecret_code() {
        return secret_code;
    }

    public void setSecret_code(String secret_code) {
        this.secret_code = secret_code;
    }
}
