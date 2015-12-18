package dk.itu.mmad.travelApp.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by domi on 25-02-2015.
 */
public class SqlLiteHelper extends SQLiteOpenHelper
{
	public SqlLiteHelper(Context context)
	{
		super(context, "travel", null, 3);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL("CREATE TABLE travels (_id integer primary key autoincrement, start text, destination text, distance real);");
		db.execSQL("CREATE TABLE stations (_id integer primary key autoincrement, station text UNIQUE);");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		db.execSQL("DROP table travels");
		db.execSQL("DROP table stations");
		onCreate(db);
	}
}
