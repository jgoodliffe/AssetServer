package LoggingSystem;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Logging Subsystem
 * Logs all system output into text file.
 */
public class LoggingSystem {
    private Date today;
    private String logName;
    private final Logger logger = Logger.getLogger("myLog");
    private FileHandler fh;

    public LoggingSystem(String tag){
        today = Calendar.getInstance().getTime();
        logName = tag+today.toString();
        try{
            fh = new FileHandler("%h/Logs/logfile%u.txt");
            logger.addHandler(fh);
            SimpleFormatter sf = new SimpleFormatter();
            fh.setFormatter(sf);

            logger.info("Log Commenced");
        } catch (SecurityException | IOException e){
            e.printStackTrace();
        }
    }

    public void errorMessage(String message){
        if(logger!=null){
            logger.severe(message);
        }
    }
    public void infoMessage(String message){
        if(logger!=null){
            logger.info(message);
        }
    }
}
