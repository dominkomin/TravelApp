package dk.itu.mmad.travelApp.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import dk.itu.mmad.travelApp.db.TravelDAO;


public class StationListActivity extends ListActivity
{

	TravelDAO travelDAO;
	SimpleCursorAdapter cursorAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		travelDAO = new TravelDAO(this);
		travelDAO.open();

		Cursor stations = travelDAO.getStations();
		startManagingCursor(stations);

		if (stations.getCount() == 0)
		{
			setResult(RESULT_CANCELED);
			stations.close();
			finish();
			Toast.makeText(StationListActivity.this, "No saved stations.", Toast.LENGTH_LONG).show();
		}


		SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
				android.R.layout.simple_list_item_1, stations,
				new String[]{"station"}, new int[]{android.R.id.text1
		});
		setListAdapter(cursorAdapter);
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		travelDAO.close();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)
	{
		super.onListItemClick(l, v, position, id);

		Cursor cursor = (Cursor) l.getItemAtPosition(position);
		Intent intent = new Intent().putExtra(
				TravelActivity.SELECTED_STATION_NAME,
				cursor.getString(cursor.getColumnIndexOrThrow("station")));
		setResult(RESULT_OK, intent);
		cursor.close();
		finish();
	}
}
