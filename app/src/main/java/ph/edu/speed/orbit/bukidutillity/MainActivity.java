package ph.edu.speed.orbit.bukidutillity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;

import ph.edu.speed.orbit.bukidutillity.FieldMapper.MapsActivity;
import ph.edu.speed.orbit.bukidutillity.Variable_Get_Post.getRecipe;
import ph.edu.speed.orbit.bukidutillity.Variable_Get_Post.postValue;
import ph.edu.speed.orbit.bukidutillity.Variable_Get_Post.viewValues;


public class MainActivity extends ActionBarActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //declare buttons
        Button get = (Button) findViewById(R.id.btGet);
        Button post = (Button) findViewById(R.id.btPost);
        Button view = (Button) findViewById(R.id.btView);
        Button about = (Button) findViewById(R.id.btAbout);

        //set onClick buttons
        get.setOnClickListener(this);
        post.setOnClickListener(this);
        view.setOnClickListener(this);
        about.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent = null;

        switch (v.getId()){

            case R.id.btGet:
                intent = new Intent(this, getRecipe.class);
                break;
            case R.id.btPost:
                intent = new Intent(this, postValue.class);
                break;
            case R.id.btView:
                intent = new Intent(this, viewValues.class);
                break;
            case R.id.btAbout:
                intent = new Intent(this, MapsActivity.class);
                break;
        }

        startActivity(intent);

    }
}
