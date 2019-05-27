package com.atta.cookhouse.addresses;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atta.cookhouse.Local.RecyclerItemTouchHelper;
import com.atta.cookhouse.newaddress.NewAddressActivity;
import com.atta.cookhouse.R;
import com.atta.cookhouse.model.Address;
import com.atta.cookhouse.model.AddressesAdapter;
import com.atta.cookhouse.model.DishesLinearAdapter;
import com.atta.cookhouse.model.SessionManager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class AddressesActivity extends AppCompatActivity implements AddressesContract.View, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    AddressesPresenter addressesPresenter;

    RecyclerView recyclerView;

    SessionManager sessionManager;

    TextView infoTextView;

    AddressesAdapter myAdapter;

    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addresses);


        recyclerView = findViewById(R.id.my_add_recycler);
        relativeLayout = findViewById(R.id.relativeLayout);

        infoTextView = findViewById(R.id.my_addresses_info_tv);


        addressesPresenter = new AddressesPresenter(this, this);

        sessionManager = new SessionManager(this);



        if (sessionManager.isLoggedIn()){

            addressesPresenter.getAddresses(sessionManager.getUserId());

            final SwipeRefreshLayout mySwipeRefreshLayout = findViewById(R.id.my_add_refresh);

            mySwipeRefreshLayout.setOnRefreshListener(
                    () -> {
                        addressesPresenter.getAddresses(sessionManager.getUserId());
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
            );
        }else {
            infoTextView.setText("You need to login to see your favorites");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        addNewAddress();
        if (id == R.id.add) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }

    @Override
    public void showRecyclerView(ArrayList<Address> addresses) {

        recyclerView.setVisibility(View.VISIBLE);
        infoTextView.setVisibility(View.GONE);
        myAdapter = new AddressesAdapter(this, addresses);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(myAdapter);
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof DishesLinearAdapter.MyViewHolder) {
            // get the removed item name to display it in snack bar

            AtomicBoolean undo = new AtomicBoolean(false);

            List<Address> addresses = myAdapter.getList();

            String name = addresses.get(viewHolder.getAdapterPosition()).getAddressName();

            // backup of removed item for undo purpose
            final Address deletedItem = addresses.get(viewHolder.getAdapterPosition());
            final int deletedIndex = viewHolder.getAdapterPosition();

            // remove the item from recycler view
            myAdapter.removeItem(viewHolder.getAdapterPosition());

            // showing snack bar with Undo option
            Snackbar snackbar = Snackbar
                    .make(relativeLayout, name + " removed from cart!", Snackbar.LENGTH_LONG);
            snackbar.setAction("UNDO", view -> {

                undo.set(true);
                // undo is selected, restore the deleted item
                myAdapter.restoreItem(deletedItem, deletedIndex);
            });
            snackbar.addCallback(new Snackbar.Callback() {

                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (!undo.get()){
                        addressesPresenter.removeAddresses(deletedItem.getId());
                    }

                }

                @Override
                public void onShown(Snackbar snackbar) {
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    @Override
    public void updateText() {

        recyclerView.setVisibility(View.GONE);
        infoTextView.setVisibility(View.VISIBLE);
        infoTextView.setText("No addresses added yet");
    }

    @Override
    public void addNewAddress() {

        Intent intent = new Intent(AddressesActivity.this, NewAddressActivity.class);
        startActivity(intent);
    }
}
