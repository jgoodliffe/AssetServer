package dbSystem;

import authentication.PasswordGenerator;
import authentication.PasswordHasher;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.*;
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
        String sql = "INSERT INTO users VALUES ?,?,?;";

        //Hash password
        password = passwordHasher.hashPassword(password);

        PreparedStatement psmt = null;
        try {
            psmt = conn.prepareStatement(sql);
            psmt.setInt(1, uLevel);
            psmt.setString(2, username);
            psmt.setString(3, password);
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

    public void changePassword(int id, String password){
        String sql = "UPDATE users SET password=? WHERE id=?;";


        //Hash new Password:
        password = passwordHasher.hashPassword(password);

        try {
            conn.close();
            DataStore.getInstance().newConnection();
            conn = DataStore.getInstance().getConn();
            PreparedStatement psmt = conn.prepareStatement(sql);
            psmt.setInt(2,id);
            psmt.setString(1, password);
            psmt.executeUpdate();
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
     * @return - all informatiomn about the person
     */
    public JSONObject getPerson(int id){
        String sql = "SELECT * FROM people WHERE id="+id+";";
        ResultSet rs = null;
        JSONObject person = new JSONObject();
        try {
            PreparedStatement psmt = conn.prepareStatement(sql);
            rs = psmt.executeQuery();
            person = JSONConverter.convertToJSONObject(rs);
            return person;
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

        return person;
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
        ResultSet rs = null;
        try{
            PreparedStatement psmt = conn.prepareStatement("SELECT * FROM USERS WHERE username=? and password=?;");
            psmt.setString(1,username);
            psmt.setString(2,password);
            rs = psmt.executeQuery();
            psmt.close();

            return rs.next();
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
        try{
            PreparedStatement p = conn.prepareStatement("SELECT id FROM users WHERE username=?;");
            p.setString(1,username);
            ResultSet rs = p.executeQuery();
            if(rs.next()){
                return rs.getInt("id");
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
}
