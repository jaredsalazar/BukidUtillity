package ph.edu.speed.orbit.bukidutillity.FieldMapper;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Iterator;

import ph.edu.speed.orbit.bukidutillity.R;

import static com.google.maps.android.SphericalUtil.computeArea;

/**
 * Most of the lines came from https://github.com/danny-source/Android-GPS-NMEA
 */

public class Mapper extends Activity {
    LatLng c;
    int count;
    ArrayList<LatLng> coord;
    double[] latitude, longitude;
    double Area;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    private LocationManager locationManager = null;
    private Criteria criteria = null;
    private LocationListener locationListener = null;
    private GpsStatus.NmeaListener nmeaListener = null;
    private GpsStatus.Listener gpsStatusListener = null;
    private TextView txtGPS_Quality = null;
    private TextView txtGPS_Location = null;
    private TextView txtGPS_Satellites = null;
    //
    private Handler mHandler = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapper);

        /** Initialize */
        count = 0;
        latitude = new double[99];
        longitude = new double[99];
        coord = new ArrayList<>();
        pref = getApplicationContext().getSharedPreferences("MyFarm", MODE_PRIVATE);

        Button mark = (Button) findViewById(R.id.btMark);
        mark.setEnabled(false);
        Button save = (Button) findViewById(R.id.btSave);


        txtGPS_Quality = (TextView) findViewById(R.id.textGPS_Quality);
        txtGPS_Location = (TextView) findViewById(R.id.textGPS_Location);
        txtGPS_Satellites = (TextView) findViewById(R.id.textGPS_Satellites);
        registerHandler();
        registerListener();
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
        locationManager.addNmeaListener(nmeaListener);
    }


    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        locationManager.removeUpdates(locationListener);
        locationManager.removeNmeaListener(nmeaListener);
    }

    private void registerListener() {
        locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location loc) {
                // TODO Auto-generated method stub
                Log.d("GPS-NMEA", loc.getLatitude() + "," + loc.getLongitude());
            }

            @Override
            public void onProviderDisabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onProviderEnabled(String provider) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                // TODO Auto-generated method stub
                Log.d("GPS-NMEA", provider + "");
                switch (status) {
                    case LocationProvider.OUT_OF_SERVICE:
                        Log.d("GPS-NMEA", "OUT_OF_SERVICE");
                        break;
                    case LocationProvider.TEMPORARILY_UNAVAILABLE:
                        Log.d("GPS-NMEA", " TEMPORARILY_UNAVAILABLE");
                        break;
                    case LocationProvider.AVAILABLE:
                        Log.d("GPS-NMEA", "" + provider + "");

                        break;
                }

            }

        };
//
        nmeaListener = new GpsStatus.NmeaListener() {
            public void onNmeaReceived(long timestamp, String nmea) {
                //check nmea's checksum
                if (isValidForNmea(nmea)) {
                    nmeaProgress(nmea);
                    Log.d("GPS-NMEA", nmea);
                }

            }
        };
