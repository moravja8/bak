package knimeEntities.knimeNodes;

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
        loadSettings(); //TODO mohlo by být nějak rozumně omezeno

        String appDriver = ServiceFactory.getPropertiesLoaderService().getProperty("HiveDriver");
        String appDefaultDatabase = ServiceFactory.getPropertiesLoaderService().getProperty("HiveDefaultDatabase");
        String appUser = ServiceFactory.getPropertiesLoaderService().getProperty("HiveDriver");
        String appPassword = ServiceFactory.getPropertiesLoaderService().getProperty("HivePassword");
        String appDatabase = ServiceFactory.getPropertiesLoaderService().getProperty("HiveServer") + "/" + appDefaultDatabase;

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


    public void setDatabase(String database) {
        ServiceFactory.getKnimeNodeService().setParameterValue(super.getXmlSettings(), "database", database);
    }

    public void setUser(String user) {
        ServiceFactory.getKnimeNodeService().setParameterValue(super.getXmlSettings(), "user", user);
    }

    public void setPassword(String password) {
        ServiceFactory.getKnimeNodeService().setParameterValue(super.getXmlSettings(), "password", password);
    }

    public void setDriver(String driver) {
        ServiceFactory.getKnimeNodeService().setParameterValue(super.getXmlSettings(), "driver", driver);
    }

    public String getDatabase() {
        return ServiceFactory.getKnimeNodeService().getParameterValue(super.getXmlSettings(), "database");
    }

    public String getPassword() {
        return ServiceFactory.getKnimeNodeService().getParameterValue(super.getXmlSettings(), "password");
    }

    public String getDriver() {
        return ServiceFactory.getKnimeNodeService().getParameterValue(super.getXmlSettings(), "driver");
    }

    public String getUser() {
        return ServiceFactory.getKnimeNodeService().getParameterValue(super.getXmlSettings(), "user");
    }
}
