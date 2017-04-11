package app.anish.com.tapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import app.anish.com.tapp.fragments.QRCodeDisplayFragment;
import app.anish.com.tapp.fragments.QRCodeScanFragment;

/**
 * Created by akhattar on 4/11/17.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    private static final String [] PAGE_TITLES = {"Show QR Code", "Scan"};

    private final Fragment [] fragments = {new QRCodeDisplayFragment(), new QRCodeScanFragment()};


    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public int getCount() {
        return PAGE_TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return PAGE_TITLES[position];
    }
}
