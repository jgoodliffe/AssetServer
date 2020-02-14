package dbSystem;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;

import LoggingSystem.LoggingSystem;


/**
 * Handles Connection to SQLite Database
 * Only initialising and cleanup functions are held here.
 */
public class DataStore {
    private static Connection conn;
    private DatabaseMetaData meta;
    private LoggingSystem log;
    private SQLQueries sqlQueries;
    private String url;
    private String dbDir;

    private static DataStore instance = new DataStore();
    public static DataStore getInstance(){
        return instance;
    }

    /**
     * Constructor
     */
    public DataStore(){
        log = new LoggingSystem(this.getClass().getCanonicalName());
        init();
        this.sqlQueries = new SQLQueries(this, conn);
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

    public SQLQueries getSqlQueries(){
        return sqlQueries;
    }


    /**
     * createTableWithParameters
     * @param tableName - name of the table to be created
     * @param parameters - [column name, data type]
     * @return
     */
    private boolean createTableWithParameters(String tableName, String[][] parameters){
        if(tableName.equals("")|tableName==null){
            return false;
        }

        String sql = "";
        sql += "CREATE TABLE IF NOT EXISTS "+ tableName +"(";

        /*sql+"(\n"
                + "    id integer PRIMARY KEY,\n"
                + "    name text NOT NULL,\n"
                + "    quantity real\n"
                + ");";
         */

        for(int i=0; i<parameters.length;i++){
            for(int j=0; j<parameters[i].length; j++){
                sql+= parameters[i][j] + " ";
            }
            if(!(i ==parameters.length-1)){
                sql+= ", ";
            }
        }
        sql+=");";

        try{
            Statement stmt = conn.createStatement();
            //System.out.println(sql);
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
    //TODO Cleanup
    private boolean newDatabaseSetup(){
        log.infoMessage("Running new DB Setup: ");
        String logoutput = "\n\n";

        if(!newDBSetupSQLStatement()){
            logoutput+= "\nFailed to create new DB.";
            log.infoMessage(logoutput+"\n");
            return false;
        }
        logoutput+= "\nSuccessfully created DB.";
        log.infoMessage(logoutput+"\n");
        return true;
    }


    /**
     * SQL Statement creator - For initialising the columns of the Database Tables
     * @return
     */
    //TODO Events and Users tables need relational values adding
    private boolean newDBSetupSQLStatement(){
        //String statement="";
        String[] id = {"id", "INTEGER PRIMARY KEY AUTOINCREMENT"}; String[] uid = {"uid", "INTEGER"};
        String[] name = {"name", "TEXT"};
        String[] type = {"type", "TEXT"}; String[] category = {"category", "TEXT"};
        String[] quant = {"quantity", "INTEGER"};
        String[] ulevel = {"userlevel", "INTEGER NOT NULL"}; String[] username = {"username", "TEXT NOT NULL"}; String[] password = {"password", "TEXT NOT NULL"}; String[] authToken = {"authToken", "BLOB"};
        String[] startDate = {"startDate", "TEXT"}; String[] endDate = {"endDate", "TEXT"};
        String[] firstName = {"firstName", "TEXT"}; String[] lastName = {"lastName", "TEXT"};
        String[] firstLine = {"firstLine", "TEXT"}; String[] postCode = {"postCode", "TEXT"}; String[] tel = {"telephone", "TEXT"}; String[] email = {"email", "TEXT"};
        String[] company = {"company", "TEXT"}; String[] role = {"role", "TEXT"}; String[] notes = {"notes", "TEXT"};
        String[][] transport = {id,name,type,quant};
        String[][] users = {id, ulevel, username,password};
        String[][] events = {id, name, startDate, endDate};
        String[][] assets = {id, name, category, quant};
        String[][] people = {id, uid, firstName, lastName, firstLine, postCode, tel, email, company, role, notes};
        String[][][] tables = {users, events, assets, people, transport};
        String[] tableNames = {"users", "events", "assets", "people", "transport"};

        for(int i=0; i<tables.length; i++){
            if(!createTableWithParameters(tableNames[i], tables[i])){
                return false;
            }
        }

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
                {"users", "events", "assets", "people", "transport"},
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
     * @param tableName - name of table\
     */
    //TODO: Add schema check.
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

    public void newConnection(){
        try{
            conn = DriverManager.getConnection(url);
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    /**
     * Initialses the database connection & creates db if it doesnt already exist
     */
    public void init() {
        dbDir = dbDirectoryCreator();
        if(dbDir.equals("")){
            log.errorMessage("Invalid database directory! Aborting.");
            return;
        }
        try {
           // String dbFolder = Paths.get(dbDir).toString();
            //Modify dbdir to output relative path...
            url = "jdbc:sqlite:"+dbDir;
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
