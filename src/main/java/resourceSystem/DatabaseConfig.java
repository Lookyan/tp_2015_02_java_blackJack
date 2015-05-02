package resourceSystem;

import base.Resource;

public class DatabaseConfig implements Resource {
    private String connectionURL;
    private String username;
    private String password;
    private String showSql;
    private String hbm2ddl;

    public DatabaseConfig() {
        connectionURL = "";
        username = "";
        password = "";
        showSql = "";
        hbm2ddl = "";
    }

    public String getConnectionURL() {
        return connectionURL;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getShowSql() {
        return showSql;
    }

    public String getHbm2ddl() {
        return hbm2ddl;
    }
}
