package com.github.lion223.divinepizza.Adapters;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.github.lion223.divinepizza.Fragments.DrinksFragment;
import com.github.lion223.divinepizza.Fragments.PizzasFragment;
import com.github.lion223.divinepizza.Fragments.SaladsFragment;

public class MainTabsPagerAdapter extends FragmentPagerAdapter {

    String[] tabArray = new String[] {"Піци", "Салати", "Напитки"};

    public MainTabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabArray[position];
    }

    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        switch(position){
            case 0:
                fragment = new PizzasFragment();
                break;
            case 1:
                fragment = new SaladsFragment();
                break;
            case 2:
                fragment = new DrinksFragment();
                break;
        }
        return fragment;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
