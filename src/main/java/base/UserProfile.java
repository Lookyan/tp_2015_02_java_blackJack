package base;

public class UserProfile {
    private String name;
    private String password;
    private String email;

    private int chips;
//    private int score;

    public UserProfile(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
        this.chips = 1000;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public int getChips() {
        return chips;
    }
}
