package com.atta.cookhouse.model;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mosta on 2/28/2018.
 */

public class APIClient {


    public static final String BASE_URL = "http://52.15.188.41/cookhouse/public/index.php/";

    public static final String Images_BASE_URL = "http://52.15.188.41/cookhouse/images/";

    public static final String SMS_API_BASE_URL = "http://52.15.188.41/cookhouse/nexmo/";

    private static APIClient mInstance;
    private Retrofit retrofit;

    //here we created a constructor that will build the retrofit object
    private APIClient(){


        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();
    }

//now we will need an instance from that retrofit client

    public static APIClient getInstance(){
        if (mInstance == null){
            mInstance = new APIClient();
        }
        return mInstance;
    }

    // now we need one more method to get the api
    public APIService getApi(){
        return retrofit.create(APIService.class);
    }

}
