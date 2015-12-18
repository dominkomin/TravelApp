package dk.itu.mmad.travelApp.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import dk.itu.mmad.travelApp.R;


public class BrowserActivity extends ActionBarActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browser);

		final WebView www = (WebView) findViewById(R.id.www);
		www.setWebViewClient(new WebViewClient());
		www.loadUrl("http://www.dsb.dk");
	}
}
