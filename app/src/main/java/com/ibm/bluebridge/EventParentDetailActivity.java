package com.ibm.bluebridge;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.bluebridge.util.RESTApi;
import com.ibm.bluebridge.valueobject.Children;
import com.ibm.bluebridge.valueobject.Event;
import com.ibm.bluebridge.valueobject.Parent;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class EventParentDetailActivity extends EventMasterActivity {

    private Context ctxt;
    private ProgressDialog pDialog;
    private Bitmap bitmap;
    private ImageView photo;
    private RESTApi REST_API;
    private Intent callIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_parent_detail);

        this.ctxt = this;
        this.REST_API = new RESTApi();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Parent parent = (Parent)intent.getSerializableExtra("ParentObj");

        photo = (ImageView) findViewById(R.id.photo);
        String imageURL = REST_API.getBaseRestURL() + "/view_user_image?user_id=" + parent.getId();
        new LoadImage().execute(imageURL);

        if(parent != null) {
            populateParentDetails(parent);
        }
    }

    private void populateParentDetails(Parent parent) {

        this.setTitle(parent.getFirstname()+" "+parent.getLastname());

        TextView nricView = (TextView) findViewById(R.id.nric_text);
        TextView genderView = (TextView) findViewById(R.id.gender_text);
        TextView contactView = (TextView) findViewById(R.id.contact_text);
        TextView emailView = (TextView) findViewById(R.id.email_text);
        TextView jobView = (TextView) findViewById(R.id.job_text);
        TextView addressView = (TextView) findViewById(R.id.address_text);
        //ListView childrenView = (ListView) findViewById(R.id.childrenlist);
        callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:+" + parent.getContact().trim()));

        contactView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                startActivity(callIntent);
            }
        });

        nricView.setText("NRIC:           " + parent.getId());
        genderView.setText("Gender:        " + parent.getGender());
        contactView.setText("Contact:       " + parent.getContact());
        //emailView.setText(  "Email:          " + parent.getEmail());
        jobView.setText(    "Job:              " + parent.getJob());
        addressView.setText("Address:      " + parent.getAddress());

        emailView.setText(Html.fromHtml("Email:            <a href=\"mailto:" + parent.getEmail() + "\">" + parent.getEmail() + "</a>"));
        emailView.setMovementMethod(LinkMovementMethod.getInstance());

       // ArrayAdapter<Children> mAdapter = getChildrenListItemAdapter(ctxt, parent.getChildren());
       // childrenView.setAdapter(mAdapter);
    }

    private class LoadImage extends AsyncTask<String, String, Bitmap> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(EventParentDetailActivity.this);
            pDialog.setMessage("Loading Image ....");
            pDialog.show();

        }
        protected Bitmap doInBackground(String... args) {
            try {
                bitmap = BitmapFactory.decodeStream((InputStream)new URL(args[0]).getContent());

            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                photo.setImageBitmap(image);
                pDialog.dismiss();
            }else{
                pDialog.dismiss();
                Toast.makeText(EventParentDetailActivity.this, "Image Does Not exist or Network Error", Toast.LENGTH_SHORT).show();

            }
        }
    }
}


