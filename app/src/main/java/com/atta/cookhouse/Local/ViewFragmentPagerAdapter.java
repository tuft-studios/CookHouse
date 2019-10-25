package com.atta.cookhouse.Local;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.atta.cookhouse.fragments.DishFragment;
import com.atta.cookhouse.model.Category;

import java.util.ArrayList;
import java.util.List;

public class ViewFragmentPagerAdapter extends FragmentPagerAdapter {

    /** Context of the app */
    private Context mContext;

    List<String> categories, categoriesEnglish;

    ArrayList<Category> mCategories;

    public ViewFragmentPagerAdapter(FragmentManager fm, Context context, ArrayList<Category> categories) {
        super(fm);
        mContext = context;
        mCategories = categories;


        //String[] categories = mContext.getResources().getStringArray(R.array.categories);
        //String[] categoriesEnglish = mContext.getResources().getStringArray(R.array.categories_english);


        //this.categories = Arrays.asList(categories);
        //this.categoriesEnglish = Arrays.asList(categoriesEnglish);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = new DishFragment();
        Bundle bundle = new Bundle();

        bundle.putInt("category", mCategories.get(position).getId());

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getCount() {
        return mCategories.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mCategories.get(position).getCategory();
    }
}
