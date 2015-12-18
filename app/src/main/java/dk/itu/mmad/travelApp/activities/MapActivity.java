package dk.itu.mmad.travelApp.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import dk.itu.mmad.travelApp.R;


public class MapActivity extends ActionBarActivity implements OnMapReadyCallback
{
	LatLng selectedPosition = new LatLng(0, 0);

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		MapFragment mapFragment = (MapFragment) getFragmentManager()
				.findFragmentById(R.id.map);
		mapFragment.getMapAsync(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.MapMenuItem)
		{
			Intent intent = new Intent().putExtra(
					TravelActivity.SELECTED_STATION_LAT_LNG, selectedPosition
			);
			setResult(RESULT_OK, intent);
			finish();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onMapReady(final GoogleMap googleMap)
	{
		googleMap.setMyLocationEnabled(true);
		googleMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener()
		{
			@Override
			public void onMyLocationChange(Location location)
			{
				LatLng loc = new LatLng(location.getLatitude(), location.getLongitude());
				googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, 16.0f));
				googleMap.setOnMyLocationChangeListener(null);
			}
		});


		googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener()
		{
			@Override
			public void onMapClick(LatLng latLng)
			{
				googleMap.clear();
				selectedPosition = latLng;
				googleMap.addMarker(new MarkerOptions()
						.position(latLng)
						.title("My station"));
			}
		});
	}
}
