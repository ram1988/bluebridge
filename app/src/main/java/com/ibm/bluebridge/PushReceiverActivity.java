package com.ibm.bluebridge;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ibm.bluebridge.util.SessionManager;

public class PushReceiverActivity extends AppCompatActivity {
    private final String KEY_PAYLOAD = "payload";
    private final String KEY_TYPE = "type";
    SessionManager session = SessionManager.getSessionInstance(this);

    private enum PUSH_TYPE{
        EVENT("event"),
        ALERT("alert");

        String value;
        PUSH_TYPE(String value){
            this.value = value;
        }

        public String getValue(){
            return this.value;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_push_receiver);

        Bundle extras = getIntent().getExtras();
        Log.d("PushReceiver", extras.toString());

        try {
            String type = extras.getString("type");
            if(type != null) {
                if (type.toLowerCase().equals(PUSH_TYPE.EVENT.getValue())){
                    String event_id = extras.getString("event_id");
                    PushReceiverActivity.this.finish();

                    Intent intent = new Intent();
                    intent.setClassName("com.ibm.bluebridge", "com.ibm.bluebridge.EventFormViewActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("event_id", event_id);

                    Log.i("PushReceiverActivity", "Redirecting to com.ibm.bluebridge.EventFormViewActivity");
                    this.startActivity(intent);
                } else if (type.toLowerCase().equals(PUSH_TYPE.ALERT.getValue())) {
                    String push_message = extras.getString("message");
                    PushReceiverActivity.this.finish();
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    if(session.isAdmin()){
                        intent.setClassName("com.ibm.bluebridge", "com.ibm.bluebridge.EventAdminHomeSpinnerCateActivity");
                        intent.putExtra("message", push_message);
                        intent.putExtra("alert", true);
                        this.startActivity(intent);
                    }else if(session.isParent()){
                        intent.setClassName("com.ibm.bluebridge", "com.ibm.bluebridge.EventParentHomeSpinnerActivity");
                        intent.putExtra("message", push_message);
                        intent.putExtra("alert", true);
                        this.startActivity(intent);
                    }
                } else {
                    Log.e("PushReceiverActivity", "Invalid payload, cannot handle. Redirecting to login page.");

                    PushReceiverActivity.this.finish();
                    Intent intent = new Intent();
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setClassName("com.ibm.bluebridge", "com.ibm.bluebridge.EventLoginActivity");
                    intent.putExtra("message", "Please login");
                    intent.putExtra("alert", true);
                    this.startActivity(intent);
                }
            }
        } catch (Exception e){
            Log.e("PushReceiverActivity", e.getMessage());
        }
    }

}
