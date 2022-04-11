package itacademy.snowadv.fightinggamep2p.Fragments;


import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import itacademy.snowadv.fightinggamep2p.R;
import itacademy.snowadv.fightinggamep2p.databinding.FragmentRoleChoiceBinding;

public class RoleChoiceFragment extends Fragment implements View.OnClickListener{

    /*
    Fragment that is made to choose the role of device
     */

    private static final String TAG = "RoleChoiceFragment";
    private FragmentRoleChoiceBinding viewBinding;

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.serverButton:
            case R.id.clientButton:
                Log.d(TAG, "Choice button clicked");
                if(roleChoiceCallback != null){
                    roleChoiceCallback.onCallBack(view.getId() == R.id.serverButton ?
                            RoleChoiceCallbackResult.SERVER : RoleChoiceCallbackResult.CLIENT);
                }
                try {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().remove(this).commit();
                } catch(NullPointerException ex) {
                    Log.d(TAG, "getSupportFragmentManager is null by some reason");
                }
                break;
        }
    }

    public enum RoleChoiceCallbackResult {CLIENT, SERVER}
    private RoleChoiceCallback roleChoiceCallback;

    public RoleChoiceFragment(RoleChoiceCallback roleChoiceCallback) {
        this.roleChoiceCallback = roleChoiceCallback;
    }

    public RoleChoiceFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentRoleChoiceBinding.inflate(inflater, container, false);
        viewBinding.clientButton.setOnClickListener(this);
        viewBinding.serverButton.setOnClickListener(this);
        return viewBinding.getRoot();
    }


    /*
    Callback for the Role choice fragment. Might be defined via constructor.
     */
    public interface RoleChoiceCallback {
        void onCallBack(RoleChoiceCallbackResult callbackResult);
    }

}
