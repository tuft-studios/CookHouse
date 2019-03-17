package com.atta.cookhouse.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.andremion.counterfab.CounterFab;
import com.atta.cookhouse.R;
import com.atta.cookhouse.cart.CartActivity;
import com.atta.cookhouse.main.MainActivity;
import com.atta.cookhouse.model.Dish;
import com.atta.cookhouse.model.DishesAdapter;
import com.atta.cookhouse.model.SessionManager;

import java.util.ArrayList;

public class MainDishFragment extends Fragment implements FragmentsContract.View {

    ImageView imageView;

    FragmentsPresenter fragmentsPresenter;

    RecyclerView recyclerView;

    CounterFab counterFab;

    String location= "any";

    BroadcastReceiver mReceiver;

    IntentFilter filter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //View rootView = inflater.inflate(R.layout.dishes_grid, container, false);


        counterFab = (CounterFab) getActivity().findViewById(R.id.fab);



        fragmentsPresenter = new FragmentsPresenter(this, getContext(), counterFab);
        //return rootView;
        View view = inflater.inflate(R.layout.grid_view,container,false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        getMenu();


        final SwipeRefreshLayout mySwipeRefreshLayout = view.findViewById(R.id.swiperefresh);

        mySwipeRefreshLayout.setOnRefreshListener(
                () -> {
                    getMenu();
                    mySwipeRefreshLayout.setRefreshing(false);
                }
        );

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if ("action_location_updated".equals(intent.getAction())) {
                    getMenu();
                }
            }
        };
        filter = new IntentFilter("action_location_updated");

        //getActivity().registerReceiver(mReceiver, filter);

        return view;
    }

    @Override
    public void onPause() {

        getContext().unregisterReceiver(mReceiver);
        super.onPause();
    }


    @Override
    public void onResume() {
        getActivity().registerReceiver(mReceiver, filter);
        super.onResume();
    }

    public void getMenu(){

        if (fragmentsPresenter == null){

            fragmentsPresenter = new FragmentsPresenter(this, getContext(), counterFab);
        }
        if (SessionManager.getInstance(getContext()).isLoggedIn()){
            location = SessionManager.getInstance(getContext()).getOrderLocation();
        }

        fragmentsPresenter.getMenu(recyclerView, "main dish", location);
    }



    @Override
    public void showError(String error) {

        Toast.makeText(getContext(),error,Toast.LENGTH_LONG).show();
    }

    @Override
    public void ViewImage(Bitmap bitmap) {
        if (bitmap != null){

            imageView.setImageBitmap(bitmap);
        }else {
            showError("can't retrieve the image");
        }
    }

    @Override
    public void openCart() {

        startActivity(new Intent(getContext(), CartActivity.class));
        //Toast.makeText(getContext(), "Saved", Toast.LENGTH_LONG).show();
    }

    @Override
    public void showOrderDialog(Dish dish) {

        ((MainActivity)getActivity()).showOrderDialog(dish, fragmentsPresenter, recyclerView, "main dish", location);

    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu, menuInflater);


    }

    @Override
    public void showRecyclerView(ArrayList<Dish> dishes) {

        DishesAdapter myAdapter = new DishesAdapter(this, dishes, getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerView.setAdapter(myAdapter);

    }
}
