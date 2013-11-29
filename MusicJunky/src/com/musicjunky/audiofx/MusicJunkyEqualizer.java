package com.musicjunky.audiofx;

import android.media.audiofx.Equalizer;

public class MusicJunkyEqualizer extends Equalizer {

	public final static short[] AUDIO_PRESET_CLASSIC={10,6,-4,8,8};
	public final static short[] AUDIO_PRESET_DANCE={12,0,4,8,2};
	public final static short[] AUDIO_PRESET_FLAT={0,0,0,0,0};
	public final static short[] AUDIO_PRESET_FOLK={6,0,0,4,-2};
	public final static short[] AUDIO_PRESET_HEAVY_METAL={8,2,15,6,0};
	public final static short[] AUDIO_PRESET_HIP_HOP={10,6,0,2,6};
	public final static short[] AUDIO_PRESET_JAZZ={8,4,-4,4,10};
	public final static short[] AUDIO_PRESET_POP={-2,4,10,2,-4};
	public final static short[] AUDIO_PRESET_ROCK={10,6,-2,6,10};
	public final static short[] AUDIO_PRESET_LATIN={12,4,-6,2,14};
	
	private int bandLevelRange=0;
	
	public MusicJunkyEqualizer(int audioSessionID) {
		super(0,audioSessionID);
		bandLevelRange=getBandLevelRange()[1]-getBandLevelRange()[0];
	}
	
	public void setPreset(short[]preset){
		for(short i=0;i<preset.length;i++){
			setBandLevel(i, (short)(preset[i]*100));
		}
	}
	
	public int getRange(){
		return bandLevelRange;
	}
	
}
