package com.atta.cookhouse.cart;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.atta.cookhouse.Local.DatabaseClient;
import com.atta.cookhouse.Local.QueryUtils;
import com.atta.cookhouse.R;
import com.atta.cookhouse.model.APIService;
import com.atta.cookhouse.model.APIUrl;
import com.atta.cookhouse.model.Address;
import com.atta.cookhouse.model.Addresses;
import com.atta.cookhouse.model.CartAdapter;
import com.atta.cookhouse.model.CartItem;
import com.atta.cookhouse.model.Order;
import com.atta.cookhouse.model.OrdersResult;
import com.atta.cookhouse.model.PromoCodeResult;

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

    private RecyclerView mSummaryRecyclerView;


    private ProgressDialog mProgressDialog;

    public CartPresenter(CartContract.View view, Context context, RecyclerView recyclerView, RecyclerView summaryRecyclerView, ProgressDialog progressDialog) {

        mView = view;

        mContext = context;

        mRecyclerView = recyclerView;

        mSummaryRecyclerView = summaryRecyclerView;

        mProgressDialog = progressDialog;
    }

    @Override
    public void getCartItems(final boolean view, @Nullable final int userId, @Nullable final String location,
                             final int deliveryAdd, @Nullable final String mobile, @Nullable final String schedule, @Nullable final String orderTime, @Nullable final double discountAmount) {


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

                double totalPrice = totalPriceCalculation(cartItems);
                double deliveryPrice = 5;
                double discount = 5;

                if (view){

                    if (!cartItems.isEmpty()){
                        mView.enableOrderBtn();

                        CartAdapter adapter = new CartAdapter(mContext, cartItems, CartPresenter.this, mView, false);
                        mRecyclerView.setAdapter(adapter);


                        CartAdapter summaryAdapter = new CartAdapter(mContext, cartItems, CartPresenter.this, mView, true);
                        mSummaryRecyclerView.setAdapter(summaryAdapter);
                        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mContext,
                                LinearLayoutManager.VERTICAL);
                        dividerItemDecoration.setDrawable(mContext.getResources().getDrawable(R.drawable.line_divider));
                        mSummaryRecyclerView.addItemDecoration(dividerItemDecoration);


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
/*

                    boolean m1 = false, m2 = false, m3 = false;

                    for (CartItem element : cartItems) {

                        switch (element.getKitchen()){
                            case 0:
                                m1 = true;
                                break;
                            case 1:
                                m2 = true;
                                break;
                            case 2:
                                m3 = true;
                                break;
                        }
                    }

                    int kitchen = 0;

                    if (m1 & !m2 & !m3)
                        kitchen = 0;
                    else if (!m1 & m2 & !m3)
                        kitchen = 1;
                    else if (!m1 & !m2 & m3)
                        kitchen = 2;
                    else if (m1 & m2 & !m3)
                        kitchen = 3;
                    else if (m1 & !m2 & m3)
                        kitchen = 4;
                    else if (!m1 & m2 & m3)
                        kitchen = 5;
                    else if (m1 & m2 & m3)
                        kitchen = 6;
*/

                    double total = totalPrice+deliveryPrice-discountAmount;

                    Order order = new Order(dishes, count,  totalPrice, deliveryPrice , total
                            , discountAmount, userId, location, deliveryAdd, mobile,  schedule, orderTime, creationTime);

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
                order.getAddressId(),
                order.getMobile(),
                order.getUserId(),
                order.getSchedule(),
                order.getOrderTime(),
                order.getCreationTime(),
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

                if (response.body() != null) {

                    //displaying the message from the response as toast
                    mView.showMessage(response.body().getMessage());
                    //if there is no error
                    if (!response.body().getError()) {
                        //starting Main activity
                        removeCartItems();
                        mView.navigateToMain();
                    }

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


    @Override
    public void getAddresses(int userId) {

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);

        //Defining the user object as we need to pass it with the call
        //User user = new User(name, email, password, phone, birthdayString, locationSting);

        //defining the call
        Call<Addresses> call = service.getAddresses(userId);

        //calling the api
        call.enqueue(new Callback<Addresses>() {
            @Override
            public void onResponse(Call<Addresses> call, Response<Addresses> response) {

                if (response.body() != null){
                    if (!response.body().getError()){

                        List<Address> addresses = response.body().getAddresses();

                        if (addresses.size() > 0){

                            mView.showAddresses(addresses);
                        }

                    }else {
                        mView.showAddressesMessage(response.body().getMessage());
                    }
                }else {
                    mView.showAddressesMessage("something wrong, please try again");
                }

            }

            @Override
            public void onFailure(Call<Addresses> call, Throwable t) {

                mView.showMessage(t.getMessage());
            }
        });
    }


    @Override
    public void checkPromoCode(int userId, String promoCode) {

        //building retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Defining retrofit api service
        APIService service = retrofit.create(APIService.class);

        //Defining the user object as we need to pass it with the call
        //User user = new User(name, email, password, phone, birthdayString, locationSting);

        //defining the call
        Call<PromoCodeResult> call = service.checkPromoCode(userId, promoCode);

        //calling the api
        call.enqueue(new Callback<PromoCodeResult>() {
            @Override
            public void onResponse(Call<PromoCodeResult> call, Response<PromoCodeResult> response) {

                if (response.body() != null){
                    if (!response.body().getError()){

                        mView.showMessage(response.body().getMessage());
                        mView.setDiscount(response.body().getDiscount());

                    }else {
                        mView.showMessage(response.body().getMessage());
                    }
                }else {
                    mView.showMessage("something wrong, please try again");
                }

            }

            @Override
            public void onFailure(Call<PromoCodeResult> call, Throwable t) {

                mView.showMessage(t.getMessage());
            }
        });
    }
}
