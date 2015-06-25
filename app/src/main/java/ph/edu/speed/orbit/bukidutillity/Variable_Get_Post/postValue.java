package ph.edu.speed.orbit.bukidutillity.Variable_Get_Post;

import android.content.Entity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import ph.edu.speed.orbit.bukidutillity.R;


public class postValue extends ActionBarActivity {

    final String TOKEN = "psjhDslSBTrTlzfpRi0CeMTPCYPml5ZGF1yKZHgFxF0aAu2NyLwT7HmZqHas";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_value);

        final ListView listview = (ListView) findViewById(R.id.lvList);
        final ArrayList<String> list = new ArrayList<String>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list);

        Button send = (Button) findViewById(R.id.btSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                try {
                    JSONObject jsonobj = new JSONObject();
                    jsonobj.put("value", "67");
                    StringEntity data = new StringEntity(jsonobj.toString());

                    //new JsonPoster(data);

                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }

        });


    }
}
