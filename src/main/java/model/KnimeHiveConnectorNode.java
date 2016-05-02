package model;

import DAO.DaoFactory;
import services.ServiceFactory;

import java.io.File;
import java.io.IOException;

/**
 * Created by cloudera on 5/1/16.
 */
public class KnimeHiveConnectorNode extends KnimeNode {

    private String database = null;
    private String password = null;
    private String driver = null;
    private String user = null;

    public KnimeHiveConnectorNode(File root) throws IOException {
        super(root);
        loadSettings();
    }

    private void loadSettings(){
        this.database = getDatabase();
        this.driver = getDriver();
        this.user = getUser();
        this.password = getPassword();
    }


    public void refreshSettings() {
        loadSettings();

        String appDriver = DaoFactory.getPropertiesDao().getProperty("HiveDriver");
        String appDefaultDatabase = DaoFactory.getPropertiesDao().getProperty("HiveDefaultDatabase");
        String appUser = DaoFactory.getPropertiesDao().getProperty("HiveDriver");
        String appPassword = DaoFactory.getPropertiesDao().getProperty("HivePassword");
        String appDatabase = DaoFactory.getPropertiesDao().getProperty("HiveServer") + "/" + appDefaultDatabase;

        if(!driver.equals(appDriver)){
            setDriver(appDriver);
            setChanged(true);
        }

        if(!database.equals(appDatabase)){
            setDatabase(appDatabase);
            setChanged(true);
        }

        if(!user.equals(appUser)){
            setUser(appUser);
            setChanged(true);
        }

        if(!password.equals(appPassword)){
            setPassword(appPassword);
            setChanged(true);
        }
    }


    private void setDatabase(String database) {
        ServiceFactory.getKnimeNodeService().setParameterValue(super.getXmlSettings(), "database", database);
    }

    private void setUser(String user) {
        ServiceFactory.getKnimeNodeService().setParameterValue(super.getXmlSettings(), "user", user);
    }

    private void setPassword(String password) {
        ServiceFactory.getKnimeNodeService().setParameterValue(super.getXmlSettings(), "password", password);
    }

    private void setDriver(String driver) {
        ServiceFactory.getKnimeNodeService().setParameterValue(super.getXmlSettings(), "driver", driver);
    }

    private String getDatabase() {
        return ServiceFactory.getKnimeNodeService().getParameterValue(super.getXmlSettings(), "database");
    }

    private String getPassword() {
        return ServiceFactory.getKnimeNodeService().getParameterValue(super.getXmlSettings(), "password");
    }

    private String getDriver() {
        return ServiceFactory.getKnimeNodeService().getParameterValue(super.getXmlSettings(), "driver");
    }

    private String getUser() {
        return ServiceFactory.getKnimeNodeService().getParameterValue(super.getXmlSettings(), "user");
    }
}
