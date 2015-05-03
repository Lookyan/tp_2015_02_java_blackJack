package dbService.dao;


import base.dataSets.UserDataSet;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.sql.SQLException;

public class UserDataSetDAO {
    private Session session;

    public UserDataSetDAO(Session session) {
        this.session = session;
    }

    public void save(UserDataSet dataSet) throws SQLException {
        session.save(dataSet);
    }

    public void update(UserDataSet dataSet) throws SQLException {
        session.update(dataSet);
    }

    public UserDataSet readByName(String name) throws SQLException {
        Criteria criteria = session.createCriteria(UserDataSet.class);
        return (UserDataSet) criteria.add(Restrictions.eq("name", name)).uniqueResult();
    }

}
