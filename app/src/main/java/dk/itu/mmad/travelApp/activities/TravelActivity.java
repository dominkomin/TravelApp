package dk.itu.mmad.travelApp.activities;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

import dk.itu.mmad.travelApp.services.CheckInOutService;
import dk.itu.mmad.travelApp.services.MyLocationListener;
import dk.itu.mmad.travelApp.R;
import dk.itu.mmad.travelApp.db.RegisteredTravel;
import dk.itu.mmad.travelApp.db.TravelDAO;


public class TravelActivity extends ActionBarActivity
{
	public static final String SELECTED_STATION_NAME = "selectedStationName";
	public static final String SELECTED_STATION_LAT_LNG = "selectedStationLatLng";

	private final String LAST_START = "lastStart";
	private final String LAST_DESTINATION = "lastDestination";
	private final String CHECK_IN_ENABLED = "checkInEnabled";
	private final String CHECK_OUT_ENABLED = "checkOutEnabled";
	private final String SELECT_IN_ENABLED = "selectInEnabled";
	private final String SELECT_OUT_ENABLED = "selectOutEnabled";
	private final String EDIT_IN_ENABLED = "editInEnabled";
	private final String EDIT_OUT_ENABLED = "editOutEnabled";
	private final String POSITION_IN_ENABLED = "positionInEnabled";
	private final String POSITION_OUT_ENABLED = "positionOutEnabled";

	public static final int SELECT_IN_BUTTON_CODE = 1;
	public static final int SELECT_OUT_BUTTON_CODE = 2;

	private final int SELECT_IN_POSITION_MAP = 3;
	private final int SELECT_OUT_POSITION_MAP = 4;

	TravelDAO travelDAO;

	private String lastStart = "";
	private String lastDestination = "";
	private Address startAddress;
	private Address destinationAddress;
	private float distance;

	CheckOutReceiver checkOutReceiver;

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		travelDAO.close();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Intent intent = new Intent(TravelActivity.this, SplashActivity.class);
		startActivity(intent);

		setContentView(R.layout.activity_travel);

		final Button checkInButton = (Button) findViewById(R.id.checkInButton);
		final Button checkOutButton = (Button) findViewById(R.id.checkOutButton);

		final EditText checkIn = (EditText) findViewById(R.id.checkInEdit);
		final EditText checkOut = (EditText) findViewById(R.id.checkOutEdit);

		final Button selectInButton = (Button) findViewById(R.id.selectInButton);
		final Button selectOutButton = (Button) findViewById(R.id.selectOutButton);

		final ImageView trainImage = (ImageView) findViewById(R.id.trainImage);

		final Button positionInButton = (Button) findViewById(R.id.positionInButton);
		final Button positionOutButton = (Button) findViewById(R.id.positionOutButton);

		travelDAO = new TravelDAO(TravelActivity.this);
		travelDAO.open();

		checkInButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				if (checkIn.getText().length() == 0)
				{
					Toast.makeText(getApplicationContext(), "Fill check-in point", Toast.LENGTH_LONG).show();
					playSound(RingtoneManager.TYPE_NOTIFICATION);
				}
				else
				{
					playSound(RingtoneManager.TYPE_NOTIFICATION);

					checkInButton.setEnabled(false);
					checkIn.setEnabled(false);
					positionInButton.setEnabled(false);

					positionOutButton.setEnabled(true);
					checkOutButton.setEnabled(true);
					checkOut.setEnabled(true);

					selectInButton.setEnabled(false);
					selectOutButton.setEnabled(true);

					lastStart = checkIn.getText().toString();
					//startAddress = getAddressByName(lastStart);
					travelDAO.saveStation(lastStart);

					Intent intent = new Intent(TravelActivity.this, CheckInOutService.class);
					intent.putExtra(CheckInOutService.CHECK_IN_STATION, lastStart);
					startService(intent);
				}
			}
		});

		checkOutButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{

				if (checkOut.getText().length() == 0)
					Toast.makeText(TravelActivity.this, "Fill check-out point", Toast.LENGTH_LONG).show();
				else
				{
					playSound(RingtoneManager.TYPE_NOTIFICATION);

					lastDestination = checkOut.getText().toString();
					resetGui(checkIn, checkOut, positionOutButton, checkOutButton, positionInButton, checkInButton, selectInButton, selectOutButton);

					travelDAO.saveStation(lastDestination);

					// This destination geo things are not working properly :(
					//destinationAddress = getAddressByName(lastDestination);
					distance = 0;//getDistance(startAddress, destinationAddress);
					travelDAO.saveTravel(lastStart, lastDestination, distance);
					Toast.makeText(TravelActivity.this, "Travel from " + lastStart + " to " + lastDestination + " saved. Distance: " + distance, Toast.LENGTH_LONG).show();

					Intent intent = new Intent(TravelActivity.this, CheckInOutService.class);
					stopService(intent);

					// There is no point checking two directions because service is responsible for this logic.
					RegisteredTravel registeredTravel = new RegisteredTravel(TravelActivity.this);
					registeredTravel.execute(new RegisteredTravel.Param("from", lastStart), new RegisteredTravel.Param("to", lastDestination));
				}
			}
		});

		selectInButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TravelActivity.this, StationListActivity.class);
				startActivityForResult(intent, SELECT_IN_BUTTON_CODE);
			}
		});


		selectOutButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TravelActivity.this, StationListActivity.class);
				startActivityForResult(intent, SELECT_OUT_BUTTON_CODE);
			}
		});

		trainImage.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TravelActivity.this, BrowserActivity.class);
				startActivity(intent);
			}
		});

		LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		String bestProvider = locationManager.getBestProvider(criteria, true);
		final MyLocationListener listener = new MyLocationListener(bestProvider, this);
		locationManager.requestLocationUpdates(bestProvider, 0, 0, listener);

		positionInButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TravelActivity.this, MapActivity.class);
				startActivityForResult(intent, SELECT_IN_POSITION_MAP);
				//checkIn.setText(getAddressByLocation(listener.getLocation()).getAddressLine(0));
			}
		});

		positionOutButton.setOnClickListener(new View.OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				Intent intent = new Intent(TravelActivity.this, MapActivity.class);
				startActivityForResult(intent, SELECT_OUT_POSITION_MAP);
				//checkOut.setText(getAddressByLocation(listener.getLocation()).getAddressLine(0));
			}
		});


	}

	private void playSound(int ringtoneType)
	{
		Uri notification = RingtoneManager.getDefaultUri(ringtoneType);
		Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
		r.play();
	}

	private void resetGui(EditText checkIn, EditText checkOut, Button positionOutButton, Button checkOutButton, Button positionInButton, Button checkInButton, Button selectInButton, Button selectOutButton)
	{
		checkIn.getText().clear();
		checkOut.getText().clear();

		positionOutButton.setEnabled(false);
		checkOutButton.setEnabled(false);
		checkOut.setEnabled(false);

		positionInButton.setEnabled(true);
		checkInButton.setEnabled(true);
		checkIn.setEnabled(true);

		selectInButton.setEnabled(true);
		selectOutButton.setEnabled(false);
	}

	protected void onPause() {
		super.onPause();
		if (checkOutReceiver != null) unregisterReceiver(checkOutReceiver);
	}

	@Override
	protected void onResume()
	{
		super.onResume();

		if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(TravelActivity.this) != ConnectionResult.SUCCESS)
			Toast.makeText(TravelActivity.this, "Google play services not working", Toast.LENGTH_LONG).show();

		if (checkOutReceiver == null) checkOutReceiver = new CheckOutReceiver();
		IntentFilter intentFilter = new IntentFilter(CheckInOutService.AUTOMATIC_CHECK_OUT);
		registerReceiver(checkOutReceiver, intentFilter);
	}

	private float getDistance(Address startAddress, Address destinationAddress)
	{
		Location startLocation = new Location(startAddress.getAddressLine(0));
		startLocation.setLatitude(startAddress.getLatitude());
		startLocation.setAltitude(startAddress.getLongitude());

		Location destinationLocation = new Location(destinationAddress.getAddressLine(0));
		startLocation.setLatitude(destinationAddress.getLatitude());
		startLocation.setAltitude(destinationAddress.getLongitude());

		if (destinationLocation != null && startLocation != null)
		{
			return destinationLocation.distanceTo(startLocation) / 1000;
		}
		return 0;
	}

	private Address getAddressByName(String station)
	{
		if (Geocoder.isPresent())
		{
			Geocoder geocoder = new Geocoder(TravelActivity.this);
			try
			{
				List<Address> addresses = geocoder.getFromLocationName(station, 1);
				if (addresses.size() > 0)
				{
					return addresses.get(0);
				}
			} catch (IOException ioe)
			{
				Toast.makeText(TravelActivity.this, station + " not found.", Toast.LENGTH_LONG).show();
			}
		}
		return null;
	}

	private Address getAddressByLocation(Location location)
	{
		if (Geocoder.isPresent())
		{
			Geocoder geocoder = new Geocoder(TravelActivity.this);
			try
			{
				List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
				if (addresses.size() > 0)
				{
					return addresses.get(0);
				}
			} catch (IOException ioe)
			{
				Toast.makeText(TravelActivity.this, "Address by location not found.", Toast.LENGTH_LONG).show();
			}
		}
		return null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_travel, menu);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		if (id == R.id.HistoryMenuItem)
		{
			Intent intent = new Intent(TravelActivity.this, HistoryActivity.class);
			startActivity(intent);
		} else if (id == R.id.SettingsMenuItem)
		{
			Intent intent = new Intent(TravelActivity.this, SettingsActivity.class);
			startActivity(intent);
		}
		else if (id == R.id.InviteFriendsMenuItem)
		{
			Intent intent = new Intent(TravelActivity.this, InviteActivity.class);
			startActivity(intent);
		}
		else if (id == R.id.PricesMenuItem)
		{
			Intent intent = new Intent(TravelActivity.this, PriceListActivity.class);
			startActivity(intent);
		}
		else if (id == R.id.NotifyFriendsMenuItem)
		{
			Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null,
					null, null, null);
			while (cursor.moveToNext())
			{
				String body = cursor.getString(cursor.getColumnIndex("body"));
				if (body.equals(getResources().getString(R.string.SmsInvite)))
				{
					SmsManager smsManager = SmsManager.getDefault();
					PendingIntent pendingIntent  = PendingIntent.getBroadcast(TravelActivity.this, 0, new Intent("SMS_SENT"), 0);
					String address = cursor.getString(cursor.getColumnIndex("address"));
					smsManager.sendTextMessage(address, null, getResources().getString(R.string.SmsInviteNotify), pendingIntent, null);

					ContentValues values = new ContentValues();
					values.put("address", address);
					values.put("body", getResources().getString(R.string.SmsInviteNotify));
					getContentResolver().insert(Uri.parse("content://sms/sent"), values);

					Toast.makeText(TravelActivity.this, "Notify has been sent.", Toast.LENGTH_LONG).show();

					return super.onOptionsItemSelected(item);
				}
			}
			Toast.makeText(TravelActivity.this, "No invite has been found.", Toast.LENGTH_LONG).show();
		}

		return super.onOptionsItemSelected(item);
	}

	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		final EditText checkInText = (EditText) findViewById(R.id.checkInEdit);
		final EditText checkOutText = (EditText) findViewById(R.id.checkOutEdit);

		final Button selectInButton = (Button) findViewById(R.id.selectInButton);
		final Button selectOutButton = (Button) findViewById(R.id.selectOutButton);

		final Button checkInButton = (Button) findViewById(R.id.checkInButton);
		final Button checkOutButton = (Button) findViewById(R.id.checkOutButton);

		final Button positionInButton = (Button) findViewById(R.id.positionInButton);
		final Button positionOutButton = (Button) findViewById(R.id.positionOutButton);



		outState.putBoolean(EDIT_IN_ENABLED, checkInText.isEnabled());
		outState.putBoolean(EDIT_OUT_ENABLED, checkOutText.isEnabled());

		outState.putBoolean(SELECT_IN_ENABLED, selectInButton.isEnabled());
		outState.putBoolean(SELECT_OUT_ENABLED, selectOutButton.isEnabled());

		outState.putBoolean(CHECK_IN_ENABLED, checkInButton.isEnabled());
		outState.putBoolean(CHECK_OUT_ENABLED, checkOutButton.isEnabled());

		outState.putString(LAST_START, lastStart);
		outState.putString(LAST_DESTINATION, lastDestination);

		outState.putBoolean(POSITION_IN_ENABLED, positionInButton.isEnabled());
		outState.putBoolean(POSITION_OUT_ENABLED, positionOutButton.isEnabled());

	}

	protected void onRestoreInstanceState(Bundle savedInstanceState)
	{
		super.onRestoreInstanceState(savedInstanceState);

		// This is an overkill, is there anything more clever to do this?

		final EditText checkIn = (EditText) findViewById(R.id.checkInEdit);
		final EditText checkOut = (EditText) findViewById(R.id.checkOutEdit);

		final Button selectInButton = (Button) findViewById(R.id.selectInButton);
		final Button selectOutButton = (Button) findViewById(R.id.selectOutButton);

		final Button checkInButton = (Button) findViewById(R.id.checkInButton);
		final Button checkOutButton = (Button) findViewById(R.id.checkOutButton);

		final Button positionInButton = (Button) findViewById(R.id.positionInButton);
		final Button positionOutButton = (Button) findViewById(R.id.positionOutButton);

		checkIn.setEnabled(savedInstanceState.getBoolean(EDIT_IN_ENABLED));
		checkOut.setEnabled(savedInstanceState.getBoolean(EDIT_OUT_ENABLED));

		selectInButton.setEnabled(savedInstanceState.getBoolean(SELECT_IN_ENABLED));
		selectOutButton.setEnabled(savedInstanceState.getBoolean(SELECT_OUT_ENABLED));

		checkInButton.setEnabled(savedInstanceState.getBoolean(CHECK_IN_ENABLED));
		checkOutButton.setEnabled(savedInstanceState.getBoolean(CHECK_OUT_ENABLED));

		positionInButton.setEnabled(savedInstanceState.getBoolean(POSITION_IN_ENABLED));
		positionOutButton.setEnabled(savedInstanceState.getBoolean(POSITION_OUT_ENABLED));

		lastStart = savedInstanceState.getString(LAST_START);
		lastDestination = savedInstanceState.getString(LAST_DESTINATION);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);

		if (resultCode == RESULT_OK)
		{
			EditText editText = null;
			if (requestCode == SELECT_IN_BUTTON_CODE)
			{
				final String selectedStationName = data.getExtras().getString(SELECTED_STATION_NAME);
				editText = (EditText) findViewById(R.id.checkInEdit);
				editText.setText(selectedStationName);
			} else if (requestCode == SELECT_OUT_BUTTON_CODE)
			{
				final String selectedStationName = data.getExtras().getString(SELECTED_STATION_NAME);
				editText = (EditText) findViewById(R.id.checkOutEdit);
				editText.setText(selectedStationName);
			} else if (requestCode == SELECT_IN_POSITION_MAP)
			{
				editText = (EditText) findViewById(R.id.checkInEdit);
				final LatLng selectedLatLng = (LatLng) data.getExtras().get(SELECTED_STATION_LAT_LNG);
				Location location = new Location("");
				location.setLatitude(selectedLatLng.latitude);
				location.setLongitude(selectedLatLng.longitude);
				Address add = getAddressByLocation(location);
				editText.setText(getAddressByLocation(location).getAddressLine(0));
			} else if (requestCode == SELECT_OUT_POSITION_MAP)
			{
				editText = (EditText) findViewById(R.id.checkOutEdit);
				final LatLng selectedLatLng = (LatLng) data.getExtras().get(SELECTED_STATION_LAT_LNG);
				Location location = new Location("");
				location.setLatitude(selectedLatLng.latitude);
				location.setLongitude(selectedLatLng.longitude);
				editText.setText(getAddressByLocation(location).getAddressLine(0));
			} else
				return;


		}
	}

	private class CheckOutReceiver extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(CheckInOutService.AUTOMATIC_CHECK_OUT)) {

				final Button checkInButton = (Button) findViewById(R.id.checkInButton);
				final Button checkOutButton = (Button) findViewById(R.id.checkOutButton);

				final EditText checkIn = (EditText) findViewById(R.id.checkInEdit);
				final EditText checkOut = (EditText) findViewById(R.id.checkOutEdit);

				final Button selectInButton = (Button) findViewById(R.id.selectInButton);
				final Button selectOutButton = (Button) findViewById(R.id.selectOutButton);

				final ImageView trainImage = (ImageView) findViewById(R.id.trainImage);

				final Button positionInButton = (Button) findViewById(R.id.positionInButton);
				final Button positionOutButton = (Button) findViewById(R.id.positionOutButton);

				resetGui(checkIn, checkOut, positionOutButton, checkOutButton, positionInButton, checkInButton, selectInButton, selectOutButton);
			}

		}
	}
}
