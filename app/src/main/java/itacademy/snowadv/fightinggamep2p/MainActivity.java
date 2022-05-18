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
import android.widget.Toast;

import itacademy.snowadv.fightinggamep2p.Classes.Events.DisconnectedEvent;
import itacademy.snowadv.fightinggamep2p.Classes.Events.GameStartedEvent;
import itacademy.snowadv.fightinggamep2p.Classes.Notifiable;
import itacademy.snowadv.fightinggamep2p.Classes.Server.GameClient;
import itacademy.snowadv.fightinggamep2p.Classes.Server.GameClientServer;
import itacademy.snowadv.fightinggamep2p.Classes.Server.GameServer;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ChatMessage;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameConnectionPacket;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameStatsPacket;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.StartTheGameRequest;
import itacademy.snowadv.fightinggamep2p.Fragments.BattleField.ServerBattleFragment;
import itacademy.snowadv.fightinggamep2p.Fragments.GameController.GameControllerFragment;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.LobbyFragment;
import itacademy.snowadv.fightinggamep2p.Fragments.PlayerChoiceFragment;
import itacademy.snowadv.fightinggamep2p.Fragments.ServerList.ServerListFragment;
import itacademy.snowadv.fightinggamep2p.Fragments.StartGameFragment;
import itacademy.snowadv.fightinggamep2p.databinding.ActivityMainBinding;

public class MainActivity extends FragmentActivity implements Notifiable {

    /* Variable for double pressing back button to leave */
    private boolean doubleBackToExitPressedOnce = false;

    private static final String TAG = "MainActivty";
    private ActivityMainBinding viewBinding;
    private Fragment currentFragment;
    private GameClientServer clientServer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
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

    private void createTheServer() {
        // TODO: if server is selected - create the server after choosing the player
    }

    private void startTransitionToServerListFragment() {
        // TODO: if client is sele cted - go to the
    }

    private void fallbackToTheStartGameScreen() {
            transitToNewFragment(new StartGameFragment());
            doTheClientServerStopJob();
    }


    private void transitToNewFragment(Fragment fragment) {
        currentFragment = fragment;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container_view_main, fragment, null);
        fragmentTransaction.commit();
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
        Toast.makeText(this, "Нажмите \"Назад\" еще раз, чтобы " + (isOnStartScreen ?
                        "выйти" : "вернуться на начальный экран")
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
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(((DisconnectedEvent) object).showToast) {
                        Toast.makeText(context, "Отключен от сервера",
                                Toast.LENGTH_SHORT).show();
                    }
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
        // TODO: Get rid of role cb
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