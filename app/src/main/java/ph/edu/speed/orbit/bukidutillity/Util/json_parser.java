package ph.edu.speed.orbit.bukidutillity.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jaredsalazar on 5/29/15.
 */
public class json_parser {

    public static String[] Name;
    public static String[] Var_ID;
    public static Integer Count;

    JSONObject jObject;
    JSONArray jArray;
    JSONObject[] variable;

    public json_parser(String response) {

        try {
            jObject = new JSONObject(response);
            Count = jObject.getInt("count");

            //setting arrays with count
            variable = new JSONObject[Count];
            Name = new String[Count];
            Var_ID = new String[Count];

            jArray = jObject.getJSONArray("results");

            for (int i = 0; i < Count; i++){
                variable[i] = jArray.getJSONObject(i);
                Name[i] = variable[i].getString("name");
                Var_ID[i] = variable[i].getString("id");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
