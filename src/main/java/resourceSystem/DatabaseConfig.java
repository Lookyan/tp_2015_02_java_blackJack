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
