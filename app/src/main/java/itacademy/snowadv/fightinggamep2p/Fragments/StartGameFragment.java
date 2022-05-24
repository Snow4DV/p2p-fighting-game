package itacademy.snowadv.fightinggamep2p.Fragments;


import android.content.res.Configuration;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import itacademy.snowadv.fightinggamep2p.Classes.Notifiable;
import itacademy.snowadv.fightinggamep2p.MainActivity;
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
                if(getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                    if(getActivity() != null) {
                        showAlertDialogWithText("Переверните устройство для того, чтобы" +
                                " запустить сервер. Игровой сервер должен быть запущен на устройстве с " +
                                "ландшафтной ориентацией (лучше всего - на телевизоре или планшете)");
                    }
                    return;
                }
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
            case R.id.add_background_volume:
                viewBinding.backgroundVolume.setText(changeVolume(0.1f) + "%");
                break;
            case R.id.reduce_background_volume:
                viewBinding.backgroundVolume.setText(changeVolume(-0.1f) + "%");
                break;
        }
    }

    /**
     * Changes volume in main activity's SoundPlayer. Will do nothing if instantiated from
     * another class.
     * @param offset Value on which volume multiplier will be changed.
     * @return current volume percent
     */
    private int changeVolume(float offset) {
        if(getActivity() instanceof MainActivity) {
            return ((MainActivity) getActivity()).changeVolume(offset);
        }
        return 0;
    }


    public StartGameFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentStartGameBinding.inflate(inflater, container, false);
        viewBinding.backgroundVolume.setText(changeVolume(0.0f) + "%");
        viewBinding.searchForAGameButton.setOnClickListener(this);
        viewBinding.startServerButton.setOnClickListener(this);
        viewBinding.addBackgroundVolume.setOnClickListener(this);
        viewBinding.reduceBackgroundVolume.setOnClickListener(this);
        viewBinding.soundCredsToButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialogWithText(getString(R.string.sound_creds_to));
            }
        });
        viewBinding.logo.setImageResource(R.drawable.animation_logo);
        AnimationDrawable frameAnimation = (AnimationDrawable) viewBinding.logo.getDrawable();
        frameAnimation.start();
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
