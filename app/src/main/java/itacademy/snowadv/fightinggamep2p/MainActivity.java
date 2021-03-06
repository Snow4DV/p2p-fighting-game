package itacademy.snowadv.fightinggamep2p;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import itacademy.snowadv.fightinggamep2p.Classes.Events.DisconnectedEvent;
import itacademy.snowadv.fightinggamep2p.Classes.Events.GameStartedEvent;
import itacademy.snowadv.fightinggamep2p.Classes.Notifiable;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.GameClient;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.GameClientServer;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.GameServer;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.GameConnectionPacket;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.GameStatsPacket;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.StartTheGameRequest;
import itacademy.snowadv.fightinggamep2p.Fragments.BattleField.ServerBattleFragment;
import itacademy.snowadv.fightinggamep2p.Fragments.GameController.GameControllerFragment;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.LobbyFragment;
import itacademy.snowadv.fightinggamep2p.Fragments.PlayerChoiceFragment;
import itacademy.snowadv.fightinggamep2p.Fragments.ServerList.ServerListFragment;
import itacademy.snowadv.fightinggamep2p.Fragments.StartGameFragment;
import itacademy.snowadv.fightinggamep2p.Sound.SoundPlayer;
import itacademy.snowadv.fightinggamep2p.UI.FlatButton;
import itacademy.snowadv.fightinggamep2p.databinding.ActivityMainBinding;

public class MainActivity extends FragmentActivity implements Notifiable {

    /* Variable for double pressing back button to leave */
    private boolean doubleBackToExitPressedOnce = false;

    private static final String TAG = "MainActivty";
    private ActivityMainBinding viewBinding;
    private Fragment currentFragment;
    private GameClientServer clientServer;
    private SoundPlayer backgroundSoundPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backgroundSoundPlayer = SoundPlayer.build(this, 0.4f);
        FlatButton.setActionOnEveryClick(new Runnable() {
            @Override
            public void run() {
                backgroundSoundPlayer.playOnce(SoundPlayer.SfxName.CLICK);
            }
        });
        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        // Force fullscreen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(viewBinding.getRoot());
        transitToNewFragment(new StartGameFragment());
    }

    /*private void addTheRoleChoiceFragment() {
        StartGameFragment roleChoiceFragment = new StartGameFragment(callbackResult -> {
            switch(callbackResult) {
                case SERVER:
                    createTheServer();
                    break;
                case CLIENT:
                    startTransitionToServerListFragment();
                    break;
            }
        });
    }*/

    private void fallbackToTheStartGameScreen() {
            transitToNewFragment(new StartGameFragment());
            doTheClientServerStopJob();
    }

    /**
     * Changes volume in main activity's SoundPlayer
     * another class.
     * @param offset Value on which volume multiplier will be changed.
     * @return current volume percent
     */
    public int changeVolume(float offset) {
        backgroundSoundPlayer.offsetVolumeMultiplier(offset);
        return backgroundSoundPlayer.getVolumePercent();
    }


    private void transitToNewFragment(Fragment fragment) {
        currentFragment = fragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view_main, fragment, null);
        fragmentTransaction.commit();
        playBackgroundMusic(fragment);
    }



    private void playBackgroundMusic(Fragment fragment) {
        if(fragment instanceof ServerBattleFragment) {
            backgroundSoundPlayer.playLong(SoundPlayer.SfxName.BACKGROUND_MUSIC);
        } else if(fragment instanceof  StartGameFragment){
            backgroundSoundPlayer.playLong(SoundPlayer.SfxName.MENU_MUSIC);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(backgroundSoundPlayer != null) {
            backgroundSoundPlayer.pauseLong();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(backgroundSoundPlayer != null) {
            backgroundSoundPlayer.resumeLong();
        }
    }

    @Override
    public void onBackPressed() {
        boolean isOnStartScreen = currentFragment instanceof StartGameFragment;
        if (doubleBackToExitPressedOnce) {
            if(!isOnStartScreen) {
                fallbackToTheStartGameScreen();
                return;
            } else {
                super.onBackPressed();
            }
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "?????????????? \"??????????\" ?????? ??????, ?????????? " + (isOnStartScreen ?
                        "??????????" : "?????????????????? ???? ?????????????????? ??????????")
                , Toast.LENGTH_LONG).show();

        new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce=false,
                2000);
    }


    @Override
    public void notify(Fragment fragment) {
        Log.d(TAG, "fragment notified the main activity: " + fragment.toString());
    }

    @Override
    public void notifyFragmentIsDone(Fragment fragment) {
        Log.d(TAG, "Done fragment notified the main activity: " + fragment.toString());
        fallbackToTheStartGameScreen();
    }



    @Override
    public void notifyWithObject(Object object) {
        if(object instanceof GameConnectionPacket) {
            transitToNewFragment(new LobbyFragment(((GameConnectionPacket) object).ip,
                    ((GameConnectionPacket) object).player));
        } else if(object instanceof GameClientServer) { // Save client-server object if provided
            clientServer = (GameClientServer) object;
        } else if(object instanceof DisconnectedEvent) { // Fallback if disconnected
            final Context context = this;
            runOnUiThread(() -> {
                if(((DisconnectedEvent) object).showToast) {
                    Toast.makeText(context, "???????????????? ???? ??????????????",
                            Toast.LENGTH_SHORT).show();
                }
            });

            fallbackToTheStartGameScreen();
        } else if(object instanceof PlayerChoiceFragment.RoleChoiceCallbackResult) {
            switch((PlayerChoiceFragment.RoleChoiceCallbackResult) object) {
                case CLIENT:
                    runThePlayerChoiceFragment();
                    break;
                case SERVER:
                    transitToNewFragment(new LobbyFragment());
                    break;
            }
        } else if(object instanceof StartTheGameRequest) {
            transitToNewFragment(new GameControllerFragment(
                    getClient(), ((StartTheGameRequest)object).gameStatsPacket));
        } else if(object instanceof GameStatsPacket
                && currentFragment instanceof GameControllerFragment) {
            ((GameControllerFragment)currentFragment).updateGameStatsPacket((GameStatsPacket) object);
        } else if(object instanceof GameStartedEvent) {
            transitToNewFragment(new ServerBattleFragment(getServer()));
        }
    }

    private void runThePlayerChoiceFragment() {
        transitToNewFragment(new PlayerChoiceFragment((battlePlayer,
                                                       roleChoiceCallbackResult) ->
                transitToNewFragment(new ServerListFragment(battlePlayer))));
    }

    private @Nullable
    GameClient getClient() {
        if(clientServer instanceof  GameClient) {
            return (GameClient) clientServer;
        }
        return null;
    }
    private @Nullable
    GameServer getServer() {
        if(clientServer instanceof  GameServer) {
            return (GameServer) clientServer;
        }
        return null;
    }

    private void doTheClientServerStopJob() {
        if(getClient() != null) {
            getClient().disconnect();
        } else if(getServer() != null) {
            getServer().stopServer();
        }
    }
}