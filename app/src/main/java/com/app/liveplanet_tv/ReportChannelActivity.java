package com.app.liveplanet_tv;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.liveplanet_tv.R;
import com.example.util.Constant;
import com.example.util.JsonUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReportChannelActivity extends AppCompatActivity {

    ImageView imgChannel;
    TextView txtChannel;
    EditText edtComment;
    Button btnSubmit;
    String strName, strImage, strComment, strId;
    MyApplication MyApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportchannel);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.report_channel));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        imgChannel = (ImageView) findViewById(R.id.image);
        txtChannel = (TextView) findViewById(R.id.text);
        edtComment = (EditText) findViewById(R.id.edt_text);
        btnSubmit = (Button) findViewById(R.id.button);
        MyApp = MyApplication.getInstance();

        Intent intent = getIntent();
        strName = intent.getStringExtra("Name");
        strImage = intent.getStringExtra("Image");
        strId = intent.getStringExtra("Id");

        txtChannel.setText(strName);
        Picasso.with(ReportChannelActivity.this).load(Constant.IMAGE_PATH + strImage).into(imgChannel);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strComment = edtComment.getText().toString().replace(" ","%20");
                if (!strComment.isEmpty()) {
                    if (JsonUtils.isNetworkAvailable(ReportChannelActivity.this)) {
                        new Report().execute(Constant.REPORT_CHANNEL_URL + MyApp.getUserName() + "&email=" + MyApp.getUserEmail() + "&channel_id=" + strId + "&report=" + strComment);
                    } else {
                        showToast(getString(R.string.conne_msg1));
                    }
                }
            }
        });
    }

    private class Report extends AsyncTask<String, Void, String> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            pDialog = new ProgressDialog(ReportChannelActivity.this);
            pDialog.setMessage("Loading...");
            pDialog.setCancelable(false);
            pDialog.show();
        }


        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            return JsonUtils.getJSONString(params[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            super.onPostExecute(result);
            if (null != pDialog && pDialog.isShowing()) {
                pDialog.dismiss();
            }

            if (null == result || result.length() == 0) {
                showToast(getString(R.string.nodata));


            } else {

                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        objJson = jsonArray.getJSONObject(i);
                        showToast(objJson.getString(Constant.MSG));
                        finish();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    public void showToast(String msg) {
        Toast.makeText(ReportChannelActivity.this, msg, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }
        return true;
    }
}
