package dbConnectors;

import DAO.DaoFactory;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.sql.ResultSet;

/**
 * Created by cloudera on 4/11/16.
 */
public class H2Connector extends DbConnector{
    private static H2Connector instance;

    private H2Connector() {
        super.driverName = "org.h2.Driver";
        super.currentDatabase = "bp;AUTO_SERVER=TRUE";
        super.dbUser = "sa";
        super.dbPassword = "";
        super.dbServer = "jdbc:h2:" + DaoFactory.getPropertiesDao().get("H2DestinationFolder");

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
