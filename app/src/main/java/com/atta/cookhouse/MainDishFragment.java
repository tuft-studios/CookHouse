package com.atta.cookhouse;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.atta.cookhouse.main.MainContract;
import com.atta.cookhouse.main.MainPresenter;

public class MainDishFragment extends Fragment implements MainContract.View {

    ImageView imageView;

    MainPresenter mainPresenter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        //View rootView = inflater.inflate(R.layout.dishes_grid, container, false);

        mainPresenter = new MainPresenter(this, getContext());
        //return rootView;
        View view = inflater.inflate(R.layout.grid_view,container,false);
        final RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        mainPresenter.getMenu(recyclerView, "main dish");

        final SwipeRefreshLayout mySwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        mainPresenter.getMenu(recyclerView, "main dish");
                        mySwipeRefreshLayout.setRefreshing(false);
                    }
                }
        );


        return view;
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


}
