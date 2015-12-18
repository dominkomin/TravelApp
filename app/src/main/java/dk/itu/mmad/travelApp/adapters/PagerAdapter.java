package dk.itu.mmad.travelApp.adapters;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by domi on 26-04-2015.
 */
public class PagerAdapter extends FragmentPagerAdapter
{

	private final List<Fragment> mFragments = new ArrayList<Fragment>();
	public PagerAdapter(FragmentManager manager) {
		super(manager);
	}

	public void addFragment(Fragment fragment) {
		mFragments.add(fragment);
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mFragments.size();
	}

	@Override
	public Fragment getItem(int position) {
		return mFragments.get(position);
	}
}