package com.atta.cookhouse.Local;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.atta.cookhouse.R;
import com.atta.cookhouse.fragments.DishFragment;

import java.util.Arrays;
import java.util.List;

public class ViewFragmentPagerAdapter extends FragmentPagerAdapter {

    /** Context of the app */
    private Context mContext;

    List<String> categories;

    public ViewFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;


        String[] categories = mContext.getResources().getStringArray(R.array.categories);


        this.categories = Arrays.asList(categories);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new DishFragment();
        Bundle bundle = new Bundle();

        bundle.putString("category", categories.get(position));

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return categories.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return categories.get(position);
    }
}
