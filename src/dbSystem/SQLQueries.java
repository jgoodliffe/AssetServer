package dbSystem;

import authentication.PasswordHasher;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * SQLQueries
 * Holds all general SQL Queries
 */
public class SQLQueries {
    private static DataStore ds;
    private static Connection conn;
    private static JSONConverter conv;
    private static PasswordHasher passwordHasher;

    public SQLQueries(DataStore ds, Connection conn){
        SQLQueries.ds = ds;
        SQLQueries.conn = conn;
        conv = new JSONConverter();
        passwordHasher = new PasswordHasher();
    }


    /**
     * Edits the User table - Prepared Statement
     * @param newUser - is this a new user?
     * @param id - user id (if there is one)
     * @param uid - username (optional, to link to users table)
     * @param firstName - Name
     * @param firstLine - addr
     * @param postCode - postcode
     * @param email - email
     * @param phone - phone
     * @param company - employer
     * @param role - role at employer
     * @param notes - notes
     */
    //TODO Have a look at this.
    public void editPeopleTable(boolean newUser, int id, int uid, String firstName, String lastName, String firstLine, String postCode, String email, String phone, String company, String role, String notes){
        //Check if a new user or not
        if(newUser){
            String sql = "INSERT INTO people VALUES "+uid+", "+ firstName+", "+ lastName+", "+ firstLine+", "+ postCode+", "+ phone+", "+ email+", "+ company+", "+ role+", "+ notes+";";
            try{
                PreparedStatement psmt = conn.prepareStatement(sql);
                psmt.executeQuery();

            } catch (SQLException e){
                e.printStackTrace();
            }
        } else{
            //TODO: Perhaps try to check if the user exists first?
            String sql = "UPDATE people SET uid="+uid+", firstName="+firstName+", lastName="+lastName+", firstLine = "+firstLine+", postCode ="+postCode+", tel="+phone+", email="+email+", company="+company+", role="+role+", notes="+notes+ " WHERE id="+id+";";
            try{
                PreparedStatement psmt = conn.prepareStatement(sql);
                psmt.executeQuery();

            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }


    /**
     * editAssetsTable
     * @param newAsset
     * @param name
     * @param type
     * @param quant
     * @param id
     */
    public void editAssetsTable(boolean newAsset, String name, String type, int quant, int id){
        if(newAsset){
            String sql = "INSERT INTO assets VALUES "+ name +", "+type+", "+quant+";";
            try{
                PreparedStatement psmt = conn.prepareStatement(sql);
                psmt.executeQuery();
            } catch (SQLException e){
                e.printStackTrace();
            }
        } else{
            String sql = "UPDATE assets VALUES "+ name +", "+type+", "+quant+" WHERE id="+id+";";
            try{
                PreparedStatement psmt = conn.prepareStatement(sql);
                psmt.executeQuery();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }


    /**
     * editTransportTable
     * @param newAsset
     * @param name
     * @param type
     * @param quant
     * @param id
     */
    public void editTransportTable(boolean newAsset, String name, String type, int quant, int id){
        if(newAsset){
            String sql = "INSERT INTO transport VALUES "+ name +", "+type+", "+quant+";";
            try{
                PreparedStatement psmt = conn.prepareStatement(sql);
                psmt.executeQuery();
            } catch (SQLException e){
                e.printStackTrace();
            }
        } else{
            String sql = "UPDATE transport VALUES "+ name +", "+type+", "+quant+" WHERE id="+id+";";
            try{
                PreparedStatement psmt = conn.prepareStatement(sql);
                psmt.executeQuery();
            } catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * editEventTable
     * @param newEvent - is this a new event
     * @param name - event name
     * @param startDate - starting date and time of event
     * @param endDate - end date and time of event
     * @param id - event id if exists.
     * TODO: Add Relational parameters
     */
    public void editEventTable(boolean newEvent, String name, Date startDate, Date endDate, int id){
        String sql="";
        if(newEvent){
            sql+="INSERT INTO events VALUES "+ name+", "+startDate.toString()+", "+endDate.toString()+";";
        } else{
            sql += "UPDATE events VALUES " + name + ", " + startDate.toString() + ", " + endDate.toString() + " WHERE id=" + id + ";";
        }
        try {
            PreparedStatement psmt = conn.prepareStatement(sql);
            psmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (conn != null) {
                try {
                    conn.close(); // <-- This is important
                    DataStore.getInstance().newConnection();
                    conn = DataStore.getInstance().getConn();
                } catch (SQLException e) {
                    /* handle exception */
                }
            }
        }
    }

    public void addUser(String username, String password, int uLevel) {
        String sql = "INSERT INTO users VALUES ?,?,?,?;";

        //Generate Salt
        byte[] salt = new byte[16];
        salt = PasswordHasher.getSalt();

        //Hash password
        password = passwordHasher.hashPassword(password, salt);

        PreparedStatement psmt = null;
        try {
            psmt = conn.prepareStatement(sql);
            psmt.setInt(1, uLevel);
            psmt.setString(2, username);
            psmt.setString(3, password);
            psmt.setBytes(4, salt);
            psmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (conn != null) {
                try {
                    conn.close(); // <-- This is important
                    DataStore.getInstance().newConnection();
                    conn = DataStore.getInstance().getConn();
                } catch (SQLException e) {
                    /* handle exception */
                }
            }
        }
    }

    public boolean changePassword(int id, String password) {
        String sql = "UPDATE users SET password=?, password_salt=? WHERE userID=?;";

        //Generate Salt
        byte[] salt = PasswordHasher.getSalt();

        //Hash new Password:
        password = passwordHasher.hashPassword(password, salt);

        try {
            conn.close();
            DataStore.getInstance().newConnection();
            conn = DataStore.getInstance().getConn();
            PreparedStatement psmt = conn.prepareStatement(sql);
            psmt.setInt(3, id);
            psmt.setBytes(2, salt);
            psmt.setString(1, password);
            psmt.executeUpdate();
            return true;
            //System.out.println("New Password set to: "+password);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if (conn != null) {
                try {
                    conn.close(); // <-- This is important
                    DataStore.getInstance().newConnection();
                    conn = DataStore.getInstance().getConn();
                } catch (SQLException e) {
                    /* handle exception */
                }
            }
        }
    }

    public void changeUName(String username, String newUsername){
        String sql = "UPDATE users SET username="+newUsername+" WHERE username="+username+";";

        PreparedStatement psmt = null;
        try {
            psmt = conn.prepareStatement(sql);
            psmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (conn != null) {
                try {
                    conn.close(); // <-- This is important
                } catch (SQLException e) {
                    /* handle exception */
                }
            }
        }
    }

    public void changeULevel(String username, int ulevel){
        String sql = "UPDATE users SET ulevel="+ ulevel+" WHERE username="+username+";";

        PreparedStatement psmt = null;
        try {
            psmt = conn.prepareStatement(sql);
            psmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (conn != null) {
                try {
                    conn.close(); // <-- This is important
                } catch (SQLException e) {
                    /* handle exception */
                }
            }
        }
    }

    /**
     * getPrimaryKeyFromName
     * @param table - Name of table to search
     * @param firstName - firstName Search criteria
     * @param lastName - Surname search criteria
     * @return - the desired user id
     * @throws SQLException - if the user id isnt found etc.
     */
    private int getPrimaryKeyFromName(String table, String firstName, String lastName) throws SQLException {
        int key=0;
        String sql = "SELECT id FROM "+table+" WHERE firstName LIKE " + firstName + " AND lastName LIKE "+lastName +";";
        PreparedStatement psmt = conn.prepareStatement(sql);
        ResultSet rs = psmt.executeQuery();

        //If More than one name available..
        if(rs.getFetchSize()>1){
            System.out.println("More than one result found.");
            key = rs.getInt(0);
        } else{
            key = rs.getInt(0);
        }
        return key;
    }

    /**
     * getPerson
     * @param id - the id of the requested user
     * @return - all information about the person
     */
    public JSONObject getPerson(int id){
        String sql = "SELECT * FROM people WHERE personID=?;";
        ResultSet rs = null;
        JSONObject person = new JSONObject();
        try {
            PreparedStatement psmt = conn.prepareStatement(sql);
            psmt.setInt(1,id);
            rs = psmt.executeQuery();
            person = JSONConverter.convertToJSONObject(rs);
            return person;
        } catch (SQLException e) {
            e.printStackTrace();
            return new JSONObject();
        }
        finally {
            if (conn != null) {
                try {
                    conn.close(); // <-- This is important
                    DataStore.getInstance().newConnection();
                    conn = DataStore.getInstance().getConn();
                } catch (SQLException e) {
                    /* handle exception */
                }
            }
        }
        //return person;
    }

    public JSONArray getAllPeople(){
        String sql = "SELECT * FROM PEOPLE;";
        ResultSet rs = null;
        JSONArray people = new JSONArray();
        try {
            PreparedStatement psmt = conn.prepareStatement(sql);
            rs = psmt.executeQuery();
            people = JSONConverter.convertToJSONArray(rs);
            return people;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (conn != null) {
                try {
                    conn.close(); // <-- This is important
                    DataStore.getInstance().newConnection();
                    conn = DataStore.getInstance().getConn();
                } catch (SQLException e) {
                    /* handle exception */
                }
            }
        }

        return people;
    }

    /**
     * Checks Login credentials against those stored in the Database.
     * @param username
     * @param password
     * @return - true/false if details are valid.
     */
    public boolean checkUserCredentials(String username, String password) {
        //Hash Password first.
        byte[] salt = new byte[16];
        try {
            //Get Password Salt
            PreparedStatement p = conn.prepareStatement("SELECT password_salt FROM USERS WHERE username=?;");
            p.setString(1, username);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                salt = r.getBytes(1);
            }

            password = passwordHasher.hashPassword(password, salt);

            PreparedStatement psmt = conn.prepareStatement("SELECT * FROM USERS WHERE username=? and password=?;");
            psmt.setString(1, username);
            psmt.setString(2, password);
            ResultSet rs = psmt.executeQuery();
            //System.out.println(rs.getString(3));
            if(rs.next()){
                return true;
            } else{
                return false;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        finally {
            if (conn != null) {
                try {
                    conn.close(); // <-- This is important
                    DataStore.getInstance().newConnection();
                    conn = DataStore.getInstance().getConn();
                } catch (SQLException e) {
                    /* handle exception */
                }
            }
        }
    }

    public String getUserLevel(String username) {
        String userLevel = "";
        try {
            PreparedStatement p = conn.prepareStatement("SELECT userlevel FROM users WHERE username=?;");
            p.setString(1,username);
            ResultSet r = p.executeQuery();
            if (r.next()) {
                userLevel = String.valueOf(r.getInt("userlevel"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            if (conn != null) {
                try {
                    conn.close(); // <-- This is important
                    DataStore.getInstance().newConnection();
                    conn = DataStore.getInstance().getConn();
                } catch (SQLException e) {
                    /* handle exception */
                }
            }
        }

        return userLevel;
    }

    public Integer getUserID(String username){
        try {
            System.out.println("Getting username for " + username);
            PreparedStatement p = conn.prepareStatement("SELECT userID FROM users WHERE username=?;");
            p.setString(1, username);
            ResultSet rs = p.executeQuery();
            if (rs.next()) {
                return rs.getInt("userID");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Check if a username exists in the database
     * @param username - the username
     * @return - if the user exists
     */
    public boolean checkUserExists(String username) {
        try{
            PreparedStatement p = conn.prepareStatement("SELECT EXISTS (SELECT * FROM users WHERE username=?);");
            p.setString(1,username);
            ResultSet r = p.executeQuery();
            if(r.next()){
                p.close();
                return true;
            } else {
                return false;
            }
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        finally {
            if (conn != null) {
                try {
                    conn.close(); // <-- This is important
                    DataStore.getInstance().newConnection();
                    conn = DataStore.getInstance().getConn();
                } catch (SQLException e) {
                    /* handle exception */
                }
            }
        }
    }

    /**
     * getDetailsForUsername - Returns all details about a user for a given username.
     *
     * @param username - the user's username
     * @return - ArrayList of all details in String format.
     */
    public ArrayList<String> getDetailsForUsername(String username) {
        ArrayList<String> details = new ArrayList();
        try {
            int userID = getUserID(username);
            PreparedStatement p = conn.prepareStatement("SELECT * FROM People WHERE userID=?;");
            p.setInt(1, userID);
            ResultSet rs = p.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            if (rs.next()) {
                for (int i = 1; i < rsmd.getColumnCount(); i++) {
                    int columnType = rsmd.getColumnType(i);
                    if (columnType == Types.VARCHAR) {
                        details.add(rs.getString(i));
                    }
                    if (columnType == Types.INTEGER) {
                        details.add(Integer.toString(rs.getInt(i)));
                    }
                }
            }
            return details;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close(); // <-- This is important
                    DataStore.getInstance().newConnection();
                    conn = DataStore.getInstance().getConn();
                } catch (SQLException e) {
                    /* handle exception */
                }
            }
        }
        return details;
    }

    /**
     * getIDForUsername - Returns the person ID for a given username.
     * @param username
     * @return
     */
    public String getIDForUsername(String username) {
        String personID = "";
        try{
            PreparedStatement psmt = conn.prepareStatement("SELECT people.personID FROM people INNER JOIN users u on people.userID = u.userID where username=?;");
            psmt.setString(1,username);
            ResultSet rs = psmt.executeQuery();
            if(rs.next()){
                personID = Integer.toString(rs.getInt(1));
                return personID;
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        finally {
            if (conn != null) {
                try {
                    conn.close(); // <-- This is important
                    DataStore.getInstance().newConnection();
                    conn = DataStore.getInstance().getConn();
                } catch (SQLException e) {
                    /* handle exception */
                }
            }
        }
        return personID;

    }

    /**
     * Returns a specific event based on the id provided
     * @param id - the event id
     * @return - the event information as JSON Object
     */
    public JSONObject getEvent(int id) {
        JSONArray formattedEvent = new JSONArray();
        try{
            PreparedStatement psmt = conn.prepareStatement("SELECT * FROM events WHERE id=?;");
            psmt.setInt(1, id);
            ResultSet rs = psmt.executeQuery();
            if(rs.next()){
                int total_rows = rs.getMetaData().getColumnCount();
                for (int i = 0; i < total_rows; i++) {
                    JSONObject obj = new JSONObject();
                    obj.put(rs.getMetaData().getColumnLabel(i + 1)
                            .toLowerCase(), rs.getObject(i + 1));
                    formattedEvent.put(obj);
                }
            }
            return new JSONObject();
        } catch (SQLException e){
            e.printStackTrace();
            return new JSONObject(formattedEvent);
        }
        finally {
            if (conn != null) {
                try {
                    conn.close(); // <-- This is important
                    DataStore.getInstance().newConnection();
                    conn = DataStore.getInstance().getConn();
                } catch (SQLException e) {
                    /* handle exception */
                }
            }
        }

    }

    /**
     * Returns all upcoming events over a year
     * @return - the event information as JSON Object
     */
    public JSONArray getUpcomingEvents() {
        JSONArray allEvents = new JSONArray();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String date = dateFormat.format(today);
        System.out.println(date);
        try{
            PreparedStatement psmt = conn.prepareStatement("SELECT * FROM events WHERE datetime(startDate)>datetime('now') OR datetime(endDate)>datetime('now')");
            ResultSet rs = psmt.executeQuery();
            while(rs.next()){
                JSONObject event = new JSONObject();
                int total_rows = rs.getMetaData().getColumnCount();
                for (int i = 0; i < total_rows; i++) {
                    JSONObject obj = new JSONObject();
                    event.put(rs.getMetaData().getColumnLabel(i + 1)
                            .toLowerCase(), rs.getObject(i + 1));
                }
                allEvents.put(event);
            }
            //System.out.println(allEvents.toString());
            return allEvents;
        } catch (SQLException e){
            e.printStackTrace();
            return allEvents;
        }
        finally {
            if (conn != null) {
                try {
                    conn.close(); // <-- This is important
                    DataStore.getInstance().newConnection();
                    conn = DataStore.getInstance().getConn();
                } catch (SQLException e) {
                    /* handle exception */
                }
            }
        }
    }

    public JSONArray getAllEvents() {
        JSONArray allEvents = new JSONArray();
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date today = Calendar.getInstance().getTime();
        String date = dateFormat.format(today);
        //System.out.println(date);
        try{
            PreparedStatement psmt = conn.prepareStatement("SELECT * FROM events");
            ResultSet rs = psmt.executeQuery();
            while(rs.next()){
                JSONObject event = new JSONObject();
                int total_rows = rs.getMetaData().getColumnCount();
                for (int i = 0; i < total_rows; i++) {
                    JSONObject obj = new JSONObject();
                    event.put(rs.getMetaData().getColumnLabel(i + 1)
                            .toLowerCase(), rs.getObject(i + 1));
                }
                allEvents.put(event);
            }
            //System.out.println(allEvents.toString());
            return allEvents;
        } catch (SQLException e){
            e.printStackTrace();
            return allEvents;
        }
        finally {
            if (conn != null) {
                try {
                    conn.close(); // <-- This is important
                    DataStore.getInstance().newConnection();
                    conn = DataStore.getInstance().getConn();
                } catch (SQLException e) {
                    /* handle exception */
                }
            }
        }
    }

    public boolean deleteEvent(int eventID) {
        try{
            PreparedStatement p = conn.prepareStatement("DELETE FROM events WHERE id=?;");
            p.setInt(1,eventID);
            p.executeUpdate();
            return true;
        } catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        finally {
            if (conn != null) {
                try {
                    conn.close(); // <-- This is important
                    DataStore.getInstance().newConnection();
                    conn = DataStore.getInstance().getConn();
                } catch (SQLException e) {
                    /* handle exception */
                }
            }
        }
    }

    public boolean addEvent(String eventName, String startDate, String endDate, String notes, String projectManager) {
        System.out.println(startDate);
        try {
            String sql = "INSERT INTO events(name, startDate, endDate, notes, type, projectManager) VALUES (?,?,?,?,?,?);";
            PreparedStatement p = conn.prepareStatement(sql);
            p.setString(1,eventName);
            p.setString(2,startDate);
            p.setString(3,endDate);
            p.setString(4,notes);
            p.setString(5," ");
            p.setInt(6,Integer.parseInt(projectManager));
            p.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
        finally {
            if (conn != null) {
                try {
                    conn.close(); // <-- This is important
                    DataStore.getInstance().newConnection();
                    conn = DataStore.getInstance().getConn();
                } catch (SQLException e) {
                    /* handle exception */
                }
            }
        }
    }
}
