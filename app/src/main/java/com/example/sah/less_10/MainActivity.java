package com.example.sah.less_10;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKBatchRequest;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKList;
import com.vk.sdk.util.VKUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {


    private String[] scope = new String[]{VKScope.STATUS};
    private ListView listView;
    private TextView tv1,tvLstName, tvFstName, tvCountry, tvCity, tvBdate;
    private Button btnFriends;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.ivAvatar);
        tv1 = (TextView)findViewById(R.id.tv1);
        tvLstName = (TextView)findViewById(R.id.tvLstName);
        tvFstName = (TextView)findViewById(R.id.tvFstName);
        tvCountry = (TextView)findViewById(R.id.tvCountry);
        tvBdate = (TextView)findViewById(R.id.tvBdate);
        tvCity = (TextView)findViewById(R.id.tvCity);
        btnFriends = (Button) findViewById(R.id.btnFriends);
        listView = (ListView) findViewById(R.id.lv);

//        String[] fingerprints = VKUtil.getCertificateFingerprint(this, this.getPackageName());
//        Log.d("mLog", "ID = " + Arrays.asList(fingerprints));

        VKSdk.login(this, scope);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {

                VKRequest request = VKApi.users()
                        .get(VKParameters.from(VKApiConst.FIELDS,
                                "first_name, last_name, photo, bdate, country, home_town"));


                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        JSONObject json = response.json;
                        try {
                            JSONArray array = json.getJSONArray("response");
                            JSONObject jsonObject = new JSONObject(array.getString(0));

                            tvLstName.setText(jsonObject.getString("last_name"));
                            tvFstName.setText(jsonObject.getString("first_name"));
                            tvBdate.setText(jsonObject.getString("bdate"));
                            tvCity.setText(jsonObject.getString("home_town"));
                            tvCountry.setText(jsonObject.getJSONObject("country").getString("title"));
                            Picasso.with(getApplicationContext()).load(jsonObject.getString("photo")).into(imageView);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                        btnFriends.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                VKRequest request1 = VKApi.friends().get(VKParameters.from(VKApiConst.FIELDS, "first_name, last_name"));
                                request1.executeWithListener(new VKRequest.VKRequestListener() {
                                    @Override
                                    public void onComplete(VKResponse response) {
                                        super.onComplete(response);
                                        VKList list = (VKList) response.parsedModel;
                                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                                        android.R.layout.simple_expandable_list_item_1, list);
                                        listView.setAdapter(adapter);

                                    }
                                });
                            }
                        });

                     }
                });

            }
            @Override
            public void onError(VKError error) {
                Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.logout:
                VKSdk.logout();
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
