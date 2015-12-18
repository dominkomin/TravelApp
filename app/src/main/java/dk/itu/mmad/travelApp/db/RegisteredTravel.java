package dk.itu.mmad.travelApp.db;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import dk.itu.mmad.travelApp.db.RegisteredTravel.Param;
import dk.itu.mmad.travelApp.activities.TravelActivity;

/**
 * Created by domi on 25-04-2015.
 */


public class RegisteredTravel extends AsyncTask<Param, Void, String>
{
	public static class Param {
		private String name;
		private String value;

		public Param(String name, String value) {
			this.name = name;
			this.value = value;
		}

	}

	private TravelActivity activity;
	private Throwable error;

	public RegisteredTravel(TravelActivity activity) {
		this.activity = activity;
	}

	@Override
	protected String doInBackground(Param... params)
	{
		String queryString = makeQueryString(params);

		InputStream inputStream = null;
		OutputStream outputStream = null;
		BufferedReader reader = null;
		try {
			URL url = new URL("http://www.itu.dk/people/jacok/MMAD/services/registertravel/");
			URLConnection connection = url.openConnection();

			((HttpURLConnection)connection).setRequestMethod("POST");

			outputStream = connection.getOutputStream();
			PrintWriter writer = new PrintWriter(outputStream);
			writer.write(queryString);
			writer.flush();
			writer.close();

			inputStream = connection.getInputStream();
			reader = new BufferedReader(new InputStreamReader(inputStream));

			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}

			JSONObject jsonObject = new JSONObject(sb.toString());
			String result = "price: " + jsonObject.getInt("price");
			return result;

		} catch (IOException e) {
			error = e;
		} catch (JSONException e)
		{
			error = e;
		}
		catch (Exception e)
		{
			error = e;
		}
		finally {
			try {
				if (inputStream != null) {
					inputStream.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (Exception e) {
				// Sorry guys
			}
		}
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		if (result != null) {
			builder.setTitle("Travel complete");
			builder.setMessage(result);
		} else if (error != null) {
			builder.setTitle("Error");
			builder.setMessage(error.getMessage());

		}
		builder.show();
	}

	private String makeQueryString(Param... params) {
		StringBuilder queryString = new StringBuilder();
		boolean first = true;
		for (Param p : params) {
			if (first) {
				first = false;
			} else {
				queryString.append("&");
			}
			queryString.append(p.name + "=" + p.value);
		}
		return queryString.toString();
	}


}
