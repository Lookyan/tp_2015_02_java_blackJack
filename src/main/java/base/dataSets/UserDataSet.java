package base.dataSets;


import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "user")
public class UserDataSet implements Serializable {

    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;

    @Column(name = "pass")
    private String pass;

    @Column(name = "email")
    private String email;

    @Column(name = "score")
    private long score;

    public UserDataSet() {
    }

    public UserDataSet(long id, String name, String pass, String email, long score) {
        this.setId(id);
        this.setName(name);
        this.setPass(pass);
        this.setEmail(email);
        this.setScore(score);
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

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getScore() {
        return score;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
