package DAO;

import java.util.Properties;

/**
 * Created by cloudera on 3/28/16.
 */
public interface PropertiesDao {
    String get(String propertyName);

    void set(String propertyName, String propertyValue);

    Properties getAll();

    void read();

    void save();
}
