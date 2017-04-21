package app.anish.com.tapp.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import app.anish.com.tapp.fragments.QRCodeDisplayFragment;
import app.anish.com.tapp.fragments.QRCodeScanFragment;

/**
 * Created by akhattar on 4/11/17.
 */

public class SectionsPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener{

    private static final int SCAN_FRAGMENT_INDEX = 1;
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


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == SCAN_FRAGMENT_INDEX) {
            fragments[SCAN_FRAGMENT_INDEX].setUserVisibleHint(true);
        } else {
            fragments[SCAN_FRAGMENT_INDEX].setUserVisibleHint(false);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
