package dk.itu.mmad.travelApp.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by domi on 25-02-2015.
 */
public class TravelDAO
{
	public final static String TRAVELS_TABLE_NAME = "travels";

	public final static String ID_TRAVEL_COL = "_id";
	public final static String START_COL = "start";
	public final static String DESTINATION_COL = "destination";
	public final static String DISTANCE_COL = "distance";

	public final static String STATIONS_TABLE_NAME = "stations";

	public final static String ID_STATION_COL = "_id";
	public final static String STATION_COL = "station";

	private SqlLiteHelper helper;
	private SQLiteDatabase db;

	private SharedPreferences preferences;

	public TravelDAO(Context context)
	{
		helper = new SqlLiteHelper(context);
		preferences = context.getSharedPreferences(context.getPackageName()
				+ "_preferences", Context.MODE_PRIVATE);
	}

	public void open()
	{
		db = helper.getWritableDatabase();
	}

	public void close()
	{
		helper.close();
	}

	public Cursor getTravels()
	{
		String limit = preferences.getString("historyLength", "10");
		Cursor cursor = db.query(TRAVELS_TABLE_NAME, new String[]{ID_TRAVEL_COL, START_COL, DESTINATION_COL, DISTANCE_COL}, null, null, null, null, ID_TRAVEL_COL + " DESC", limit);
		return cursor;
	}

	public void saveTravel(String start, String destination, float distance)
	{
		ContentValues values = new ContentValues();
		values.put(START_COL, start);
		values.put(DESTINATION_COL, destination);
		values.put(DISTANCE_COL, distance);
		db.insert(TRAVELS_TABLE_NAME, null, values);
	}

	public Cursor getStations()
	{
		Cursor cursor = db.query(STATIONS_TABLE_NAME, new String[]{ID_STATION_COL, STATION_COL}, null, null, null, null, STATION_COL);
		return cursor;
	}

	public void saveStation(String station)
	{
		boolean save = preferences.getBoolean("saveStations", true);
		if (!station.isEmpty() && save)
		{
			ContentValues values = new ContentValues();
			values.put(STATION_COL, station);
			db.insertWithOnConflict(STATIONS_TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
		}
	}

	public void clearStations()
	{
		db.delete(STATIONS_TABLE_NAME, null, null);
	}

	public void clearTravels()
	{
		db.delete(TRAVELS_TABLE_NAME, null, null);
	}
}
