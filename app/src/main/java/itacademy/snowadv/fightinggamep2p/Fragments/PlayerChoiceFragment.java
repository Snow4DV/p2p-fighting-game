package itacademy.snowadv.fightinggamep2p.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.databinding.FragmentPlayerChoiceBinding;
import itacademy.snowadv.fightinggamep2p.R;

public class PlayerChoiceFragment extends Fragment {




    private FragmentPlayerChoiceBinding viewBinding;
    private static BattlePlayer.BattlePlayerName player = BattlePlayer.BattlePlayerName.SCHOOLBOY;

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
                    case R.id.accept_player_button:
                        connectToServer();
                }
            }
        };
        return viewBinding.getRoot();
    }

    private void connectToServer() {
        // TODO: Server connection job  
    }


    private int getNextPlayerAndGetDrawableId() {
        player = player.next();
        return player.drawableId;
    }
    private int getPrevPlayerAndGetDrawableId() {
        player = player.prev();
        return player.drawableId;
    }
}