//
        gpsStatusListener = new GpsStatus.Listener() {
            public void onGpsStatusChanged(int event) {
                // TODO Auto-generated method stub
                GpsStatus gpsStatus;
                gpsStatus = locationManager.getGpsStatus(null);

                switch (event) {
                    case GpsStatus.GPS_EVENT_FIRST_FIX:
                        //
                        gpsStatus.getTimeToFirstFix();
                        Log.d("GPS-NMEA", "GPS_EVENT_FIRST_FIX");
                        break;
                    case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

                        Iterable<GpsSatellite> allSatellites = gpsStatus.getSatellites();
                        Iterator<GpsSatellite> it = allSatellites.iterator();

                        int count = 0;
                        while (it.hasNext()) {
                            GpsSatellite gsl = it.next();

                            if (gsl.getSnr() > 0.0) {
                                count++;
                            }

                        }


                        break;
                    case GpsStatus.GPS_EVENT_STARTED:
                        //Event sent when the GPS system has started.
                        Log.d("GPS-NMEA", "GPS_EVENT_STARTED");
                        break;
                    case GpsStatus.GPS_EVENT_STOPPED:
                        //Event sent when the GPS system has stopped.
                        Log.d("GPS-NMEA", "GPS_EVENT_STOPPED");
                        break;
                    default:
                        break;
                }
            }

        };

    }

    private void registerHandler() {
    /*
	GGA Global Positioning System Fix Data. Time, Position and fix related data for a GPS receiver
	11
	1 2 34 5678 910|121314 15
	||||||||||||||| $--GGA,hhmmss.ss,llll.ll,a,yyyyy.yy,a,x,xx,x.x,x.x,M,x.x,M,x.x,xxxx*hh
	1) Time (UTC)
	2) Latitude
	3) N or S (North or South)
	4) Longitude
	5) E or W (East or West)
	6) GPS Quality Indicator,
	0 - fix not available,
	1 - GPS fix,
	2 - Differential GPS fix
	7) Number of satellites in view, 00 - 12
	8) Horizontal Dilution of precision
	9) Antenna Altitude above/below mean-sea-level (geoid)
	10) Units of antenna altitude, meters
	11) Geoidal separation, the difference between the WGS-84 earth
	ellipsoid and mean-sea-level (geoid), "-" means mean-sea-level below ellipsoid
	12) Units of geoidal separation, meters
	13) Age of differential GPS data, time in seconds since last SC104
	type 1 or 9 update, null field when DGPS is not used
	14) Differential reference station ID, 0000-1023
	15) Checksum
		 */
        mHandler = new Handler() {
            public void handleMessage(Message msg) {

                String str = (String) msg.obj;
                String[] rawNmeaSplit = str.split(",");
                txtGPS_Quality.setText(rawNmeaSplit[6]);
                txtGPS_Location.setText(rawNmeaSplit[2] + " " + rawNmeaSplit[3] + "," + rawNmeaSplit[4] + " " + rawNmeaSplit[5]);
                txtGPS_Satellites.setText(rawNmeaSplit[7]);

                Button mark = (Button) findViewById(R.id.btMark);
                Button save = (Button) findViewById(R.id.btSave);
                TextView stat = (TextView) findViewById(R.id.tvMStat);

                if (Integer.valueOf(rawNmeaSplit[6]) != 0) {      //if GPS Quality is Good
                    if (Integer.valueOf(rawNmeaSplit[7]) > 3) {   //if there are more than 3 satelite lock
                        mark.setEnabled(true);
                        final double lat = Double.valueOf(rawNmeaSplit[2]) / 100;
                        final double lon = Double.valueOf(rawNmeaSplit[4]) / 100;
                        //stat.setText("Latitude: " + String.valueOf(lat) + "\nLongitude: " + String.valueOf(lon));

                        mark.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mapping_start(lat, lon);
                            }


                        });

                        save.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mapping_stop();
                            }
                        });


                    } else {
                        mark.setEnabled(false);
                    }
                } else
                    mark.setEnabled(false);
            }

        };


    }

    private void mapping_stop() {
        Area = computeArea(coord);
        if (Area != 0.0) {
            getPlotName(Area);
        } else {
            reploting();
        }
    }

    private void reploting() {
        /** Get Plot Name */
        //setting layout
        LinearLayout layout = new LinearLayout(Mapper.this);  //put MainActivity.this or similar
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        //setting dialog padding
        layout.setPadding(10, 0, 10, 0);

        final AlertDialog.Builder builder = new AlertDialog.Builder(Mapper.this);
        builder.setView(layout);
        builder.setTitle("No calculated Area");
        builder.setMessage("Do you want to repeat?");

        builder.setNegativeButton("Back to Main", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                count = 0;
                coord.clear();
                Intent i = new Intent(Mapper.this, MapsActivity.class);
                startActivity(i);
            }
        });
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                count = 0;
                coord.clear();
            }
        });
        builder.show();
    }

    private void getPlotName(final double area) {
        final String[] name = {null};
        /** Get Plot Name */
        //setting layout
        LinearLayout layout = new LinearLayout(Mapper.this);  //put MainActivity.this or similar
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.CENTER_HORIZONTAL);

        //create an edit text
        final EditText input = new EditText(Mapper.this);     //put MainActivity.this or similar

        //setting dialog padding
        layout.setPadding(10, 0, 10, 0);

        //apply layout
        layout.addView(input);

        final AlertDialog.Builder builder = new AlertDialog.Builder(Mapper.this);
        builder.setView(layout);
        builder.setTitle("Enter Plot Name");

        builder.setNegativeButton("Cancel", null);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                name[0] = String.valueOf(input.getText());
                compile(name[0], area);
            }
        });
        builder.show();
    }

    private void compile(String plotName, double area) {
        TextView stat = (TextView) findViewById(R.id.tvMStat);
        //String featurecollection = "{\"type\": \"FeatureCollection\", \"features\": [";
        String feature = "{ \"type\":\"Feature\",\"geometry\":{\"type\":\"Polygon\",\"coordinates\":[";
        String coor = "[";
        for (int i = 0; i < count - 1; i++) {
            coor = coor + "[" + longitude[i] + "," + latitude[i] + "]" + ",";
        }
        coor = coor + "[" + longitude[count - 1] + "," + latitude[count - 1] + "],[" +
                longitude[0] + "," + latitude[0] + "]]]";

        String properties = "},\"properties\": {\"plotname\": \"" + plotName + "\",\"Area\":"
                + area + "}";
        feature = feature + coor + properties + "}";
        stat.setText(feature);
        editor = pref.edit();
        editor.putString("feature", feature);
        editor.commit();

        /*
        String fullcollection = pref.getString("fullcollection","{}");
        if(fullcollection == "{}"){
            fullcollection = featurecollection + feature + " ]\n" + "}";
            editor.putString("fullcollection",fullcollection);
            editor.commit();
        }else{
            try {
                JSONObject FCobject = new JSONObject(fullcollection);
                JSONArray Farray = FCobject.getJSONArray("");
                Farray.put(feature);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //stat.setText(fullcollection);
        */
        Intent i = new Intent(this, MapsActivity.class);
        startActivity(i);
    }

    private void mapping_start(double lat, double lon) {
        TextView stat = (TextView) findViewById(R.id.tvMStat);
        latitude[count] = lat;
        longitude[count] = lon;
        if (count == 0) {
            c = new LatLng(latitude[count], longitude[count]);
            coord.add(c);
            count++;
        } else if (count != 0 && latitude[count] != latitude[count - 1]) {
            c = new LatLng(latitude[count], longitude[count]);
            coord.add(c);
            count++;
        }
        stat.setText(String.valueOf(count) + " : " + String.valueOf(c));
    }

    //custom
    //nmea callback
    private void nmeaProgress(String rawNmea) {

        String[] rawNmeaSplit = rawNmea.split(",");

        if (rawNmeaSplit[0].equalsIgnoreCase("$GPGGA")) {
            //send GGA nmea data to handler
            Message msg = new Message();
            msg.obj = rawNmea;
            mHandler.sendMessage(msg);
        }

    }


    private boolean isValidForNmea(String rawNmea) {
        boolean valid = true;
        byte[] bytes = rawNmea.getBytes();
        int checksumIndex = rawNmea.indexOf("*");
        //NMEA checksum number
        byte checksumCalcValue = 0;
        int checksumValue;

        //$
        if ((rawNmea.charAt(0) != '$') || (checksumIndex == -1)) {
            valid = false;
        }
        //
        if (valid) {
            String val = rawNmea.substring(checksumIndex + 1, rawNmea.length()).trim();
            checksumValue = Integer.parseInt(val, 16);
            for (int i = 1; i < checksumIndex; i++) {
                checksumCalcValue = (byte) (checksumCalcValue ^ bytes[i]);
            }
            if (checksumValue != checksumCalcValue) {
                valid = false;
            }
        }
        return valid;
    }
}
