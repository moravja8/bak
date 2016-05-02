package DAO;

import DAO.Impl.KnimeNodeDaoImpl;
import DAO.Impl.PropertiesDaoImpl;

/**
 * Created by cloudera on 5/2/16.
 */
public class DaoFactory {
    private static PropertiesDao propertiesDao = null;
    private static KnimeNodeDao knimeNodeDao = null;

    public static PropertiesDao getPropertiesDao() {
        if(propertiesDao == null){
            propertiesDao = new PropertiesDaoImpl();
        }
        return propertiesDao;
    }

    public static KnimeNodeDao getKnimeNodeDao() {
        if(knimeNodeDao == null){
            knimeNodeDao = new KnimeNodeDaoImpl();
        }
        return knimeNodeDao;
    }
}
