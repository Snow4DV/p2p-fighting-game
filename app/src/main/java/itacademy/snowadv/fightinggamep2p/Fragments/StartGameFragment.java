package itacademy.snowadv.fightinggamep2p.Fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import itacademy.snowadv.fightinggamep2p.Classes.NotifiableActivity;
import itacademy.snowadv.fightinggamep2p.R;
import itacademy.snowadv.fightinggamep2p.databinding.FragmentStartGameBinding;

public class StartGameFragment extends Fragment implements View.OnClickListener{

    /*
    Fragment that is made to choose the role of device
     */

    private static final String TAG = "RoleAndPlayerChoiceFragment";
    private FragmentStartGameBinding viewBinding;

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.start_button:
                // Notify the activity about fragment's death
                if(getActivity() instanceof NotifiableActivity) {
                    ((NotifiableActivity) getActivity()).notifyFragmentIsDone(this);
                }
        }
    }




    public StartGameFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentStartGameBinding.inflate(inflater, container, false);
        viewBinding.startButton.setOnClickListener(this);
        return viewBinding.getRoot();
    }


}
