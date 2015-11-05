package com.ibm.bluebridge;

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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_parent_detail);

        this.ctxt = this;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Parent parent = (Parent)intent.getSerializableExtra("ParentObj");

        if(parent != null) {
            populateParentDetails(parent);
            //ImageView imgView =(ImageView)findViewById(R.id.imageView);
            //Drawable drawable = LoadImageFromWebOperations("http://school-volunteer-bluebridge.mybluemix.net/api/view_user_image?user_id=" + parent.getId());
            //imgView.setImageDrawable(drawable);
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
        ListView childrenView = (ListView) findViewById(R.id.childrenlist);

        nricView.setText(parent.getId());
        genderView.setText(parent.getGender());
        contactView.setText(parent.getContact());
        emailView.setText(parent.getEmail());
        jobView.setText(parent.getJob());
        addressView.setText(parent.getAddress());

        ArrayAdapter<Children> mAdapter = getChildrenListItemAdapter(ctxt, parent.getChildren());
        childrenView.setAdapter(mAdapter);
    }

    private Drawable LoadImageFromWebOperations(String url)
    {
        try{
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        }catch (Exception e) {
            System.out.println("Exc="+e);
            return null;
        }
    }
}
