package itacademy.snowadv.fightinggamep2p.Fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import itacademy.snowadv.fightinggamep2p.R;
import itacademy.snowadv.fightinggamep2p.databinding.FragmentServerListBinding;

public class ServerListFragment extends Fragment {
    private FragmentServerListBinding viewBinding;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentServerListBinding.inflate(inflater);
        return viewBinding.getRoot();
    }
}
