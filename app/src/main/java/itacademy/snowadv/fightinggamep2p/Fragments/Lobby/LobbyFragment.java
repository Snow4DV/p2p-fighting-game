package itacademy.snowadv.fightinggamep2p.Fragments.Lobby;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.LobbyStatusUpdateResponse;
import itacademy.snowadv.fightinggamep2p.databinding.FragmentLobbyBinding;

public class LobbyFragment extends Fragment {


    private FragmentLobbyBinding viewBinding;




    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentLobbyBinding.inflate(inflater);

        return viewBinding.getRoot();
    }


    public void updateLobbyStatus(LobbyStatusUpdateResponse response) {
        StringBuilder roomText = new StringBuilder("Хост: " + response.host.name + "\nИгроки: ");
        for(BattlePlayer player: response.players) {
            roomText.append(player.name).append('\n');
        }
        viewBinding.roomText.setText(roomText);
    }



}