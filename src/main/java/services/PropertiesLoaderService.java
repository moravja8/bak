package services;

import knimeEntities.KnimeWorkflowManager;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by cloudera on 3/28/16.
 */
public class PropertiesLoaderService {

    protected  PropertiesLoaderService() {
    }

    private Properties prop = null;

    public void loadProperties(){

        InputStream input = null;
        prop = new Properties();

        try {
            input = new FileInputStream(getClass().getResource("/config.properties").getPath());
            prop.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public String getProperty(String propertyName){
        if (prop == null){
            loadProperties();
        }
        return prop.getProperty(propertyName);
    }
}
