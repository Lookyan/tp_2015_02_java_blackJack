package base;


import base.dataSets.UserDataSet;

import java.util.List;

public interface DBService {

    void saveUser(UserDataSet dataSet);

//    void readByName(String userName);

    int getChipsByName(String userName);

    UserDataSet getUserData(String userName);

    UserDataSet getUserDataByEmail(String userName);

    List<UserDataSet> getTop();

    void addChipsByName(String userName, int amount);

    void subChipsByName(String userName, int amount);

    long countAllUsers();

    void shutdown();
}
