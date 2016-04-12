package dbConnectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import services.ServiceFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by cloudera on 4/11/16.
 */
public class H2Connector extends DbConnector{
    private static H2Connector instance;
    private Logger log = LoggerFactory.getLogger(H2Connector.class);

    private H2Connector() {
        super.driverName = "org.h2.Driver";
        super.currentDatabase = "bp;AUTO_SERVER=TRUE";
        super.dbUser = "sa";
        super.dbPassword = "";
        super.dbServer = "jdbc:h2:" + ServiceFactory.getPropertiesLoaderService().getProperty("H2DestinationFolder");

        connect();
    }

    public static DbConnector getInstance() {
        if(instance == null){
            instance = new H2Connector();
        }

        return instance;
    }

    public ResultSet getDatabases() {
        throw new NotImplementedException();
    }

    public ResultSet getTables() {
        throw new NotImplementedException();
    }

    public String getBasicSelectSQL(String tableName) {
        return "select * from " + tableName + ";";
    }

    protected void setDb(String dbName) {
        throw new NotImplementedException();
    }
}
