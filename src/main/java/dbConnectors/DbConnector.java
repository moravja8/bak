package dbConnectors;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.sql.*;

/**
 * Created by cloudera on 4/10/16.
 */
public abstract class DbConnector {
    private Logger log = LoggerFactory.getLogger(DbConnector.class);
    String dbServer = null;
    String currentDatabase = null;
    String driverName = null;
    private Statement statement = null;
    String dbUser = null;
    String dbPassword = null;

    public abstract ResultSet getDatabases();
    public abstract ResultSet getTables();
    public abstract String getBasicSelectSQL(String tableName);
    protected abstract void setDb(String dbName);

    ResultSet callQuery(String sql, boolean expectResult) {
        log.info("Running query '"+sql+"'");
        try {
            if(expectResult){
                ResultSet resultSet = statement.executeQuery(sql);
                log.info("Fetched result has " + resultSet.getMetaData().getColumnCount() + " columns and " );
                return resultSet;
            }else{
                try {
                    statement.executeQuery(sql);
                } catch (SQLException e) {
                    log.error("Called query that does not expects a result set.");
                }
                return null;
            }
        }catch (NullPointerException e){
            log.error("Connection to db is not working.", e);
            return  null;
        }catch (SQLException e) {
            log.error("SQL query failed: " + sql);
            return null;
        }
    }

    public void execute(String sql) {
        log.info("Executing sql command '"+sql+"'");
        try {
            statement.execute(sql);
        } catch (SQLException e) {
            log.error("SQL query failed: " + sql, e);
        }
    }


    public String getCurrentDatabase() {
        return currentDatabase;
    }

    public void setCurrentDatabase(String currentDatabase) {
        this.currentDatabase = currentDatabase;
        setDb(currentDatabase);
    }

    void connect() {
        String conn = dbServer + File.separator + getCurrentDatabase();
        try {
            Class.forName(driverName);
        }catch (NullPointerException e){
            log.error("DB driver is not set up properly", e);
        }catch (ClassNotFoundException e) {
            log.error("Could not find driver class " + driverName, e);
        }
        try {
            Connection connection = DriverManager.getConnection(conn, dbUser, dbPassword);
            statement = connection.createStatement();
        } catch (SQLException e) {
            log.error("Could not create connection to database server " + conn, e);
        }
    }
}
