package dbConnectors;

import org.apache.hive.jdbc.HiveDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ServiceFactory;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Singleton, not thread safe
 * Created by cloudera on 4/10/16.
 */
public class HiveConnector extends DbConnector{

    private static DbConnector instance;

    private Logger log = LoggerFactory.getLogger(HiveConnector.class);

    private HiveConnector() {
        super.driverName = ServiceFactory.getPropertiesLoaderService().getProperty("HiveDriver");
        super.currentDatabase = ServiceFactory.getPropertiesLoaderService().getProperty("HiveDefaultDatabase");
        super.dbUser = ServiceFactory.getPropertiesLoaderService().getProperty("HiveUser");
        super.dbPassword = ServiceFactory.getPropertiesLoaderService().getProperty("HivePassword");
        super.dbServer = ServiceFactory.getPropertiesLoaderService().getProperty("HiveServer");

        connect();
    }

    public static DbConnector getInstance() {
        if(instance == null){
            instance = new HiveConnector();
        }

        return instance;
    }

    public ResultSet getDatabases() {
        return callQuery("show databases", true);
    }

    public ResultSet getTables(){
        return callQuery("show tables in " + super.getCurrentDatabase(), true);
    }

    /**
     * This method returns only SQL, not result of a SQL command! Returns select * from table, table name is fully qualified.
     * @param tableName - name of queried table
     * @return basic query for retrieving data from table
     */
    public String getBasicSelectSQL(String tableName) {
        return "SELECT * FROM " + super.getCurrentDatabase() + "." +tableName+";";
    }

    protected void setDb(String dbName) {
        callQuery("use "+dbName, false);
    }
}
