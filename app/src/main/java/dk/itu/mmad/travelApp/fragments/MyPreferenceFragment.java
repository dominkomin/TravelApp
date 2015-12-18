package dk.itu.mmad.travelApp.fragments;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import dk.itu.mmad.travelApp.R;
import dk.itu.mmad.travelApp.db.TravelDAO;


public class MyPreferenceFragment extends PreferenceFragment
{
	TravelDAO travelDAO;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferences);


		travelDAO = new TravelDAO(getActivity());
		travelDAO.open();

		Preference clearHistory = findPreference("clearHistory");
		clearHistory.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
		{
			@Override
			public boolean onPreferenceClick(Preference preference)
			{
				travelDAO.clearTravels();
				return true;
			}
		});

		Preference clearStations = findPreference("clearStations");
		clearStations.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener()
		{
			@Override
			public boolean onPreferenceClick(Preference preference)
			{
				travelDAO.clearStations();
				return true;
			}
		});
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		travelDAO.close();
	}
}
