package itacademy.snowadv.fightinggamep2p.Fragments;


import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import itacademy.snowadv.fightinggamep2p.Classes.Notifiable;
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
            case R.id.start_server_button:
                // Notify the activity about starting a server
                if(getActivity() instanceof Notifiable) {
                    ((Notifiable) getActivity())
                            .notifyWithObject(PlayerChoiceFragment
                                    .RoleChoiceCallbackResult.SERVER);
                }
                break;
            case R.id.search_for_a_game_button:
                // Notify the activity about fragment's death
                if(getActivity() instanceof Notifiable) {
                    ((Notifiable) getActivity()).notifyWithObject(
                            PlayerChoiceFragment.RoleChoiceCallbackResult.CLIENT);
                }
                break;
        }
    }


    public StartGameFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentStartGameBinding.inflate(inflater, container, false);
        viewBinding.searchForAGameButton.setOnClickListener(this);
        viewBinding.startServerButton.setOnClickListener(this);
        viewBinding.soundCredsToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogWithText(getString(R.string.sound_creds_to));
            }
        });
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


}
