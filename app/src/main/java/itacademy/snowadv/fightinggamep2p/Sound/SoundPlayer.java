package itacademy.snowadv.fightinggamep2p.Sound;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import java.lang.reflect.Field;
import java.util.HashMap;

import itacademy.snowadv.fightinggamep2p.R;

public class SoundPlayer {
    private AudioManager audioManager;
    private SoundPool mSoundPool;
    private Context context;



    public SoundPlayer(Context context) {
        this.context = context;
    }


    HashMap<String, Integer> loadedSounds = new HashMap<>();
    HashMap<String, Integer> streamedSounds = new HashMap<>();



    private void getAudioManagerByContext() {
        audioManager = (AudioManager) context.
                getSystemService(Context.AUDIO_SERVICE);
    }

    private void loadSoundPool() {
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();
        mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(attributes)
                .build();
        loadedSounds.put("coordinating", load(R.raw.coordinating));
        loadedSounds.put("background_music", load(R.raw.background_music));
        loadedSounds.put("blown_grenade", load(R.raw.blown_grenade));
        loadedSounds.put("death", load(R.raw.death));
        loadedSounds.put("hacking", load(R.raw.hacking));
        loadedSounds.put("heavy_blown_grenade", load(R.raw.heavy_blown_grenade));
        loadedSounds.put("menu_music", load(R.raw.menu_music));
        loadedSounds.put("pistol", load(R.raw.pistol));
        loadedSounds.put("reporting", load(R.raw.reporting));
        loadedSounds.put("shotgun", load(R.raw.shotgun));
        loadedSounds.put("throw_paper", load(R.raw.throw_paper));
        loadedSounds.put("win", load(R.raw.win));
    }

    /**
     * Loads sound resource to sound pool
     * @return id of loaded sound
     */
    private int load(int resource) {
        return mSoundPool.load(context, resource, 1);
    }

    public void play(String soundName) {
        if(!(loadedSounds.containsKey(soundName))) return;
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = curVolume / maxVolume;
        float rightVolume = curVolume / maxVolume;
        streamedSounds.put(soundName, mSoundPool.play(loadedSounds.get(soundName), leftVolume, rightVolume,
                1, -1, 1));
    }

    public void playOnce(String soundName) {
        if(!(loadedSounds.containsKey(soundName))) return;
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = curVolume / maxVolume;
        float rightVolume = curVolume / maxVolume;
        streamedSounds.put(soundName, mSoundPool.play(loadedSounds.get(soundName), leftVolume, rightVolume,
                1, 0, 1));
    }

    public void stop(String soundName) {
        if(!(loadedSounds.containsKey(soundName))) return;
        mSoundPool.stop(streamedSounds.get(soundName));
    }
}
