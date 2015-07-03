package ph.edu.speed.orbit.bukidutillity.Variable_Get_Post;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.apache.http.entity.StringEntity;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import ph.edu.speed.orbit.bukidutillity.FieldMapper.Mapper;
import ph.edu.speed.orbit.bukidutillity.MainActivity;
import ph.edu.speed.orbit.bukidutillity.PixelCam.CamActivity;
import ph.edu.speed.orbit.bukidutillity.R;
import ph.edu.speed.orbit.bukidutillity.Util.bukid_json_parser;


public class getRecipe extends ActionBarActivity {

    final String URL = "http://bukidutility.appspot.com/api/v1";
    final String task = "/recipe/";

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String response,val;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_recipe);



        AsyncHttpClient client = new AsyncHttpClient();
        client.get(URL + task, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(final int statusCode, Header[] headers, byte[] responseBody) {
                TextView stat = (TextView) findViewById(R.id.tvGetStat);
                stat.setText(String.valueOf(statusCode) + " Connected");

                /** Getting the response from backend */
                response = new String(responseBody);

                /** Save response to SharedPref */
                pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                editor = pref.edit();
                editor.putString("Recipe", response);
                editor.commit();

                //give response string to the json parser
                new bukid_json_parser(response);

                process();

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                TextView stat = (TextView) findViewById(R.id.tvGetStat);
                stat.setText(String.valueOf(statusCode) + " Not Connected");


                //get response saved in SharedPreference
                pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                response = pref.getString("Recipe", "[]");

                if (response != "[]") {
                    //give response string to the json parser
                    new bukid_json_parser(response);
                    process();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getRecipe.this);
                    LinearLayout layout = new LinearLayout(getRecipe.this);  //put MainActivity.this or similar
                    layout.setOrientation(LinearLayout.VERTICAL);
                    layout.setGravity(Gravity.CENTER_HORIZONTAL);

                    //setting dialog padding
                    layout.setPadding(10, 0, 10, 0);

                    builder.setView(layout);
                    builder.setTitle("No List Present, Please Connect to Internet!");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getRecipe.this, MainActivity.class);
                            startActivity(i);
                        }
                    });
                    builder.show();


                }


            }
        });
    }

    private void process() {
        final TextView stat = (TextView) findViewById(R.id.tvGetStat);
        final ListView listview = (ListView) findViewById(R.id.lvList);
        final ArrayList<String> list = new ArrayList<String>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list);

        final String[] jsonArray = bukid_json_parser.string_ar;
        //populate list array
        for (int i = 0; i < jsonArray.length; i++){
            list.add(jsonArray[i]);
        }

        listview.setAdapter(adapter);

        //setting array
        final String[] var_ID = new String[jsonArray.length];
        final String[] value = new String[jsonArray.length];
        final long[] timestamps = new long[jsonArray.length];



        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {

                switch(jsonArray[position]){

                    case ("Phosphorus"):
                    case ("Nitrogen"):
                    case ("Potassium"):
                        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                        editor = pref.edit();
                        editor.putBoolean("GetColor", true);
                        editor.apply();
                        Intent c = new Intent(getRecipe.this, CamActivity.class);
                        startActivityForResult(c,1);

                        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                        val = pref.getString("color", "");
                        assign(position, val);
                        break;


                    case ("GPS"):
                        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);
                        String str = pref.getString("Coordinates", "[]");
                        if(str == "[]"){
                            Intent g = new Intent(getRecipe.this,Mapper.class);
                            g.putExtra("getthis", "gps");
                            startActivityForResult(g,1);
                        }else{
                            val = str;
                        }
                        assign(position, val);
                        break;

                    default:
                        //setting layout
                        LinearLayout layout = new LinearLayout(getRecipe.this);  //put MainActivity.this or similar
                        layout.setOrientation(LinearLayout.VERTICAL);
                        layout.setGravity(Gravity.CENTER_HORIZONTAL);

                        //create an edit text
                        final EditText input = new EditText(getRecipe.this);     //put MainActivity.this or similar

                        //setting the allowed input characters
                        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                        //setting dialog padding
                        layout.setPadding(10, 0, 10, 0);

                        //set character inside

                        input.setHint("Old Value: " + value[position]);


                        //apply layout
                        layout.addView(input);

                        final AlertDialog.Builder builder = new AlertDialog.Builder(getRecipe.this);
                        builder.setView(layout);
                        builder.setTitle("Enter " + String.valueOf(jsonArray[position]) + " Value");

                        builder.setNegativeButton("Cancel", null);
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                val = String.valueOf(input.getText());
                                assign(position, val);
                            }


                        });
                        builder.show();
                        break;
                    }
            }


            private void assign(int position, String val) {
                value[position] = val; //input value
                timestamps[position] = System.currentTimeMillis(); //input timestamp
                var_ID[position] = jsonArray[position];
            }
        });


        //This is sending section
        Button btSend = (Button) findViewById(R.id.btSend);
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < jsonArray.length; i++) {
                    JSONObject jsonobj = new JSONObject();
                    try {
                        jsonobj.put("value", value[i]);
                        StringEntity data = new StringEntity(jsonobj.toString());

                        if (value[i] != null)
                            new JsonPoster(data, var_ID[i]);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }


                }
                stat.setText(JsonPoster.Status);
                stat.setText(JsonPoster.Status);
                stat.setText(JsonPoster.Status);

            }
        });
    }
}
