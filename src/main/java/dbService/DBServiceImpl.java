package dbService;


import base.DBService;
import base.dataSets.UserDataSet;
import dbService.dao.UserDataSetDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;


public class DBServiceImpl implements DBService {
    private SessionFactory sessionFactory;

    public DBServiceImpl() {
        Configuration configuration = new Configuration();
        configuration.addAnnotatedClass(UserDataSet.class);

        configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        configuration.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        configuration.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/jdb");
        configuration.setProperty("hibernate.connection.username", "root");
        configuration.setProperty("hibernate.connection.password", "123456");
        configuration.setProperty("hibernate.show_sql", "true");
        configuration.setProperty("hibernate.hbm2ddl.auto", "create");

        sessionFactory = createSessionFactory(configuration);
    }

    private static SessionFactory createSessionFactory(Configuration configuration) {
        StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder();
        builder.applySettings(configuration.getProperties());
        ServiceRegistry serviceRegistry = builder.build();
        return configuration.buildSessionFactory(serviceRegistry);
    }

    public void saveUser(UserDataSet dataSet) {
        Session session = sessionFactory.openSession();
        Transaction transaction = session.beginTransaction();
        UserDataSetDAO userDataSetDAO = new UserDataSetDAO(session);
        userDataSetDAO.save(dataSet);
        transaction.commit();
    }
}
