package ph.edu.speed.orbit.bukidutillity.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jaredsalazar on 5/31/15.
 */
public class bukid_json_parser {

    public static String[] string_ar;
    static JSONArray jArray;
    static String string;
    static JSONObject[] variable;

    public bukid_json_parser(String response) {

        variable = new JSONObject[1];

        try {
            jArray = new JSONArray(response);
            variable[0] = jArray.getJSONObject(0);
            string = variable[0].getString("recipe variables");
            string_ar = string.split(", ");


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
}
