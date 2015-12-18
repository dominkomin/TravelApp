package dk.itu.mmad.travelApp.activities;

import android.app.ListActivity;
import android.os.Bundle;

import dk.itu.mmad.travelApp.services.PriceListTask;

/**
 * Created by domi on 25-04-2015.
 */
public class PriceListActivity extends ListActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		PriceListTask priceListTask = new PriceListTask(PriceListActivity.this);
		priceListTask.execute();

	}
}
