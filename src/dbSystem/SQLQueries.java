package dbSystem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * SQLQueries
 * Holds all general SQL Queries
 */
public class SQLQueries {
    private static DataStore ds;
    private static Connection conn;
    private static JSONConverter conv;

    public SQLQueries(DataStore ds, Connection conn){
        SQLQueries.ds = ds;
        SQLQueries.conn = conn;
        conv = new JSONConverter();
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
            sql+="UPDATE events VALUES "+name+", "+startDate.toString()+", "+endDate.toString()+" WHERE id="+id+";";
        }
        try{
            PreparedStatement psmt = conn.prepareStatement(sql);
            psmt.executeQuery();
        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void addUser(String username, String password, int ulevel){
        String sql = "INSERT INTO users VALUES "+ ulevel+", "+username+", "+password+";";

        PreparedStatement psmt = null;
        try {
            psmt = conn.prepareStatement(sql);
            psmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void changePassword(String username, String password){
        String sql = "UPDATE users SET password="+password+" WHERE username="+username+";";

        PreparedStatement psmt = null;
        try {
            psmt = conn.prepareStatement(sql);
            psmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
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
            person = JSONConverter.convertToJSON(rs);
            return person;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return person;
    }
}
