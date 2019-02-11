package com.atta.cookhouse;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.atta.cookhouse.fragments.MainDishFragment;
import com.atta.cookhouse.fragments.SaladsFragment;
import com.atta.cookhouse.fragments.SoupFragment;

public class ViewFragmentPagerAdapter extends FragmentPagerAdapter {

    /** Context of the app */
    private Context mContext;

    public ViewFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return new MainDishFragment();
        } else if (position == 1) {
            return new SaladsFragment();
        } else {
            return new SoupFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return mContext.getString(R.string.category_main);
        } else if (position == 1) {
            return mContext.getString(R.string.category_salads);
        } else{
            return mContext.getString(R.string.category_soups);
        }
    }
}
