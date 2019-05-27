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
            if(title.equals("Trip Matched")){
                mNotificationManager = new MyNotificationManager(getApplicationContext(), false);
                //intent = new Intent(getApplicationContext(), MatchedTripsActivity.class);
            }else if(title.equals("Request result")){
                mNotificationManager = new MyNotificationManager(getApplicationContext(), false);
                String requesterId = data.getString("requester_id");
                String offerorId = data.getString("offeror_id");
                String tripRequestId = data.getString("trip_request_id");
                String tripOfferId = data.getString("trip_offer_id");
                String status = data.getString("status");

                //intent = new Intent(getApplicationContext(), TripDetailsActivity.class);
                intent.putExtra("requesterId", requesterId);
                intent.putExtra("offerorId", offerorId);
                intent.putExtra("tripRequestId", tripRequestId);
                intent.putExtra("tripOfferId", tripOfferId);
                intent.putExtra("status", status);
            }else if(title.equals("Trip Started")){
                mNotificationManager = new MyNotificationManager(getApplicationContext(), false);
                intent = new Intent(getApplicationContext(), MainActivity.class);
            }else if(title.equals("Order Added")){
                mNotificationManager = new MyNotificationManager(getApplicationContext(), true);
                /*String distance = data.getString("distance");
                String offerorId = data.getString("offeror_id");*/

                intent = new Intent(getApplicationContext(), MainActivity.class);
                /*intent.putExtra("distance", distance);
                intent.putExtra("offeror_id", offerorId);*/
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
