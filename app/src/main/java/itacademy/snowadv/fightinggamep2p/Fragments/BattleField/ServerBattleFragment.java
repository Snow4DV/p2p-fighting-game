package itacademy.snowadv.fightinggamep2p.Fragments.BattleField;

import android.content.Context;
import android.os.Bundle;
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
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameStatsPacket;
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


    public void addStringToLog(String playerName, String action) {
        String logText = viewBinding.gameLogText.getText().toString() + '\n' + playerName + ": "
                + action;
        viewBinding.gameLogText.setText(logText);
    }


    public void setBalanceHpBar(int evilHP, int kindHP) {

    }





}
