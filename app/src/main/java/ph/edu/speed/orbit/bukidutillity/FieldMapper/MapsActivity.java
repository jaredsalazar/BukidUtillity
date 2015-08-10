package ph.edu.speed.orbit.bukidutillity.FieldMapper;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ph.edu.speed.orbit.bukidutillity.R;

public class MapsActivity extends FragmentActivity {

    int Count;
    double[] lat;
    double[] lng;
    SharedPreferences pref;
    JSONObject[] FarmPlotObjects;
    JSONArray FarmArray, PlotArray;
    JSONObject plotpolygonArray;
    String[] plotpolygonArrayString, plotpolygonAreaString;
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Count = 0;
        pref = getApplicationContext().getSharedPreferences("MyPref", MODE_PRIVATE);

        viewmap();

        Button create = (Button)findViewById(R.id.btSave);
        create.setOnClickListener(new android.view.View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createPolygon();
            }
        });

    }



    //Creates Polygon on other Activity
    private void createPolygon() {
        AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
        alertDialog.setTitle("Create New Polygon");
        alertDialog.setMessage("Are you sure?");
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(MapsActivity.this,Mapper.class);
                        startActivity(i);
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }



    //Setting up the polygons if available from sharedpref
    private void viewmap() {

        pref = getApplicationContext().getSharedPreferences("MyFarm", MODE_PRIVATE);
        TextView stat = (TextView) findViewById(R.id.tvMapStat);

        String str = pref.getString("feature", "[]");

        if (!(str == "[]")) {
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            JSONObject feature = null;
            try {
                feature = new JSONObject(str);
                JSONObject geometry = feature.getJSONObject("geometry");
                JSONArray coordinates = geometry.getJSONArray("coordinates");
                JSONArray inner_coordinates = coordinates.getJSONArray(0);

                double[] lat = new double[inner_coordinates.length()];
                double[] lng = new double[inner_coordinates.length()];

                for (int i = 0; i < inner_coordinates.length(); i++) {
                    JSONArray point = inner_coordinates.getJSONArray(i);
                    lat[i] = point.getDouble(1);
                    lng[i] = point.getDouble(0);
                }

                JSONObject properties = feature.getJSONObject("properties");
                String plotname = properties.getString("plotname");
                double area = properties.getDouble("Area");

                setUpMap(lat, lng, inner_coordinates.length(), plotname, area);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }


    //Setting up the polygons on the map
    private void setUpMap(double[] lat, double[] lng, int length, String plotname, double area) {
        PolygonOptions setup = new PolygonOptions().strokeColor(Color.RED).fillColor(Color.BLUE);
        for (int i = 0; i < length; i++) {
            setup.add(new LatLng(lat[i], lng[i]));
        }

        MarkerOptions options = new MarkerOptions().position(new LatLng(lat[0], lng[0]));
        options.title("Plotname: " + plotname);
        options.snippet("Area: " + area + " Sq-m");
        mMap.addMarker(options);
        mMap.addPolygon(setup);

        //zoom to location
        LatLng coordinate = new LatLng(lat[0], lng[0]);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 20);
        mMap.animateCamera(yourLocation);

    }



    @Override
    protected void onResume() {
        super.onResume();
        viewmap();
    }
}
