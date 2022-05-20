package itacademy.snowadv.fightinggamep2p.Fragments.BattleField;

import android.content.res.Resources;
import android.os.Bundle;
import android.text.Layout;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import itacademy.snowadv.fightinggamep2p.Classes.Server.GameServer;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ChatMessage;
import itacademy.snowadv.fightinggamep2p.Classes.Server.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.databinding.FragmentBattlefieldBinding;

import static android.view.View.VISIBLE;

public class ServerBattleFragment extends Fragment {
    private BattleFieldSurfaceView battleFieldSurfaceView;
    private FragmentBattlefieldBinding viewBinding;
    private static final String TAG = "ServerBattleFragment";


    private GameServer gameServer;

    public ServerBattleFragment(GameServer gameServer) {
        this.gameServer = gameServer;
    }

    public ServerBattleFragment() {
        // Non-arg constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        battleFieldSurfaceView = new BattleFieldSurfaceView(getContext(), gameServer, this);
        viewBinding = FragmentBattlefieldBinding.inflate(inflater, container, false);
        viewBinding.battlefieldConstraintLayout.addView(battleFieldSurfaceView);
        battleFieldSurfaceView.setElevation(-1f);
        viewBinding.gameLogText.setMovementMethod(new ScrollingMovementMethod());
        return viewBinding.getRoot();
    }


    /**
     * Sets visibility of the hud on UI thread
     * @param visibility Pass GONE or VISIBLE
     */
    public void changeHUDVisibility(int visibility) {
        getActivity().runOnUiThread(() -> {
            viewBinding.gameLogBox.setVisibility(VISIBLE);
            viewBinding.hpBox.setVisibility(visibility);
        });

    }

    public void addChatMessageToLog(ChatMessage message) {
        BattlePlayer sender = message.player;
        if(getActivity() == null) {
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String chatText = viewBinding.gameLogText.getText().toString() + '\n'
                        + ((sender == null) ? "СЕРВЕР" : sender.getName()) + ':' + ' ' + message.text;
                viewBinding.gameLogText.setText(chatText);
                // Scroll to the bottom
                final Layout layout = viewBinding.gameLogText.getLayout();
                if (layout != null) {
                    int scrollDelta = layout.getLineBottom(viewBinding.gameLogText.getLineCount() - 1)
                            - viewBinding.gameLogText.getScrollY() - viewBinding.gameLogText.getHeight();
                    if (scrollDelta > 0)
                        viewBinding.gameLogText.scrollBy(0, scrollDelta);
                }
            }
        });
    }

    /**
     * Shows message in a box on battle fragment [can be ran from any thread]
     * @param message
     * @param millis
     */
    public void showMessageOnDisplay(String message, int millis) {
        if(getActivity() == null) {
            return;
        }

        getActivity().runOnUiThread(() -> {
            viewBinding.messageBox.setVisibility(VISIBLE);
            viewBinding.messageBoxText.setText(message);
        });
        // Thread to close the message box in given time
        new Thread(() -> {
            try{
                Thread.sleep(millis);
                getActivity().runOnUiThread(() -> viewBinding.messageBox.setVisibility(View.GONE));
            } catch(InterruptedException | NullPointerException ex) {
                Log.d(TAG, "showMessageOnDisplay: thrown ex " + ex.getClass().toString() );
            }

        }).start();

    }



    public void addStringToLog(String playerName, String action) {
        String logText = viewBinding.gameLogText.getText().toString() + '\n' + playerName + ": "
                + action;
        viewBinding.gameLogText.setText(logText);
    }


    public void setBalanceHpBar(int evilHP, int kindHP) {
        int kindHpPercent = ((evilHP + kindHP)/100) * evilHP;
        int evilHpPercent = ((evilHP + kindHP)/100) * evilHP;
        int redBarWidth = (int) ((150/100.0)*evilHpPercent);
        int greenBarWidth = (int) (((150/100.0)*(kindHpPercent)));
        // convert dp to px
        Resources r = getResources();
        int redBarWidthPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                redBarWidth,
                r.getDisplayMetrics()
        );
        int greenBarWidthPx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                greenBarWidth,
                r.getDisplayMetrics()
        );
        if(getActivity() != null) {
            getActivity().runOnUiThread(() -> {

                viewBinding.evilBar.setLayoutParams(new FrameLayout.LayoutParams(redBarWidthPx, viewBinding.evilBar.getLayoutParams().height));
                viewBinding.kindBar.setLayoutParams(new FrameLayout.LayoutParams(greenBarWidthPx, viewBinding.kindBar.getLayoutParams().height));
            });
        }
    }





}
