package com.atta.cookhouse.main;

import android.content.Context;
import android.os.AsyncTask;

import com.andremion.counterfab.CounterFab;
import com.atta.cookhouse.Local.DatabaseClient;
import com.atta.cookhouse.Local.QueryUtils;
import com.atta.cookhouse.model.APIService;
import com.atta.cookhouse.model.APIUrl;
import com.atta.cookhouse.model.AddFavResult;
import com.atta.cookhouse.model.CartItem;
import com.atta.cookhouse.model.FavResult;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainPresenter implements MainContract.Presenter {


    private MainContract.View mView;

    private Context mContext;

    private CounterFab mCounterFab;

    public MainPresenter(MainContract.View view, Context context, CounterFab counterFab) {

        mView = view;

        mContext = context;

        mCounterFab = counterFab;
    }

    @Override
    public void getCartItemsNum() {


        class GetTasks extends AsyncTask<Void, Void, List<CartItem>> {

        @Override
        protected List<CartItem> doInBackground(Void... voids) {
            List<CartItem> taskList = DatabaseClient
                    .getInstance(mContext)
                    .getAppDatabase()
                    .itemDao()
                    .getAll();
            return taskList;
        }

        @Override
        protected void onPostExecute(List<CartItem> cartItems) {
            super.onPostExecute(cartItems);

            int numOfCartItem = cartItems.size();

            mCounterFab.setCount(numOfCartItem);

        }
    }

    GetTasks gt = new GetTasks();
        gt.execute();
    }

    @Override
    public void addToFav(int propertyId, int userId) {

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);


        //defining the call
        Call<AddFavResult> call = service.addToFavorite(propertyId, userId);

        //calling the api
        call.enqueue(new Callback<AddFavResult>() {
            @Override
            public void onResponse(Call<AddFavResult> call, retrofit2.Response<AddFavResult> response) {
                //hiding progress dialog


                //displaying the message from the response as toast
                if (response.body() != null) {
                    mView.showMessage(response.body().getMessage());
                    //if there is no error
                    if (!response.body().getError()) {
                        //starting Main activity
                        mView.changeFavIcon(true);
                        mView.setFavId(response.body().getId());
                    }else {
                        mView.showMessage(response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<AddFavResult> call, Throwable t) {

                mView.showMessage(t.getMessage());
            }
        });
    }

    @Override
    public void removeFromFav(int fId) {
        QueryUtils.removeFromFav(fId, mView, null);
    }

    @Override
    public void checkIfFav(int propertyId, int userId) {

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);


        //defining the call
        Call<FavResult> call = service.checkIfFavorite(propertyId, userId);

        //calling the api
        call.enqueue(new Callback<FavResult>() {
            @Override
            public void onResponse(Call<FavResult> call, retrofit2.Response<FavResult> response) {


                //displaying the message from the response as toast
                if (response.body() != null){

                    mView.changeFavIcon(response.body().getFavorite());

                    if (response.body().getFavorite()){
                        mView.setFavId(response.body().getId());
                    }

                }

            }

            @Override
            public void onFailure(Call<FavResult> call, Throwable t) {

                mView.showMessage(t.getMessage());
            }
        });
    }
}


