package itacademy.snowadv.fightinggamep2p.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import itacademy.snowadv.fightinggamep2p.Classes.Server.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.Fragments.ServerList.Callback2;
import itacademy.snowadv.fightinggamep2p.databinding.FragmentPlayerChoiceBinding;
import itacademy.snowadv.fightinggamep2p.R;

public class PlayerChoiceFragment extends Fragment {


    public enum RoleChoiceCallbackResult {CLIENT, SERVER}

    private FragmentPlayerChoiceBinding viewBinding;
    private static BattlePlayer.BattlePlayerName playerName = BattlePlayer.BattlePlayerName.SCHOOLBOY;

    private Callback2<BattlePlayer, RoleChoiceCallbackResult> callbackAfterChoosingAPlayer;

    public PlayerChoiceFragment(Callback2<BattlePlayer, RoleChoiceCallbackResult> callbackAfterChoosingAPlayer) {
        this.callbackAfterChoosingAPlayer = callbackAfterChoosingAPlayer;
    }

    public PlayerChoiceFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentPlayerChoiceBinding.inflate(inflater, container, false);
        View.OnClickListener buttonsClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()) {
                    case R.id.next_player_button:
                        viewBinding.playerPreview.setImageDrawable(ResourcesCompat.getDrawable(getResources(), getNextPlayerAndGetDrawableId(), null));
                        break;
                    case R.id.previous_player_button:
                        viewBinding.playerPreview.setImageDrawable(ResourcesCompat.getDrawable(getResources(), getPrevPlayerAndGetDrawableId(), null));
                        break;
                    case R.id.search_for_a_game_button:
                        if(viewBinding.nameEditText.length() == 0) {
                            Toast.makeText(getActivity(), "Имя не должно быть пустым",
                                    Toast.LENGTH_SHORT).show();
                            return;
                        }
                        callbackAfterChoosingAPlayer.evaluate(new BattlePlayer(playerName,
                                viewBinding.nameEditText.getText().toString()),
                                        RoleChoiceCallbackResult.CLIENT);
                }
            }
        };
        viewBinding.searchForAGameButton.setOnClickListener(buttonsClickListener);
        viewBinding.nextPlayerButton.setOnClickListener(buttonsClickListener);
        viewBinding.previousPlayerButton.setOnClickListener(buttonsClickListener);
        return viewBinding.getRoot();
    }



    private int getNextPlayerAndGetDrawableId() {
        playerName = playerName.next();
        return playerName.drawableId;
    }
    private int getPrevPlayerAndGetDrawableId() {
        playerName = playerName.prev();
        return playerName.drawableId;
    }
}
