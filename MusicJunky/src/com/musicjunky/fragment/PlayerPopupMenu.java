package com.musicjunky.fragment;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import com.musicjunky.R;

public class PlayerPopupMenu extends DialogFragment {
	
	public static boolean VISIBILE=false;
	
	public static PlayerPopupMenu newInstance(Bundle bundle){
		PlayerPopupMenu popupMenu=new PlayerPopupMenu();
		popupMenu.setArguments(bundle);
		return popupMenu;
	}
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NORMAL, R.style.PlayerMenu_Theme_orange);
	}
	
	private View.OnClickListener onShowQueue=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dismiss();			
		}
	};
	
	private View.OnClickListener onClearQueue=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dismiss();			
		}
	};
	
	private View.OnClickListener onAddToPlayList=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dismiss();
		}
	};
	
	private View.OnClickListener onGoToArtist=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dismiss();	
		}
	};
	
	private View.OnClickListener onGoToAlbum=new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			dismiss();	
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.player_popup_menu, container, false);
		Button showQueueButton=(Button)view.findViewById(R.id.show_queue_item);
		showQueueButton.setOnClickListener(onShowQueue);
		
		Button clearQueueButton=(Button)view.findViewById(R.id.clear_queue_item);
		clearQueueButton.setOnClickListener(onClearQueue);
		
		Button addToPlaylistButton=(Button)view.findViewById(R.id.add_to_playlist_item);
		addToPlaylistButton.setOnClickListener(onAddToPlayList);
		
		Button goToArtistButton=(Button)view.findViewById(R.id.go_to_arist);
		goToArtistButton.setOnClickListener(onGoToArtist);
		
		Button goToAlbumButton=(Button)view.findViewById(R.id.go_to_album);
		goToAlbumButton.setOnClickListener(onGoToAlbum);
		
		return view;
		
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog dialog=super.onCreateDialog(savedInstanceState);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		return dialog;
	}
	
	@Override
	public void onDestroyView() {
		VISIBILE=false;
		super.onDestroyView();
	}
}
