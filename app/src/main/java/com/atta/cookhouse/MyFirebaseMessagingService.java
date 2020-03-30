package com.atta.cookhouse;

import android.content.Intent;
import android.util.Log;

import com.atta.cookhouse.main.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {


    private static final String TAG = "MyFirebaseMsgService";


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }
    //this method will display the notification
    //We are passing the JSONObject that is received from
    //firebase cloud messaging
    private void sendPushNotification(JSONObject json) {
        //optionally we can display the json into log
        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            //getting the json data
            JSONObject data = json.getJSONObject("data");

            //parsing json data
            String title = data.getString("title");
            String message = data.getString("message");

            //creating MyNotificationManager object
            MyNotificationManager mNotificationManager = null;

            Intent intent = null;
            //creating an intent for the notification
            if(title.equals("Order Added")){
                mNotificationManager = new MyNotificationManager(getApplicationContext(), true);
                /*String distance = data.getString("distance");
                String offerorId = data.getString("offeror_id");*/

                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                /*intent.putExtra("distance", distance);
                intent.putExtra("offeror_id", offerorId);*/
            }else if(title.equals("Order status changed")){
                mNotificationManager = new MyNotificationManager(getApplicationContext(), true);
                //String distance = data.getString("distance");
                String orderId = data.getString("order_id");

                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.putExtra("distance", distance);
                intent.putExtra("order_id", orderId);
            }else if(title.equals("Order Canceled")){
                mNotificationManager = new MyNotificationManager(getApplicationContext(), true);
                //String distance = data.getString("distance");
                String orderId = data.getString("order_id");

                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //intent.putExtra("distance", distance);
                intent.putExtra("order_id", orderId);
            }


            //displaying small notification
            mNotificationManager.showSmallNotification(title, message, intent);

        } catch (JSONException e) {
            Log.e(TAG, "Json Exception: " + e.getMessage());
        } catch (Exception e) {
            Log.e(TAG, "Exception: " + e.getMessage());
        }
    }

}
