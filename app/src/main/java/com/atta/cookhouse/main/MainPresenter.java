package com.atta.cookhouse.main;

import android.content.Context;
import android.os.AsyncTask;

import com.andremion.counterfab.CounterFab;
import com.atta.cookhouse.Local.DatabaseClient;
import com.atta.cookhouse.model.APIClient;
import com.atta.cookhouse.model.AddFavResult;
import com.atta.cookhouse.model.CartItem;
import com.atta.cookhouse.model.CategoriesResult;
import com.atta.cookhouse.model.FavResult;
import com.atta.cookhouse.model.OptionsResult;
import com.atta.cookhouse.model.PointsResult;
import com.atta.cookhouse.model.Result;
import com.atta.cookhouse.model.SessionManager;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    public void addToFav(int dishId, int userId) {


        //defining the call
        Call<AddFavResult> call = APIClient.getInstance().getApi().addToFavorite(dishId, userId);

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
    public void getPoints(int userId) {

        //defining the call
        Call<PointsResult> call = APIClient.getInstance().getApi().getPoints(userId);

        //calling the api
        call.enqueue(new Callback<PointsResult>() {
            @Override
            public void onResponse(Call<PointsResult> call, Response<PointsResult> response) {
                //hiding progress dialog


                //displaying the message from the response as toast
                if (response.body() != null) {
                    //mView.showMessage(response.body().getMessage());
                    //if there is no error
                    if (!response.body().getError()) {
                        //starting Main activity
                        mView.changeFavIcon(true);
                        mView.setPoints(response.body().getPoints());
                    }else {
                        mView.showMessage(response.body().getMessage());
                    }
                }
            }

            @Override
            public void onFailure(Call<PointsResult> call, Throwable t) {

                mView.showMessage(t.getMessage());
            }
        });
    }


    @Override
    public void getCategories() {

        //defining the call
        Call<CategoriesResult> call = APIClient.getInstance().getApi().getCategories();

        //calling the api
        call.enqueue(new Callback<CategoriesResult>() {
            @Override
            public void onResponse(Call<CategoriesResult> call, Response<CategoriesResult> response) {
                //hiding progress dialog


                //displaying the message from the response as toast
                if (response.body() != null) {
                    //mView.showMessage(response.body().getMessage());
                    //if there is no error
                    if (!response.body().getError()) {
                        //starting Main activity
                        mView.changeFavIcon(true);
                        mView.setVewPager(response.body().getCategories());
                    }
                }else {
                    mView.showMessage("something wrong, try again ");
                }

                getOptions();
            }

            @Override
            public void onFailure(Call<CategoriesResult> call, Throwable t) {

                mView.showMessage(t.getMessage());
                getOptions();
            }
        });
    }



    @Override
    public void getOptions() {

        //defining the call
        Call<OptionsResult> call = APIClient.getInstance().getApi().getoOptions();

        //calling the api
        call.enqueue(new Callback<OptionsResult>() {
            @Override
            public void onResponse(Call<OptionsResult> call, Response<OptionsResult> response) {
                //hiding progress dialog


                //displaying the message from the response as toast
                if (response.body() != null) {
                    //mView.showMessage(response.body().getMessage());
                    //if there is no error
                    if (!response.body().getError()) {
                        //starting Main activity
                        mView.changeFavIcon(true);
                        mView.setOptions(response.body().getOptions());
                    }
                }else {
                    mView.showMessage("something wrong, try again ");
                }

                getPoints(SessionManager.getInstance(mContext).getUserId());
            }

            @Override
            public void onFailure(Call<OptionsResult> call, Throwable t) {

                mView.showMessage(t.getMessage());
                getPoints(SessionManager.getInstance(mContext).getUserId());
            }
        });
    }

    @Override
    public void removeFromFav(int fId) {
        //QueryUtils.removeFromFav(fId, mView, null);
    }

    @Override
    public void checkIfFav(int dishId, int userId) {

        //defining the call
        Call<FavResult> call = APIClient.getInstance().getApi().checkIfFavorite(dishId, userId);

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

    @Override
    public void saveToken(String token, int userId) {

        //defining the call
        Call<Result> call = APIClient.getInstance().getApi().saveToken(token, userId);

        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {


                //displaying the message from the response as toast
                if (response.body() != null){

                    if (!response.body().getError()){
                        mView.showMessage(response.body().getMessage());
                    }

                }

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {

                mView.showMessage(t.getMessage());
            }
        });
    }
}


