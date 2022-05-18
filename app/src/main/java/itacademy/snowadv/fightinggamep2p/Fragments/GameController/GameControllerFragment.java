package itacademy.snowadv.fightinggamep2p.Fragments.GameController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import itacademy.snowadv.fightinggamep2p.Classes.Server.GameClient;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameStatsPacket;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.databinding.FragmentGameControllerBinding;

public class GameControllerFragment extends Fragment {
    FragmentGameControllerBinding viewBinding;
    private GameClient client;
    private GameStatsPacket gameStatsPacket;

    public GameControllerFragment(GameClient client, GameStatsPacket gameStatsPacket) {
        this.client = client;
        this.gameStatsPacket = gameStatsPacket;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentGameControllerBinding.inflate(inflater, container, false);
        viewBinding.lightKickButton.setOnClickListener(v -> {
            client.sendPlayerActionToServer(BattlePlayer.BattlePlayerAction.LIGHT_KICK);
        });
        viewBinding.hardKickButton.setOnClickListener(v -> {
            client.sendPlayerActionToServer(BattlePlayer.BattlePlayerAction.HARD_KICK);
        });
        viewBinding.hardKickButton.setOnClickListener(v -> {
            client.sendPlayerActionToServer(BattlePlayer.BattlePlayerAction.ABILITY);
        });
        setPreviewPicture(client.getMyBattlePlayer());
        return viewBinding.getRoot();
    }


    private void setPreviewPicture(BattlePlayer player) {
        viewBinding.playerPreview.setImageDrawable(ResourcesCompat.getDrawable(
                getActivity().getResources(), player.getPlayer().drawableId, null));
    }



    public void updateGameStatsPacket(GameStatsPacket gameStatsPacket) {
        this.gameStatsPacket = gameStatsPacket;
    }




}
