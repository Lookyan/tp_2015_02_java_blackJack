package dbService;

import base.DBService;
import base.dataSets.UserDataSet;
import dbService.dao.UserDataSetDAO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;
import resourceSystem.DatabaseConfig;

import java.sql.SQLException;

public class DBServiceImpl implements DBService {

    private static final Logger logger = LogManager.getLogger();

    private SessionFactory sessionFactory;

    public DBServiceImpl(DatabaseConfig config) {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UserDataSet.class);

//        DatabaseConfig config = (DatabaseConfig) ResourceFactory.getInstance().get("data/database_config.xml");

        logger.info("Configuring database...");

        configuration.setProperty("hibernate.dialect", config.getDialect());
        configuration.setProperty("hibernate.connection.driver_class", config.getDriverClass());
        configuration.setProperty("hibernate.connection.url", config.getConnectionURL());
        configuration.setProperty("hibernate.connection.username", config.getUsername());
        configuration.setProperty("hibernate.connection.password", config.getPassword());
        configuration.setProperty("hibernate.show_sql", config.getShowSql());
        configuration.setProperty("hibernate.hbm2ddl.auto", config.getHbm2ddl());

        sessionFactory = createSessionFactory(configuration);
        logger.info("Database service initialized");
    }

    @Override
    public void saveUser(UserDataSet dataSet) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        UserDataSetDAO userDataSetDAO = new UserDataSetDAO(session);
        try {
            userDataSetDAO.save(dataSet);
        } catch (SQLException e) {
            logger.error(e);
        }
        logger.info("Saved new user '{}'", dataSet.getName());
        transaction.commit();
    }

    @Override
    public int getChipsByName(String userName) {
        Session session = sessionFactory.openSession();
        UserDataSetDAO userDataSetDAO = new UserDataSetDAO(session);
        logger.info("Getting chips of '{}'", userName);
        try {
            return userDataSetDAO.readByName(userName).getChips();
        } catch (SQLException e) {
            logger.error(e);
        }
        return 0;
    }

    @Override
    public UserDataSet getUserData(String userName) {
        Session session = sessionFactory.openSession();
        UserDataSetDAO dao = new UserDataSetDAO(session);
        logger.info("Getting user data of '{}'", userName);
        try {
            return dao.readByName(userName);
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }

    @Override
    public UserDataSet getUserDataByEmail(String email) {
        Session session = sessionFactory.openSession();
        UserDataSetDAO dao = new UserDataSetDAO(session);
        logger.info("Getting user data of '{}'", email);
        try {
            return dao.readByEmail(email);
        } catch (SQLException e) {
            logger.error(e);
        }
        return null;
    }

    @Override
    public void addChipsByName(String userName, int amount) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        UserDataSetDAO userDataSetDAO = new UserDataSetDAO(session);
        logger.info("Adding {} chips to '{}'", amount, userName);
        try {
            UserDataSet dataSet = userDataSetDAO.readByName(userName);
            dataSet.setChips(dataSet.getChips() + amount);
            userDataSetDAO.update(dataSet);
        } catch (SQLException e) {
            logger.error(e);
        }
        transaction.commit();
    }

    @Override
    public void subChipsByName(String userName, int amount) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        UserDataSetDAO userDataSetDAO = new UserDataSetDAO(session);
        logger.info("Subtracting {} chips from '{}'", amount, userName);
        try {
            UserDataSet dataSet = userDataSetDAO.readByName(userName);
            dataSet.setChips(dataSet.getChips() - amount);
            if (dataSet.getChips() == 0) {
                dataSet.setChips(1000);
            }
            userDataSetDAO.update(dataSet);
        } catch (SQLException e) {
            logger.error(e);
        }
        transaction.commit();
    }

    @Override
    public long countAllUsers() {
        Session session = sessionFactory.openSession();
        try {
            return new UserDataSetDAO(session).countAll();
        } catch (SQLException e) {
            logger.error(e);
        }
        return -1;
    }

    public void shutdown(){
        sessionFactory.close();
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }
}
