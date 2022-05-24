package itacademy.snowadv.fightinggamep2p.Fragments;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.Server.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.Fragments.ServerList.Callback;
import itacademy.snowadv.fightinggamep2p.R;

public class PlayerSelectDialogFragment extends DialogFragment {

    private ListView listView;
    private List<BattlePlayer> players;
    private Callback<BattlePlayer> onBattlePlayerSelectedCallback;
    public PlayerSelectDialogFragment() {
        // Needed to implement DialogFragment
    }

    public PlayerSelectDialogFragment(List<BattlePlayer> players, Callback<BattlePlayer> onBattlePlayerSelectedCallback) {
        this.players = players;
        this.onBattlePlayerSelectedCallback = onBattlePlayerSelectedCallback;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return inflater.inflate(R.layout.fragment_player_select_dialog, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listView = view.findViewById(R.id.players_list);
        List<String> playersNames = new ArrayList<>();
        for (BattlePlayer player :
                players) {
            playersNames.add(player.getName());
        }
        ArrayAdapter<String> arrayAdapter
                = new ArrayAdapter<>(getContext(), R.layout.player_list_item, playersNames);
        DialogFragment thisDialogFragment = this;
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            onBattlePlayerSelectedCallback.evaluate(players.get(position));
            thisDialogFragment.dismiss();
        });
        listView.setAdapter(arrayAdapter);
    }
}