package com.atta.cookhouse.Local;

import android.content.Context;
import android.os.AsyncTask;

import com.andremion.counterfab.CounterFab;
import com.atta.cookhouse.cart.CartContract;
import com.atta.cookhouse.favorites.FavoritesContract;
import com.atta.cookhouse.fragments.FragmentsContract;
import com.atta.cookhouse.login.LoginContract;
import com.atta.cookhouse.model.APIService;
import com.atta.cookhouse.model.APIClient;
import com.atta.cookhouse.model.CartItem;
import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.Result;
import com.atta.cookhouse.model.SessionManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class QueryUtils {

    public static void updateCount(final CartItem cartItem, final Context mContext, int count) {

        int itemCount = cartItem.getCount();

        cartItem.setCount(itemCount + count);

        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //adding to database
                DatabaseClient.getInstance(mContext).getAppDatabase()
                        .itemDao()
                        .update(cartItem);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //mView.openCart();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();

    }

    public static void updateCount(final CartItem cartItem, final Context mContext, boolean add) {

        final int count;

        if (add){
            count = cartItem.getCount() + 1;
        }else{
            count = cartItem.getCount();
        }

        cartItem.setCount(count);

        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //adding to database
                DatabaseClient.getInstance(mContext).getAppDatabase()
                        .itemDao()
                        .update(cartItem);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //mView.openCart();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();

    }

    public static void checkCartItem(final Dish dish, final String price, final int count,
                                     final Context mContext, final CounterFab mCounterFab) {


        class GetTasks extends AsyncTask<Void, Void, CartItem> {

            @Override
            protected CartItem doInBackground(Void... voids) {
                return DatabaseClient
                        .getInstance(mContext)
                        .getAppDatabase()
                        .itemDao()
                        .getItem(dish.getDishName(), dish.getSelectedOption(), dish.getSelectedSide1(),
                                dish.getSelectedSide2(), dish.getSelectedSize());
            }

            @Override
            protected void onPostExecute(CartItem cartItem) {
                super.onPostExecute(cartItem);

                //itemInserted[0] = id > -1 ;

                if (cartItem != null){
                    updateCount(cartItem, mContext, count);
                }else {

                    addToCart(dish, count, price, mContext);

                    if (mCounterFab != null){

                        mCounterFab.increase();
                    }
                }

            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();

    }

    public static void addToCart(final Dish dish, final int count,
                                 final String dishPrice, final Context mContext) {
        class SaveTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {

                //creating a task
                CartItem cartItem = new CartItem();
                cartItem.setDishId(dish.getDishId());
                cartItem.setDishName(dish.getDishName());
                cartItem.setCount(count);
                cartItem.setDishPrice(dishPrice);
                cartItem.setKitchen(0);
                cartItem.setOption(dish.getSelectedOption());
                cartItem.setSide1(dish.getSelectedSide1());
                cartItem.setSide2(dish.getSelectedSide2());
                cartItem.setSize(dish.getSelectedSize());
                cartItem.setEta(dish.getEta());

                //adding to database
                DatabaseClient.getInstance(mContext).getAppDatabase()
                        .itemDao()
                        .insert(cartItem);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //mView.openCart();
            }
        }

        SaveTask st = new SaveTask();
        st.execute();
    }


    public static void removeCartItems(final Context mContext, final CartContract.View mView) {
        class DeleteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(mContext).getAppDatabase()
                        .itemDao()
                        .deleteAll();
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                //Toast.makeText(mContext, "Deleted", Toast.LENGTH_LONG).show();
                if (mView != null){

                    mView.backToMain();
                }
            }
        }

        DeleteTask dt = new DeleteTask();
        dt.execute();
    }

    public static void login(String email, String password, final LoginContract.View loginView, final CartContract.View cartView
            , final Context mContext) {


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService service = retrofit.create(APIService.class);


        Call<Result> call = service.userLogin(email, password);

        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                if (loginView != null && cartView == null){

                    loginView.dismissProgressDialog();
                    if (!response.body().getError()) {
                        loginView.showMessage();
                        SessionManager.getInstance(mContext).setLanguage(SessionManager.getInstance(mContext).getLanguage());
                        SessionManager.getInstance(mContext).createLoginSession(response.body().getUser());
                        loginView.navigateToMain();
                    } else {

                        loginView.showMessage("Invalid email or password");
                    }
                }else if (loginView == null && cartView != null){

                    cartView.dismissProgressDialog();
                    if (!response.body().getError()) {
                        cartView.showMessage("Login successfully");
                        SessionManager.getInstance(mContext).setLanguage(SessionManager.getInstance(mContext).getLanguage());
                        SessionManager.getInstance(mContext).createLoginSession(response.body().getUser());
                    } else {

                        cartView.showMessage("Invalid email or password");
                    }
                }

            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                if (loginView != null && cartView == null){

                    loginView.dismissProgressDialog();

                    loginView.showMessage(t.getMessage());
                }else if (loginView == null && cartView != null){

                    cartView.dismissProgressDialog();

                    cartView.showMessage(t.getMessage());
                }


            }
        });

    }

    public static boolean validate(String email, String password, final LoginContract.View loginView, final CartContract.View cartView, final Context mContex) {

        boolean valid = true;
        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";


        if (loginView != null && cartView == null){

            if (!email.matches(emailPattern) || email.isEmpty())
            {
                loginView.showMessage("Invalid email address");
                loginView.showViewError("email","Invalid email address");
                valid = false;

            }else {

                loginView.showViewError("email",null);
            }

            if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
                loginView.showMessage("password must be between 4 and 10 alphanumeric characters");
                loginView.showViewError("password","password must be between 4 and 10 alphanumeric characters");
                valid = false;
            } else {
                loginView.showViewError("password",null);
            }
        }else if (loginView == null && cartView != null){

            if (!email.matches(emailPattern) || email.isEmpty())
            {
                cartView.showMessage("Invalid email address");
                valid = false;

            }

            if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
                cartView.showMessage("password must be between 4 and 10 alphanumeric characters");
                valid = false;
            }
        }


        return valid;
    }



    public static void removeFromFav(int fId, final FragmentsContract.View mainView, final FavoritesContract.View favView) {

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);


        //defining the call
        Call<Result> call = service.removeFromFavorite(fId);

        //calling the api
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, retrofit2.Response<Result> response) {
                //hiding progress dialog


                //displaying the message from the response as toast
                if (response.body() != null) {
                    if (mainView != null && favView == null){

                        mainView.showMessage(response.body().getMessage());
                        //if there is no error
                        if (!response.body().getError()) {
                            //starting Main activity
                            mainView.changeFavIcon(false);
                        }
                    }else if (favView != null && mainView == null){

                        favView.showMessage(response.body().getMessage());

                    }
                }
            }

            @Override
            public void onFailure(Call<Result> call, Throwable t) {
                if (mainView != null && favView == null){

                    mainView.showMessage(t.getMessage());
                }else if (favView != null && mainView == null){

                    favView.showMessage(t.getMessage());
                }
            }
        });
    }
}
