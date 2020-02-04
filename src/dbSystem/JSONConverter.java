package dbSystem;

import com.google.gson.JsonParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * JSONConverter - Utility Functions to convert SQL ResultSets into JSON.
 */
public class JSONConverter {

    /**
     * convertToJSONArray
     * @param resultSet
     * @return
     * @throws SQLException
     */
    public static JSONArray convertToJSONArray(ResultSet resultSet)
            throws SQLException {
        JSONArray arr = new JSONArray();
        int colCount = resultSet.getMetaData().getColumnCount();
        while(resultSet.next()){
            arr.put(getJSObject(colCount,resultSet));
        }

        return arr;
    }

    /**
     * getJSObject - Return an individual JSON Object
     * @param columnCount - Number of columns.
     * @param rs - resultSet
     * @return JSONObject - returns all rows as JSON objects.
     * @throws SQLException - if nothing exists.
     */
    public static JSONObject getJSObject(int columnCount, ResultSet rs) throws SQLException {
        JSONObject object = new JSONObject();

        for(int i=0; i<columnCount; i++){
            object.put(rs.getMetaData().getColumnLabel(i + 1)
                    .toLowerCase(), rs.getObject(i + 1));
        }
        return object;
    }

    /**
     * Convert a result set into a JSON Object
     * @param resultSet
     * @return a JSONObject
     * @throws Exception
     */
    public static JSONObject convertToJSONObject(ResultSet resultSet)
            throws SQLException {
        JSONObject obj = new JSONObject();
        while (resultSet.next()) {
            int columnCount = resultSet.getMetaData().getColumnCount();
            for (int i = 0; i < columnCount; i++) {
                obj.put(resultSet.getMetaData().getColumnLabel(i + 1)
                        .toLowerCase(), resultSet.getObject(i + 1));
            }
        }
        return obj;
    }
    /**
     * Convert a result set into a XML List
     * @param resultSet
     * @return a XML String with list elements
     * @throws Exception if something happens
     */
    public static String convertToXML(ResultSet resultSet)
            throws SQLException {
        StringBuffer xmlArray = new StringBuffer("<results>");
        while (resultSet.next()) {
            int total_rows = resultSet.getMetaData().getColumnCount();
            xmlArray.append("<result ");
            for (int i = 0; i < total_rows; i++) {
                xmlArray.append(" " + resultSet.getMetaData().getColumnLabel(i + 1)
                        .toLowerCase() + "='" + resultSet.getObject(i + 1) + "'"); }
            xmlArray.append(" />");
        }
        xmlArray.append("</results>");
        return xmlArray.toString();
    }
}