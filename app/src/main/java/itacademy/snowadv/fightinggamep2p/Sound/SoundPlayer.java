package itacademy.snowadv.fightinggamep2p.Sound;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;

import java.lang.reflect.Field;
import java.util.HashMap;

import itacademy.snowadv.fightinggamep2p.R;

public class SoundPlayer {
    private AudioManager audioManager;
    private SoundPool mSoundPool;
    private final Context context;



    public enum SfxName {
        COORDINATING, BACKGROUND_MUSIC, BLOWN_GRENADE, DEATH, HACKING, HEAVY_BLOWN_GRENADE,
        MENU_MUSIC, PISTOL, REPORTING, SHOTGUN, THROW_PAPER, WIN
    }
    public static SoundPlayer build(Context context) {
        SoundPlayer soundPlayer = new SoundPlayer(context);
        soundPlayer.getAudioManagerByContext();
        soundPlayer.loadSoundPool();
        return soundPlayer;
    }
    private SoundPlayer(Context context) {
        this.context = context;
    }


    HashMap<SfxName, Integer> loadedSounds = new HashMap<>();
    HashMap<SfxName, Integer> streamedSounds = new HashMap<>();



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
        loadedSounds.put(SfxName.COORDINATING, load(R.raw.coordinating));
        loadedSounds.put(SfxName.BACKGROUND_MUSIC, load(R.raw.background_music));
        loadedSounds.put(SfxName.BLOWN_GRENADE, load(R.raw.blown_grenade));
        loadedSounds.put(SfxName.DEATH, load(R.raw.death));
        loadedSounds.put(SfxName.HACKING, load(R.raw.hacking));
        loadedSounds.put(SfxName.HEAVY_BLOWN_GRENADE, load(R.raw.heavy_blown_grenade));
        loadedSounds.put(SfxName.MENU_MUSIC, load(R.raw.menu_music));
        loadedSounds.put(SfxName.PISTOL, load(R.raw.pistol));
        loadedSounds.put(SfxName.REPORTING, load(R.raw.reporting));
        loadedSounds.put(SfxName.SHOTGUN, load(R.raw.shotgun));
        loadedSounds.put(SfxName.THROW_PAPER, load(R.raw.throw_paper));
        loadedSounds.put(SfxName.WIN, load(R.raw.win));
    }

    /**
     * Loads sound resource to sound pool
     * @return id of loaded sound
     */
    private int load(int resource) {
        return mSoundPool.load(context, resource, 1);
    }

    public void play(SfxName soundName) {
        if(!(loadedSounds.containsKey(soundName)))
            throw new Resources.NotFoundException("There's no \"" + soundName +
                    "\" in registered resources");
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = curVolume / maxVolume;
        float rightVolume = curVolume / maxVolume;
        streamedSounds.put(soundName, mSoundPool.play(loadedSounds.get(soundName), leftVolume, rightVolume,
                1, -1, 1));
    }

    public void playOnce(SfxName soundName) {
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
