package com.atta.cookhouse.main;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.atta.cookhouse.model.APIService;
import com.atta.cookhouse.model.APIUrl;
import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.Dishes;
import com.atta.cookhouse.model.DishesAdapter;
import com.squareup.okhttp.ResponseBody;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MainPresenter implements MainContract.Presenter {


    private MainContract.View mView;

    private Context mContext;

    Bitmap bitmap ;

    public MainPresenter(MainContract.View view, Context context) {

        mView = view;

        mContext = context;
    }
    @Override
    public void getRetrofitImage(final ImageView imageView, String url) {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.Images_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);

        Call<ResponseBody> call = service.getImageDetails(url);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Response<ResponseBody> response, Retrofit retrofit) {

                try {

                    Log.d("onResponse", "Response came from server");

                    bitmap = DownloadImage(response.body());

                    imageView.setImageBitmap(bitmap);

                    Log.d("onResponse", "Image is downloaded and saved ? ");

                } catch (Exception e) {
                    Log.d("onResponse", "There is an error");
                    e.printStackTrace();
                    bitmap = null;
                }

            }

            @Override
            public void onFailure(Throwable t) {
                Log.d("onFailure", t.toString());
                mView.showError(t.getMessage());
                bitmap = null;
            }
        });

    }

    @Override
    public Bitmap DownloadImage(ResponseBody body) {

        Bitmap bitmap;

        try {
            Log.d("DownloadImage", "Reading and writing file");
            InputStream in = null;
            FileOutputStream out = null;

            try {
                in = body.byteStream();
                bitmap = BitmapFactory.decodeStream(in);
                int c;

                while ((c = in.read()) != -1) {
                    out.write(c);
                }
            } catch (IOException e) {
                Log.d("DownloadImage", e.toString());
                return null;
            } finally {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            }

            int width, height;
            //Bitmap bMap = BitmapFactory.decodeFile(getExternalFilesDir(null) + File.separator + "AndroidTutorialPoint.jpg");
            width = 2 * bitmap.getWidth();
            height = 6 * bitmap.getHeight();
            Bitmap bMap2 = Bitmap.createScaledBitmap(bitmap, width, height, false);

            return bitmap;

        } catch (IOException e) {
            Log.d("DownloadImage", e.toString());
            return null;
        }
    }

    @Override
    public void getMenu(final RecyclerView recyclerView, String type) {

        ArrayList<Dish> dishes = null;


        //building retrofit object
        retrofit2.Retrofit retrofit = new retrofit2.Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);

        //Defining the user object as we need to pass it with the call
        //User user = new User(name, email, password, phone, birthdayString, locationSting);

        //defining the call
        retrofit2.Call<Dishes> call = service.getMenu( type );

        //calling the api
        call.enqueue(new retrofit2.Callback<Dishes>() {
            @Override
            public void onResponse(retrofit2.Call<Dishes> call, retrofit2.Response<Dishes> response) {

                ArrayList<Dish> dishes = response.body().getDishes();

                DishesAdapter myAdapter = new DishesAdapter(mContext, new MainPresenter(mView, mContext), dishes);
                recyclerView.setLayoutManager(new GridLayoutManager(mContext,2));
                recyclerView.setAdapter(myAdapter);
            }

            @Override
            public void onFailure(retrofit2.Call<Dishes> call, Throwable t) {

                mView.showError(t.getMessage());
            }
        });

    }
}
