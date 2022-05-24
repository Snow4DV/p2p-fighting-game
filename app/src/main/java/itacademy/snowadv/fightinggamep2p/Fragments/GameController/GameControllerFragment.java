package itacademy.snowadv.fightinggamep2p.Fragments.GameController;

import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import itacademy.snowadv.fightinggamep2p.Classes.Drawables.DrawableBattleUnit;
import itacademy.snowadv.fightinggamep2p.Classes.Server.GameClient;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameStatsPacket;
import itacademy.snowadv.fightinggamep2p.Classes.Server.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.Fragments.PlayerSelectDialogFragment;
import itacademy.snowadv.fightinggamep2p.R;
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
        Toast.makeText(getActivity(),
                "Удерживайте кнопку действия, чтобы узнать, что она делает.",
                Toast.LENGTH_SHORT).show();
        viewBinding.lightKickButton.setOnClickListener(v -> {
            sendPlayerActionWithPlayerSelected(BattlePlayer.BattlePlayerAction.LIGHT_KICK);
        });
        viewBinding.hardKickButton.setOnClickListener(v -> {
            sendPlayerActionWithPlayerSelected(BattlePlayer.BattlePlayerAction.HARD_KICK);
        });
        viewBinding.abilityButton.setOnClickListener(v -> {
            client.sendPlayerActionToServer(BattlePlayer.BattlePlayerAction.ABILITY, null);
            updateButtonsActiveness(false);
        });
        viewBinding.lightKickButton.setOnLongClickListener(v -> {
            String desc = DrawableBattleUnit.getLightAttackDescByBattlePlayerName(
                    client.getMyBattlePlayer().getPlayerName());
            showAlertDialogWithText(desc);
            return false;
        });
        viewBinding.hardKickButton.setOnLongClickListener(v -> {
            String desc = DrawableBattleUnit.getHardAttackDescByBattlePlayerName(
                    client.getMyBattlePlayer().getPlayerName());
            showAlertDialogWithText(desc);
            return false;
        });
        viewBinding.abilityButton.setOnLongClickListener(v -> {
            String desc = DrawableBattleUnit.getAbilityDescByBattlePlayerName(
                    client.getMyBattlePlayer().getPlayerName());
            showAlertDialogWithText(desc);
            return false;
        });
        setPreviewPicture(client.getMyBattlePlayer());
        setPlayerName(client.getMyBattlePlayer());
        updateBars();
        setButtonsText();
        return viewBinding.getRoot();
    }

    private void showAlertDialogWithText(String text) {
        if(getActivity() == null) return;
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity(),
                R.style.TransparentDialog);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View customDialog = inflater.inflate(R.layout.alert_dialog_text, null);
        alertDialogBuilder.setView(customDialog);
        AlertDialog alertDialog = alertDialogBuilder.show();
        TextView titleView = alertDialog.findViewById(R.id.message);
        titleView.setText(text);
    }

    public void sendPlayerActionWithPlayerSelected(BattlePlayer.BattlePlayerAction action) {
        if(getActivity() == null) {
            return;
        }
        PlayerSelectDialogFragment dialog = new PlayerSelectDialogFragment(
                gameStatsPacket.getPlayersList(), selectedPlayer -> {
            Log.d(TAG, "sendPlayerActionWithPlayerSelected: " + selectedPlayer);
                    client.sendPlayerActionToServer(action, selectedPlayer);
                    updateButtonsActiveness();
        });
        dialog.show(getActivity().getSupportFragmentManager(), "SelectPlayerDialogTag");
    }

    /**
     * Updates health/stamina bars of controller
     */
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
        if(getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                viewBinding.hpBar.setLayoutParams(new FrameLayout.LayoutParams(hpBarWidthPx, viewBinding.hpBar.getLayoutParams().height));
                viewBinding.staminaBar.setLayoutParams(new FrameLayout.LayoutParams(staminaBarWidthPx, viewBinding.staminaBar.getLayoutParams().height));
            });
        }
        Log.d(TAG, "updateBars: " + "staminabarwidthPx:" + staminaBarWidthPx + ",staminabarwidth:" + staminaBarWidth);
    }

    private void setPreviewPicture(BattlePlayer player) {
        viewBinding.playerPreview.setImageDrawable(ResourcesCompat.getDrawable(
                getActivity().getResources(), player.getPlayerName().drawableId, null));
    }

    private void setPlayerName(BattlePlayer player) {
        viewBinding.playerNameText.setText(player.getName());
    }

    private void updateButtonsActiveness() {
        boolean areButtonsActive = client.getMyBattlePlayer().getAbilityToStep();
        if (getActivity() == null) return;
        getActivity().runOnUiThread(() -> {
            viewBinding.lightKickButton.setEnabled(areButtonsActive);
            viewBinding.hardKickButton.setEnabled(areButtonsActive);
            viewBinding.abilityButton.setEnabled(areButtonsActive);
        });
    }


    private void updateButtonsActiveness(boolean areButtonsActive) {
        areButtonsActive = areButtonsActive && client.getMyBattlePlayer().isAlive();
        if(getActivity() == null) return;
        boolean finalAreButtonsActive = areButtonsActive;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewBinding.lightKickButton.setEnabled(finalAreButtonsActive);
                viewBinding.hardKickButton.setEnabled(finalAreButtonsActive);
                viewBinding.abilityButton.setEnabled(finalAreButtonsActive);
            }
        });
    }

    private void setButtonsText() {
        viewBinding.lightKickButton.setText(DrawableBattleUnit.getLightAttackNameByBattlePlayerName(
                client.getMyBattlePlayer().getPlayerName()));
        viewBinding.hardKickButton.setText(DrawableBattleUnit.getHardAttackNameByBattlePlayerName(
                client.getMyBattlePlayer().getPlayerName()));
        viewBinding.abilityButton.setText(DrawableBattleUnit.getAbilityNameByBattlePlayerName(
                client.getMyBattlePlayer().getPlayerName()));
    }






    public void updateGameStatsPacket(GameStatsPacket gameStatsPacket) {
        this.gameStatsPacket = gameStatsPacket;
        updateBars();
        Log.d(TAG, "updateGameStatsPacket: " + gameStatsPacket.toString());
        updateButtonsActiveness();
    }




}
