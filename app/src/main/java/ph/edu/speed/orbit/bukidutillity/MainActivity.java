package ph.edu.speed.orbit.bukidutillity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import ph.edu.speed.orbit.bukidutillity.FieldMapper.MapsActivity;
import ph.edu.speed.orbit.bukidutillity.PixelCam.CamActivity;
import ph.edu.speed.orbit.bukidutillity.Variable_Get_Post.getRecipe;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //declare buttons
        Button send = (Button) findViewById(R.id.btsendValues);
        Button viewMap = (Button) findViewById(R.id.btviewMap);
        Button viewCam = (Button) findViewById(R.id.btviewCam);
        Button about = (Button) findViewById(R.id.btAbout);

        //set onClick buttons
        send.setOnClickListener(this);
        viewMap.setOnClickListener(this);
        viewCam.setOnClickListener(this);
        about.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()){

            case R.id.btsendValues:
                intent = new Intent(this, getRecipe.class);
                break;
            case R.id.btviewMap:
                intent = new Intent(this, MapsActivity.class);
                break;
            case R.id.btviewCam:
                intent = new Intent(this, CamActivity.class);
                break;
            case R.id.btAbout:
                intent = new Intent(this, about.class);
                break;
        }

        startActivity(intent);

    }
}
