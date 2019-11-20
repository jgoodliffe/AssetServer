package dbSystem;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import LoggingSystem.LoggingSystem;

/**
 * Connection to SQLite Database
 */
public class DataStore {
    private static Connection conn;
    private DatabaseMetaData meta;
    private LoggingSystem log;

    /**
     * Constructor
     */
    public DataStore(){
        log = new LoggingSystem(this.getClass().getCanonicalName());
        init();
    }

    /**
     * getConn
     * @return - the current db Connection
     */
    public Connection getConn(){
        return conn;
    }

    /**
     * Checks if connection is alive
     * @return - true or false
     */
    public static boolean getConnStatus(Connection connection){
        try {
            return connection == null||connection.isValid(3);
        } catch (SQLException e) {
            return false;
        }
    }

    private boolean createTable(String tableName){
        // SQL statement for creating a new table
        if(tableName.equals("")|tableName==null){
            return false;
        }
        String sql = "CREATE TABLE IF NOT EXISTS "+ tableName +"(\n"
                + "    id integer PRIMARY KEY,\n"
                + "    name text NOT NULL,\n"
                + "    quantity real\n"
                + ");";
        try{
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            return true;
        } catch (SQLException e){
            log.errorMessage("Error creating new table.");
            log.errorMessage(e.getMessage());
            return false;
        }
    }

    /**
     * Setup new database.
     */
    private boolean newDatabaseSetup(){
        log.infoMessage("Running new DB Setup: ");
        String[] tables = {"Users", "Events", "Assets", "Staff", "Transport"};
        String logoutput = "\n\n";
        for(int i=0; i<tables.length; i++){
            if(!tableExists(tables[i])){
                logoutput+="\n Creating table "+tables[i];
                if(createTable(tables[i])){
                    logoutput+="\n Created table "+tables[i];
                } else{
                    logoutput+="\n Unable to create table "+tables[i];
                    return false;
                }
            } else{
                logoutput+="\n "+tables[i] +" exists. Not creating.";
            }
        }
        logoutput+= "\nSuccessfully created DB.";
        log.infoMessage(logoutput+"\n");
        return true;
    }

    /**
     * Checks the consistency of the database, reports to Log and offers to create new tables and init DB if not correct.
     * @return - true/false (whether db is consistent)
     */
    private boolean dbConsistencyCheck(){
        log.infoMessage("Running consistency check:");
        String logoutput = "\n\nConsistency Report";
        String[][] statuses = {
                {"Users", "Events", "Assets", "Staff", "Transport"},
                {"false", "false", "false", "false", "false"}
        };

        for(int i=0; i<statuses[0].length; i++){
            if(tableExists(statuses[0][i])){
                statuses[1][i] = "true";
                logoutput+= "\n - "+statuses[0][i] + " table exists";
            } else{
                statuses[1][i] = "false";
                logoutput+= "\n - "+statuses[0][i] + " table does not exist";
            }
        }
        logoutput+="\n";
        log.infoMessage(logoutput);

        for(int i=0; i<statuses[0].length; i++){
            if(statuses[1][i].equals("false")){
                return false;
            }
        } return true;
    }

    /**
     * Checks if table exists
     * @param tableName - name of table
     */
    private boolean tableExists(String tableName){
        if(getConnStatus(conn)){
            try{
                ResultSet rs = meta.getTables(null, null, tableName, null);
                //rs.last();
                return rs.next();
            } catch (SQLException e){
                //log.errorMessage("tableExists "+e.getMessage());
            }
            return false;
        }
        return false;
    }

    /**
     * Initialses the database connection & creates db if it doesnt already exist
     */
    public void init() {
        String dbDir = dbDirectoryCreator();
        if(dbDir.equals("")){
            log.errorMessage("Invalid database directory! Aborting.");
            return;
        }
        try {
           // String dbFolder = Paths.get(dbDir).toString();
            //Modify dbdir to output relative path...
            String url = "jdbc:sqlite:"+dbDir;
            conn = DriverManager.getConnection(url);


            //Get DB Metadata
            if(conn!=null){
                meta = conn.getMetaData();
            }

            //Run a consistency check and init DB if not consistent.
            if(dbConsistencyCheck()){
                log.infoMessage("Passed DB Consistency check.");
            } else{
                log.infoMessage("Did not pass Consistency Check.");
                newDatabaseSetup();
            }

            log.infoMessage("Connected to SQLite Database.");

        } catch (SQLException e) {
            log.errorMessage("SQL Exception");
            log.errorMessage(e.getMessage());
        }
    }

    /**
     * Closes database connection.
     */
    private void close(){
        try {
            if (conn != null) {
                conn.close();
            } else {
            }
        } catch (SQLException e) {
            log.errorMessage("SQL Exception: "+e.getMessage());
        }
    }


    /**
     * dbDirectoryCreator
     * Checks if the main db directory exists and if not creates it...
     * @return - the directory string
     */
    private String dbDirectoryCreator(){
        String currentDir = System.getProperty("user.dir");
        String dataStoreDir = currentDir + "/Data";
        Path path = Paths.get(dataStoreDir);

        //Check if a datastore directory exists..
        if(Files.exists(path)){
            //Datastore directory exists, check for db....
            log.infoMessage("Found datastore Directory.");
            String relative = getRelativePath(dataStoreDir);
            return relative + "mainDB.db";
        } else{
            //Create new datastore directory..
            Boolean newdir = new File(currentDir+"/Data").mkdirs();
            if(newdir){
                log.infoMessage("Created new directory - Data.");
                currentDir = dataStoreDir;
                return getRelativePath(dataStoreDir);
            } else{
                log.errorMessage("Unable to create new directory - could already exist?");
                return "";
            }
        }
    }

    /**
     * Returns relative path of working directory
     * @param dbPath full path
     * @return the relative path
     */
    private String getRelativePath(String dbPath){
        String relPath = "";
        String main[] = dbPath.split("/");
        relPath = main[main.length-1]+"/";
        //log.infoMessage(relPath);
        return relPath;
    }

    private void resetDatabase(){
        String currentDir = System.getProperty("user.dir");
        String dataStoreDir = currentDir + "/Data";
        Path path = Paths.get(dataStoreDir);
        if(!Files.exists(path)){
            //Database does not exist
        } else{
            //Database exists
        }
    }
}
