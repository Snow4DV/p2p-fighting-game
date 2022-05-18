package itacademy.snowadv.fightinggamep2p.Fragments.GameController;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import itacademy.snowadv.fightinggamep2p.Classes.DrawableBattleUnits.DrawableBattleUnit;
import itacademy.snowadv.fightinggamep2p.Classes.Server.GameClient;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameStatsPacket;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.Fragments.PlayerSelectDialogFragment;
import itacademy.snowadv.fightinggamep2p.Fragments.ServerList.Callback;
import itacademy.snowadv.fightinggamep2p.databinding.FragmentGameControllerBinding;

public class GameControllerFragment extends Fragment {
    FragmentGameControllerBinding viewBinding;
    private GameClient client;
    private GameStatsPacket gameStatsPacket;
    private static final String TAG = "GameControllerFragment";

    public GameControllerFragment(GameClient client, GameStatsPacket gameStatsPacket) {
        this.client = client;
        this.gameStatsPacket = gameStatsPacket;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentGameControllerBinding.inflate(inflater, container, false);
        viewBinding.lightKickButton.setOnClickListener(v -> {
            sendPlayerActionWithPlayerSelected(BattlePlayer.BattlePlayerAction.LIGHT_KICK);
        });
        viewBinding.hardKickButton.setOnClickListener(v -> {
            sendPlayerActionWithPlayerSelected(BattlePlayer.BattlePlayerAction.HARD_KICK);
        });
        viewBinding.hardKickButton.setOnClickListener(v -> {
            client.sendPlayerActionToServer(BattlePlayer.BattlePlayerAction.ABILITY, null);
        });
        setPreviewPicture(client.getMyBattlePlayer());
        setPlayerName(client.getMyBattlePlayer());
        updateBars();
        setButtonsText();
        return viewBinding.getRoot();
    }

    public void sendPlayerActionWithPlayerSelected(BattlePlayer.BattlePlayerAction action) {
        if(getActivity() == null) {
            return;
        }
        PlayerSelectDialogFragment dialog = new PlayerSelectDialogFragment(
                gameStatsPacket.getPlayersList(), selectedPlayer -> {
            Log.d(TAG, "sendPlayerActionWithPlayerSelected: " + selectedPlayer);
                    client.sendPlayerActionToServer(action, selectedPlayer);
            updateButtonsActiveness(false);
        });
        dialog.show(getActivity().getSupportFragmentManager(), "SelectPlayerDialogTag");
    }

    private void updateBars() {
        int hpBarWidth = (int) ((130.0/100.0)*(client.getMyBattlePlayer()
                        .getHealth()));
        int staminaBarWidth = (int) ((130.0/100.0)*(client.getMyBattlePlayer()
                .getStamina()));
        // convert dp to px
        Resources r = getResources();
        int hpBarWidthPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                hpBarWidth,
                r.getDisplayMetrics()
        );
        int staminaBarWidthPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                staminaBarWidth,
                r.getDisplayMetrics()
        );
        viewBinding.hpBar.getLayoutParams().width = hpBarWidthPx;
        viewBinding.staminaBar.getLayoutParams().width = staminaBarWidthPx;
    }

    private void setPreviewPicture(BattlePlayer player) {
        viewBinding.playerPreview.setImageDrawable(ResourcesCompat.getDrawable(
                getActivity().getResources(), player.getPlayer().drawableId, null));
    }

    private void setPlayerName(BattlePlayer player) {
        viewBinding.playerNameText.setText(player.getName());
    }

    private void updateButtonsActiveness() {
        boolean areButtonsActive = (gameStatsPacket.getPhase()
                == GameStatsPacket.GamePhase.KIND_MOVE) == client.getMyBattlePlayer().getPlayer()
                .isKind();
        viewBinding.lightKickButton.setEnabled(areButtonsActive);
        viewBinding.hardKickButton.setEnabled(areButtonsActive);
        viewBinding.abilityButton.setEnabled(areButtonsActive);
    }

    private void updateButtonsActiveness(boolean areButtonsActive) {
        viewBinding.lightKickButton.setEnabled(areButtonsActive);
        viewBinding.hardKickButton.setEnabled(areButtonsActive);
        viewBinding.abilityButton.setEnabled(areButtonsActive);
    }

    private void setButtonsText() {
        viewBinding.lightKickButton.setText(DrawableBattleUnit.getLightAttackNameByBattlePlayerName(
                client.getMyBattlePlayer().getPlayer()));
        viewBinding.hardKickButton.setText(DrawableBattleUnit.getHardAttackNameByBattlePlayerName(
                client.getMyBattlePlayer().getPlayer()));
        viewBinding.abilityButton.setText(DrawableBattleUnit.getAbilityNameByBattlePlayerName(
                client.getMyBattlePlayer().getPlayer()));
    }






    public void updateGameStatsPacket(GameStatsPacket gameStatsPacket) {
        this.gameStatsPacket = gameStatsPacket;
        updateBars();
        updateButtonsActiveness();
    }




}
