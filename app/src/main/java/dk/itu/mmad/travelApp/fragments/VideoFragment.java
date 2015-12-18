package dk.itu.mmad.travelApp.fragments;


import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.VideoView;

import dk.itu.mmad.travelApp.R;

public class VideoFragment extends Fragment
{
	
	private VideoView video;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.video_fragment, container, false);
		video = (VideoView) view.findViewById(R.id.video);
		video.setVideoURI(Uri.parse("https://ia700402.us.archive.org/11/items/train_1/train_512kb.mp4"));
        video.setMediaController(new MediaController(getActivity()));
        video.requestFocus();
		return view;
	}

	public void play()
	{
		video.start();
	}
}
