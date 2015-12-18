package dk.itu.mmad.travelApp.activities;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import dk.itu.mmad.travelApp.R;
import dk.itu.mmad.travelApp.db.TravelDAO;


public class HistoryActivity extends ListActivity
{

	TravelDAO travelDAO;
	Cursor travels;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		travelDAO = new TravelDAO(this);
		travelDAO.open();

		travels = travelDAO.getTravels();
		startManagingCursor(travels);

		SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this,
				R.layout.history_item, travels,
				new String[]{"start", "destination", "distance"}, new int[]{R.id.startListItem, R.id.destinationListItem, R.id.distanceListItem
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
	}
}
