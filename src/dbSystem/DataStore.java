package dbSystem;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import LoggingSystem.LoggingSystem;
import org.apache.commons.lang3.StringUtils;

/**
 * Connection to SQLite Database
 */
public class DataStore {
    private Connection conn;
    private LoggingSystem log;

    public DataStore(){
        log = new LoggingSystem(this.getClass().getCanonicalName());
    }

    public Connection getConn(){
        return conn;
    }

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
                DatabaseMetaData meta = conn.getMetaData();
            }

            log.infoMessage("Connected to SQLite Database.");

        } catch (SQLException e) {
            log.errorMessage("SQL Exception");
            log.errorMessage(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                } else {
                }
            } catch (SQLException e) {
                log.errorMessage("SQL Exception: "+e.getMessage());
            }
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

    private String getRelativePath(String dbpath){
        String relPath = "";
        String main[] = dbpath.split("/");
        relPath = main[main.length-1]+"/";
        //log.infoMessage(relPath);
        return relPath;
    }
}
