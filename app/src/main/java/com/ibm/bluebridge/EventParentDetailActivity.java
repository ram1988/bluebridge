package com.ibm.bluebridge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.ibm.bluebridge.valueobject.Children;
import com.ibm.bluebridge.valueobject.Event;
import com.ibm.bluebridge.valueobject.Parent;

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


        populateParentDetails(parent);
    }

    private void populateParentDetails(Parent parent) {

        this.setTitle(parent.getFirstname()+" "+parent.getLastname());

        TextView nricView = (TextView) findViewById(R.id.nric_text);
        TextView fnameView = (TextView) findViewById(R.id.fname_text);
        TextView lnameView = (TextView) findViewById(R.id.lname_text);
        TextView genderView = (TextView) findViewById(R.id.gender_text);
        TextView contactView = (TextView) findViewById(R.id.contact_text);
        TextView emailView = (TextView) findViewById(R.id.email_text);
        TextView jobView = (TextView) findViewById(R.id.job_text);
        TextView addressView = (TextView) findViewById(R.id.address_text);
        ListView childrenView = (ListView) findViewById(R.id.childrenlist);

        nricView.setText(parent.getId());
        fnameView.setText(parent.getFirstname());
        lnameView.setText(parent.getLastname());
        genderView.setText(parent.getGender());
        contactView.setText(parent.getContact());
        emailView.setText(parent.getEmail());
        jobView.setText(parent.getJob());
        addressView.setText(parent.getAddress());

        ArrayAdapter<Children> mAdapter = getChildrenListItemAdapter(ctxt, parent.getChildren());
        childrenView.setAdapter(mAdapter);
    }

}
