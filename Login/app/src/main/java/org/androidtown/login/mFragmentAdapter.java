package org.androidtown.login;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class mFragmentAdapter extends FragmentPagerAdapter {
    ArrayList<Fragment> flist = new ArrayList<Fragment>();
    String[] titles;
    static int[] imagelist;
    mainui context;
    static ImageView imageView;


    public mFragmentAdapter(FragmentManager fm, ArrayList<Fragment>f, String[]t,int[] imagelist,mainui context)
    {
        super(fm);
        flist = f;
        titles = t;
        this.imagelist = imagelist;
        this.context = context;
        imageView = context.findViewById(R.id.images);

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        imageView.setImageResource(imagelist[position]);
    }

    @Override
    public Fragment getItem(int i)
    {

        return flist.get(i);
    }


    @Override
    public int getCount() {

        return flist.size();
    }

}
