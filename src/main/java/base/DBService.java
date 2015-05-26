package base;


import base.dataSets.UserDataSet;

public interface DBService {

    void saveUser(UserDataSet dataSet);

//    void readByName(String userName);

    int getChipsByName(String userName);

    UserDataSet getUserData(String userName);

    UserDataSet getUserDataByEmail(String userName);

    void addChipsByName(String userName, int amount);

    void subChipsByName(String userName, int amount);

    long countAllUsers();

    void shutdown();
}
