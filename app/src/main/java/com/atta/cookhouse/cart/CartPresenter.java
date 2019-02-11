package com.atta.cookhouse.cart;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.atta.cookhouse.Local.DatabaseClient;
import com.atta.cookhouse.Local.QueryUtils;
import com.atta.cookhouse.model.APIService;
import com.atta.cookhouse.model.APIUrl;
import com.atta.cookhouse.model.CartAdapter;
import com.atta.cookhouse.model.CartItem;
import com.atta.cookhouse.model.Order;
import com.atta.cookhouse.model.OrdersResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CartPresenter implements CartContract.Presenter {


    private CartContract.View mView;

    private Context mContext;

    private RecyclerView mRecyclerView;


    private ProgressDialog mProgressDialog;

    public CartPresenter(CartContract.View view, Context context, RecyclerView recyclerView, ProgressDialog progressDialog) {

        mView = view;

        mContext = context;

        mRecyclerView = recyclerView;

        mProgressDialog = progressDialog;
    }

    @Override
    public void getCartItems(final boolean view, @Nullable final int userId, @Nullable final String location,
                             @Nullable final String deliveryAdd, @Nullable final String schedule, @Nullable final String orderTime) {
        int kitchen = 0;

        class GetTasks extends AsyncTask<Void, Void, List<CartItem>> {

            @Override
            protected List<CartItem> doInBackground(Void... voids) {
                List<CartItem> cartItems = DatabaseClient
                        .getInstance(mContext)
                        .getAppDatabase()
                        .itemDao()
                        .getAll();
                return cartItems;
            }

            @Override
            protected void onPostExecute(List<CartItem> cartItems) {
                super.onPostExecute(cartItems);

                int totalPrice = totalPriceCalculation(cartItems);
                int deliveryPrice = 5;
                int discount = 5;

                if (view){

                    if (!cartItems.isEmpty()){
                        mView.enableOrderBtn();

                        CartAdapter adapter = new CartAdapter(mContext, cartItems, CartPresenter.this, mView);
                        mRecyclerView.setAdapter(adapter);


                        mView.setTotalPrice(totalPrice, deliveryPrice);
                    }else {

                        mView.disableOrderBtn();
                    }
                }else {

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm");

                    String creationTime = sdf.format(new Date());

                    Map<String, String> dishes = new HashMap<String, String>(), count = new HashMap<String, String>();
                    for (CartItem element : cartItems) {
                        dishes.put("dishes[" + cartItems.indexOf(element) + "]", String.valueOf(element.getDishId()));
                        count.put("quantities[" + cartItems.indexOf(element) + "]", String.valueOf(element.getCount()));
                    }



                    Order order = new Order(dishes, count,  totalPrice, deliveryPrice , totalPrice+deliveryPrice
                            , discount, userId, location, kitchen, deliveryAdd, schedule, orderTime, creationTime);

                    addOrder(order);
                }
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

    @Override
    public int totalPriceCalculation(List<CartItem> cartItems) {
        int total = 0;

        for (CartItem cartItem : cartItems){
            total += (Integer.valueOf(cartItem.getDishPrice())* cartItem.getCount());
        }
        return total;
    }

    @Override
    public void removeCartItems() {

        QueryUtils.removeCartItems( mContext, mView);

        mView.disableOrderBtn();
    }

    @Override
    public void deleteCartItem(final CartItem cartItem) {
        class DeleteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                DatabaseClient.getInstance(mContext).getAppDatabase()
                        .itemDao()
                        .delete(cartItem);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mView.showMessage("Deleted");

            }
        }

        DeleteTask dt = new DeleteTask();
        dt.execute();
    }


    @Override
    public void updateCount(final CartItem cartItem, boolean add) {

        QueryUtils.updateCount(cartItem, mContext, add);

    }

    @Override
    public void addOrder(Order order) {

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        // set your desired log level
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …

        // add logging as last interceptor
        httpClient.addInterceptor(logging);  // <-- this is the important line!

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);


        //defining the call
        Call<OrdersResult> call = service.addOrder(
                order.getDishes(),
                order.getCount(),
                order.getSubtotalPrice(),
                order.getDelivery(),
                order.getDiscount(),
                order.getTotalPrice(),
                order.getAddress(),
                order.getUserId(),
                order.getSchedule(),
                order.getOrderTime(),
                order.getCreationTime(),
                order.getKitchen(),
                order.getLocation()
        );

        //calling the api
        call.enqueue(new Callback<OrdersResult>() {
            @Override
            public void onResponse(Call<OrdersResult> call, Response<OrdersResult> response) {
                //hiding progress dialog
                if(mProgressDialog != null || mProgressDialog.isShowing() ){
                    mProgressDialog.dismiss();
                }

                //displaying the message from the response as toast
                mView.showMessage(response.body().getMessage());
                //if there is no error
                if (!response.body().getError()) {
                    //starting Main activity
                    removeCartItems();
                    mView.navigateToMain();
                }
            }

            @Override
            public void onFailure(Call<OrdersResult> call, Throwable t) {
                if(mProgressDialog != null || mProgressDialog.isShowing() ){
                    mProgressDialog.dismiss();
                }
                mView.showMessage(t.getMessage());
            }
        });
    }


    @Override
    public void login(String email, String password) {


        QueryUtils.login( email,  password, null, mView, mContext);

    }

    @Override
    public boolean validate(String email, String password) {


        return QueryUtils.validate( email,  password,null, mView, mContext);

    }
}