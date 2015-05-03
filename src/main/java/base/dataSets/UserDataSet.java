package base.dataSets;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "User")
public class UserDataSet implements Serializable {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "password")
    private String password;

    @Column(name = "email")
    private String email;

    @Column(name = "chips")
    private int chips;

    public UserDataSet() {
    }

    public UserDataSet(String name, String password, String email, int chips) {
        this.setId(-1);
        this.setName(name);
        this.setPassword(password);
        this.setEmail(email);
        this.setChips(chips);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getChips() {
        return chips;
    }

    public void setChips(int chips) {
        this.chips = chips;
    }
}
