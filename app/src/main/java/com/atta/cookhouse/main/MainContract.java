package com.atta.cookhouse.main;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;

import com.squareup.okhttp.ResponseBody;

public interface MainContract {

    interface View{

        void showError(String error);

        void ViewImage(Bitmap bitmap);
    }

    interface Presenter{

        void getRetrofitImage(final ImageView imageView, String url) ;


        Bitmap DownloadImage(ResponseBody body);

        void getMenu(final RecyclerView recyclerView, String type);
    }
}
