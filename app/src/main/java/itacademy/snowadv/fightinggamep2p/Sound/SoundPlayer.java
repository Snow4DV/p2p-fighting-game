package itacademy.snowadv.fightinggamep2p.Sound;

import android.content.Context;
import android.content.res.Resources;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.Field;
import java.util.HashMap;

import itacademy.snowadv.fightinggamep2p.R;

public class SoundPlayer {
    private AudioManager audioManager;
    private SoundPool mSoundPool;
    private final Context context;
    private MediaPlayer mediaPlayer;
    private SfxName longContent;
    private float volumeMultiplier = 1f;

    public void setVolumeMultiplier(float volumeMultiplier) {
        if(volumeMultiplier > 1f) {
            volumeMultiplier = 1f;
        } else if(volumeMultiplier < 0f) {
            volumeMultiplier = 0f;
        }
        this.volumeMultiplier = volumeMultiplier;
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * volumeMultiplier;
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = curVolume / maxVolume;
        float rightVolume = curVolume / maxVolume;
        if(mediaPlayer != null) {
            mediaPlayer.setVolume(leftVolume,rightVolume);
        }
    }

    public void offsetVolumeMultiplier(float volumeMultiplierOffset) {
        setVolumeMultiplier(volumeMultiplierOffset + volumeMultiplier);
    }

    public int getVolumePercent() {
        return (int) (volumeMultiplier * 100);
    }

    public enum SfxName {
        COORDINATING, BACKGROUND_MUSIC, BLOWN_GRENADE, DEATH, HACKING, HEAVY_BLOWN_GRENADE,
        MENU_MUSIC, PISTOL, REPORTING, SHOTGUN, THROW_PAPER, WIN, HEAL, CLICK, SCREAM
    }

    public static SoundPlayer build(Context context) {
        SoundPlayer soundPlayer = new SoundPlayer(context);
        soundPlayer.getAudioManagerByContext();
        soundPlayer.loadSoundPool();
        return soundPlayer;
    }

    public static SoundPlayer build(Context context, float volumeMultiplier) {
        SoundPlayer soundPlayer = new SoundPlayer(context);
        soundPlayer.getAudioManagerByContext();
        soundPlayer.loadSoundPool();
        soundPlayer.setVolumeMultiplier(volumeMultiplier);
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
        loadedSounds.put(SfxName.HEAL, load(R.raw.heal));
        loadedSounds.put(SfxName.CLICK, load(R.raw.click));
        loadedSounds.put(SfxName.SCREAM, load(R.raw.scream));
    }

    /**
     * Loads sound resource to sound pool
     * @return id of loaded sound
     */
    private int load(int resource) {
        return mSoundPool.load(context, resource, 1);
    }

    public void play(SfxName soundName, float volumeMultiplier) {
        if(!(loadedSounds.containsKey(soundName)))
            throw new Resources.NotFoundException("There's no \"" + soundName +
                    "\" in registered resources");
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * volumeMultiplier;
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = curVolume / maxVolume;
        float rightVolume = curVolume / maxVolume;
        streamedSounds.put(soundName, mSoundPool.play(loadedSounds.get(soundName), leftVolume, rightVolume,
                1, -1, 1));
    }

    /**
     * Plays sfx one time
     * @param soundName SFX name
     */
    public void playOnce(SfxName soundName) {
        if(!(loadedSounds.containsKey(soundName))) return;
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = curVolume / maxVolume;
        float rightVolume = curVolume / maxVolume;
        streamedSounds.put(soundName, mSoundPool.play(loadedSounds.get(soundName), leftVolume, rightVolume,
                1, 0, 1));
    }

    public void playOnceWithDelay(SfxName soundName, int delay) {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            playOnce(soundName);
        }, delay);
    }

    public void stopLong() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
            longContent = null;
        }
    }

    public void pauseLong() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    public void resumeLong() {
        if(mediaPlayer != null && longContent != null) {
            playLong(longContent);
        }
    }
    public void playLong(SfxName soundName) {
        stopLong();
        float curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) * volumeMultiplier;
        float maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float leftVolume = curVolume / maxVolume;
        float rightVolume = curVolume / maxVolume;
        switch(soundName) {
            default:
            case BACKGROUND_MUSIC:
                mediaPlayer = MediaPlayer.create(context, R.raw.background_music);
                longContent = soundName;
                break;
            case MENU_MUSIC:
                mediaPlayer = MediaPlayer.create(context, R.raw.menu_music);
                longContent = soundName;
        }
        mediaPlayer.setLooping(true);
        mediaPlayer.setVolume(leftVolume, rightVolume);
        mediaPlayer.start();

    }

    public void stop(String soundName) {
        if(!(loadedSounds.containsKey(soundName))) return;
        mSoundPool.stop(streamedSounds.get(soundName));
    }
}
