package itacademy.snowadv.fightinggamep2p.Fragments.GameController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import itacademy.snowadv.fightinggamep2p.Classes.Server.GameClient;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameStatsPacket;
import itacademy.snowadv.fightinggamep2p.databinding.FragmentGameControllerBinding;

public class GameControllerFragment extends Fragment {
    FragmentGameControllerBinding viewBinding;
    private GameClient client;
    private GameStatsPacket gameStatsPacket;

    public GameControllerFragment(GameClient client) {
        this.client = client;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentGameControllerBinding.inflate(inflater, container, false);
        return viewBinding.getRoot();
    }
}
