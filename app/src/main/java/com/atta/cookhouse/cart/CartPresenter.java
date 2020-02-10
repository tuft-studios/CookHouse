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
import com.atta.cookhouse.model.APIClient;
import com.atta.cookhouse.model.Address;
import com.atta.cookhouse.model.Addresses;
import com.atta.cookhouse.model.CartAdapter;
import com.atta.cookhouse.model.CartItem;
import com.atta.cookhouse.model.Order;
import com.atta.cookhouse.model.OrdersResult;
import com.atta.cookhouse.model.PromoCodeResult;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                             final int deliveryAdd, @Nullable final String mobile, @Nullable final String orderTime,
                             @Nullable final double discountAmount, @Nullable final String promocode, final int numOfPoints) {


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

                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.ENGLISH);

                    String creationTime = sdf.format(new Date());

                    //ArrayList<String> dishes = new ArrayList<>(), count = new ArrayList<>();

                    StringBuilder dishesBuilder = new StringBuilder(), countBuilder = new StringBuilder(),
                            optionstBuilder = new StringBuilder(), sides1Builder = new StringBuilder(),
                            sides2Builder = new StringBuilder(), sizeBuilder = new StringBuilder();

                    int eta = 0;

                    for (int i = 0; i <  cartItems.size(); i++) {
                        dishesBuilder.append(cartItems.get(i).getDishId());
                        countBuilder.append(cartItems.get(i).getCount());
                        optionstBuilder.append(cartItems.get(i).getOption());
                        sides1Builder.append(cartItems.get(i).getSide1());
                        sides2Builder.append(cartItems.get(i).getSide2());
                        sizeBuilder.append(cartItems.get(i).getSize());

                        if (i != (cartItems.size() -1)){
                            dishesBuilder.append(",");
                            countBuilder.append(",");
                            optionstBuilder.append(",");
                            sides1Builder.append(",");
                            sides2Builder.append(",");
                            sizeBuilder.append(",");
                        }

                        if (cartItems.get(i).getEta() > eta){

                            eta = cartItems.get(i).getEta();
                        }
                    }

                    eta += 30;

                    double total = totalPrice+deliveryPrice-discountAmount;

                    Order order = new Order(dishesBuilder.toString(), countBuilder.toString(),
                            optionstBuilder.toString(), sizeBuilder.toString(), sides1Builder.toString(),
                            sides2Builder.toString(), totalPrice, deliveryPrice, total, discountAmount,
                            userId, location, deliveryAdd, mobile,  orderTime, creationTime, eta, "test", promocode, numOfPoints);

                    addOrder(order);
                }
            }
        }

        GetTasks gt = new GetTasks();
        gt.execute();
    }

    @Override
    public double totalPriceCalculation(List<CartItem> cartItems) {
        double total = 0;

        for (CartItem cartItem : cartItems){
            total += (Double.valueOf(cartItem.getDishPrice())* cartItem.getCount());
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

        //defining the call
        Call<OrdersResult> call = APIClient.getInstance().getApi().addOrder(
                order.getDishes(),
                order.getCount(),
                order.getOptions(),
                order.getSides1(),
                order.getSides2(),
                order.getSizes(),
                order.getSubtotalPrice(),
                order.getDelivery(),
                order.getDiscount(),
                order.getTotalPrice(),
                order.getAddressId(),
                order.getMobile(),
                order.getUserId(),
                order.getOrderTime(),
                order.getCreationTime(),
                order.getLocation(),
                order.getEta(),
                order.getComment(),
                order.getPromocode(),
                order.getPoints()
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

        //defining the call
        Call<Addresses> call = APIClient.getInstance().getApi().getAddresses(userId);

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

        //defining the call
        Call<PromoCodeResult> call = APIClient.getInstance().getApi().checkPromoCode(userId, promoCode);

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

                        mView.wrongPromo();
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
