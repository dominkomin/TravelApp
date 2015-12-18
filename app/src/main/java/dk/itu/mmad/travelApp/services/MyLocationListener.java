package dk.itu.mmad.travelApp.services;

import android.app.Activity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by domi on 11-03-2015.
 */
public class MyLocationListener implements LocationListener
{

	private final String provider;
	private final Activity parent;
	private Location location;

	public MyLocationListener(String provider, Activity parent)
	{
		this.provider = provider;
		this.parent = parent;
	}

	public Location getLocation()
	{
		return location;
	}

	@Override
	public void onLocationChanged(Location location)
	{
		this.location = location;
	}

	@Override
	public void onProviderDisabled(String provider)
	{
		Toast.makeText(parent, provider + " disabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onProviderEnabled(String provider)
	{
		Toast.makeText(parent, provider + " enabled", Toast.LENGTH_SHORT).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras)
	{
		String statusStr = "";
		switch (status)
		{
			case LocationProvider.AVAILABLE:
				statusStr = "available";
				break;
			case LocationProvider.TEMPORARILY_UNAVAILABLE:
				statusStr = "temporarily available";
				break;
			case LocationProvider.OUT_OF_SERVICE:
				statusStr = "out of service";
				break;
		}
		Toast.makeText(parent, provider + " status changed to " + statusStr, Toast.LENGTH_SHORT).show();
	}

}
