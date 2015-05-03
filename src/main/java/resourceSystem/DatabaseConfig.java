package resourceSystem;

import base.Resource;

public class DatabaseConfig implements Resource {
    private String dialect;
    private String driverClass;
    private String connectionURL;
    private String username;
    private String password;
    private String showSql;
    private String hbm2ddl;

    public DatabaseConfig() {
    }

    public DatabaseConfig(String dialect, String driverClass, String connectionURL, String username,
                          String password, String showSql, String hbm2ddl) {
        this.dialect = dialect;
        this.driverClass = driverClass;
        this.connectionURL = connectionURL;
        this.username = username;
        this.password = password;
        this.showSql = showSql;
        this.hbm2ddl = hbm2ddl;
    }

    public String getDialect() {
        return dialect;
    }

    public String getDriverClass() {
        return driverClass;
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
