package itacademy.snowadv.fightinggamep2p.Fragments.BattleField;

import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.Server.GameServer;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ChatMessage;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameStatsPacket;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.R;
import itacademy.snowadv.fightinggamep2p.databinding.FragmentBattlefieldBinding;

import static android.view.View.VISIBLE;

public class ServerBattleFragment extends Fragment {
    private BattleFieldSurfaceView battleFieldSurfaceView;
    private FragmentBattlefieldBinding viewBinding;


    private GameServer gameServer;

    public ServerBattleFragment(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public ServerBattleFragment() {
        // Non-arg constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        battleFieldSurfaceView = new BattleFieldSurfaceView(getContext(), gameServer, this);
        viewBinding = FragmentBattlefieldBinding.inflate(inflater, container, false);
        viewBinding.battlefieldConstraintLayout.addView(battleFieldSurfaceView);
        battleFieldSurfaceView.setElevation(-1f);
        viewBinding.gameLogText.setMovementMethod(new ScrollingMovementMethod());
        return viewBinding.getRoot();
    }


    /**
     * Sets visibility of the hud on UI thread
     * @param visibility Pass GONE or VISIBLE
     */
    public void changeHUDVisibility(int visibility) {
        getActivity().runOnUiThread(() -> {
            viewBinding.gameLogBox.setVisibility(VISIBLE);
            viewBinding.hpBox.setVisibility(visibility);
        });

    }

    public void addChatMessageToLog(ChatMessage message) {
        BattlePlayer sender = message.player;
        if(getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String chatText = viewBinding.gameLogText.getText().toString() + '\n'
                        + ((sender == null) ? "СЕРВЕР" : sender.getName()) + ':' + ' ' + message.text;
                viewBinding.gameLogText.setText(chatText);
                // Scroll to the bottom
                final Layout layout = viewBinding.gameLogText.getLayout();
                if (layout != null) {
                    int scrollDelta = layout.getLineBottom(viewBinding.gameLogText.getLineCount() - 1)
                            - viewBinding.gameLogText.getScrollY() - viewBinding.gameLogText.getHeight();
                    if (scrollDelta > 0)
                        viewBinding.gameLogText.scrollBy(0, scrollDelta);
                }
            }
        });
    }

    public void showMessageOnDisplay(String message, int millis) {
        if(getActivity() == null) {
            return;
        }

        getActivity().runOnUiThread(new Runnable() { // Сроки горят :(((
            @Override
            public void run() {
                viewBinding.messageBox.setVisibility(VISIBLE);
                viewBinding.messageBoxText.setText(message);
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(millis);
                } catch(InterruptedException ex) {/*ignored*/}
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        viewBinding.messageBox.setVisibility(VISIBLE);
                    }
                });
            }
        });

    }



    public void addStringToLog(String playerName, String action) {
        String logText = viewBinding.gameLogText.getText().toString() + '\n' + playerName + ": "
                + action;
        viewBinding.gameLogText.setText(logText);
    }


    public void setBalanceHpBar(int evilHP, int kindHP) {

    }





}
