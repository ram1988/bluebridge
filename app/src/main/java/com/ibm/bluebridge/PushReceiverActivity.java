package com.ibm.bluebridge;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.ibm.mobilefirstplatform.clientsdk.android.push.internal.MFPInternalPushMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

public class PushReceiverActivity extends AppCompatActivity {
    private final String KEY_PAYLOAD = "payload";
    private final String KEY_TYPE = "type";

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

        MFPInternalPushMessage message = (MFPInternalPushMessage) getIntent().getExtras().get("message");
        JSONObject message_json = message.toJson();
        try {
            String payload = message_json.getString(KEY_PAYLOAD);
            JSONObject payload_json = new JSONObject(payload);
            if(payload_json != null) {
                String type = payload_json.getString(KEY_TYPE);
                if(type != null) {
                    if (type.toLowerCase().equals(PUSH_TYPE.EVENT.getValue())){
                        redirect("com.ibm.bluebridge.EventParentDetailActivity", message_json);
                    } else if (type.toLowerCase().equals(PUSH_TYPE.ALERT.getValue())) {
                        redirect("com.ibm.bluebridge.EventParentDetailActivity", message_json);
                    } else {
                        Log.e("PushReceiverActivity", "Invalid payload, cannot handle. Redirecting to home page.");
                        redirect("com.ibm.bluebridge.EventLoginActivity", message_json);
                    }
                }
            }
        } catch (JSONException e) {
            Log.e("PushReceiverActivity", e.getMessage());
        } catch (Exception e){
            Log.e("PushReceiverActivity", e.getMessage());
        }
    }

    private void redirect(String className, JSONObject message) throws JSONException{
        Intent intent = new Intent();
        intent.setClassName("com.ibm.bluebridge", className);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        Iterator<?> keys = message.keys();
        while(keys.hasNext()) {
            String key = (String)keys.next();
            String value = message.getString(key);

            intent.putExtra(key, value);
        }

        Log.i("PushReceiverActivity", "Redirecting to "+className);
        this.startActivity(intent);
    }
}
