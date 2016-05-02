package dbConnectors;

import DAO.DaoFactory;
import java.sql.ResultSet;

/**
 * Singleton, not thread safe
 * Created by cloudera on 4/10/16.
 */
public class HiveConnector extends DbConnector{

    private static DbConnector instance;

    private HiveConnector() {
        super.driverName = DaoFactory.getPropertiesDao().getProperty("HiveDriver");
        super.currentDatabase = DaoFactory.getPropertiesDao().getProperty("HiveDefaultDatabase");
        super.dbUser = DaoFactory.getPropertiesDao().getProperty("HiveUser");
        super.dbPassword = DaoFactory.getPropertiesDao().getProperty("HivePassword");
        super.dbServer = DaoFactory.getPropertiesDao().getProperty("HiveServer");

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
