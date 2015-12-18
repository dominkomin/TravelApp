package dk.itu.mmad.travelApp.services;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.JsonReader;
import android.util.JsonToken;
import android.widget.ArrayAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by domi on 25-04-2015.
 */
public class PriceListTask extends AsyncTask<Void, Void, List<String>>
{
	private ListActivity activity;
	private Throwable error;

	public PriceListTask(ListActivity activity) {
		this.activity = activity;
	}

	@Override
	protected List<String> doInBackground(Void... params) {
		InputStream inputStream = null;
		JsonReader jsonReader = null;

		try {
			URL url = new URL("http://www.itu.dk/people/jacok/MMAD/services/prices/");
			URLConnection connection = url.openConnection();

			inputStream = connection.getInputStream();

			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

			List<String> result = new ArrayList<String>();
			StringBuilder sb = null;

			jsonReader = new JsonReader(reader);

			jsonReader.beginObject();
			jsonReader.skipValue();
			jsonReader.beginArray();
			while (jsonReader.peek() != JsonToken.END_ARRAY) {
				jsonReader.beginObject();
				while (jsonReader.peek() != JsonToken.END_OBJECT) {
					jsonReader.skipValue();
					jsonReader.beginArray();
					sb = new StringBuilder(jsonReader.nextString()+" - ");
					sb.append(jsonReader.nextString() + ": ");
					jsonReader.endArray();
					jsonReader.skipValue();
					sb.append(jsonReader.nextInt());
					result.add(sb.toString());
				}
				jsonReader.endObject();
			}
			jsonReader.endArray();
			jsonReader.skipValue();
			int defaultPrice = jsonReader.nextInt();
			result.add("Default price: " + defaultPrice);

			return result;

		} catch (MalformedURLException e) {
			error = e;
		} catch (IOException e) {
			error = e;
		} finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (jsonReader != null) {
					jsonReader.close();
				}
			} catch (Exception e) {
				// Just too bad
			}
		}

		return null;
	}

	@Override
	protected void onPostExecute(List<String> result) {
		if (result != null) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity, android.R.layout.simple_list_item_1,
					result);
			activity.setListAdapter(adapter);
		} else if (error != null) {
			AlertDialog.Builder builder = new AlertDialog.Builder(activity);
			builder.setTitle("Error");
			builder.setMessage(error.getMessage());
			builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			builder.show();
		}
	}
}
