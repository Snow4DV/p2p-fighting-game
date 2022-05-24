package itacademy.snowadv.fightinggamep2p.Fragments.Lobby;

import android.content.pm.ActivityInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Formatter;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import itacademy.snowadv.fightinggamep2p.Classes.Notifiable;
import itacademy.snowadv.fightinggamep2p.Classes.Server.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.Classes.Server.GameClient;
import itacademy.snowadv.fightinggamep2p.Classes.Server.GameClientServer;
import itacademy.snowadv.fightinggamep2p.Classes.Server.GameServer;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ChatMessage;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.LobbyStatusUpdateResponse;
import itacademy.snowadv.fightinggamep2p.Fragments.PlayerChoiceFragment;
import itacademy.snowadv.fightinggamep2p.R;
import itacademy.snowadv.fightinggamep2p.databinding.FragmentLobbyBinding;

import static android.content.Context.WIFI_SERVICE;
import static android.view.View.GONE;

public class LobbyFragment extends Fragment {


    private FragmentLobbyBinding viewBinding;
    private PlayerChoiceFragment.RoleChoiceCallbackResult role;

    private GameClientServer clientServer;
    private boolean isServer = false;
    private static final String TAG = "LobbyFragment";

    private String ip;
    private BattlePlayer player;

    /**
     * Client constructor
     * @param ip Ip of the server
     * @param player Created battleplayer
     */
    public LobbyFragment(String ip,
                         BattlePlayer player) {
        this.role = PlayerChoiceFragment.RoleChoiceCallbackResult.CLIENT;
        this.ip = ip;
        this.player = player;
    }

    /**
     * Server constructor
     */
    public LobbyFragment() {
        this.role = PlayerChoiceFragment.RoleChoiceCallbackResult.SERVER;
        isServer = true;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewBinding = FragmentLobbyBinding.inflate(inflater, container, false);
        if(isServer) {
            doServerJob();
            viewBinding.chatText.setMovementMethod(new ScrollingMovementMethod());
        } else {
            doClientJob();
        }
        if(getActivity() instanceof Notifiable) {
            ((Notifiable)getActivity()).notifyWithObject(clientServer);
        }
        updateIpAddress();
        viewBinding.sendMessageButton.setOnClickListener(v -> {
            clientServer.sendChatMessage(new ChatMessage(player,
                    viewBinding.chatEditText.getText().toString()));
        });
        viewBinding.lobbyStartServerButton.setOnClickListener(v -> {
            if(getServer().getEvilPlayersAmount() <= 0 || getServer().getKindPlayersAmount() <= 0) {
                showAlertDialogWithText("Не хватает игроков. На каждой стороне должно быть минимум " +
                        "по одному игроку.");
                Log.d(TAG, "onCreateViewFailedToStartServer: " +
                        getServer().getPlayersAsString());
                return;
            }
           getServer().startGame();
        });
        // Lock orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        // Hide start server box for clients
        if(getServer() == null) {
            viewBinding.serverBox.setVisibility(GONE);
        }
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public void updateLobbyStatus(LobbyStatusUpdateResponse response) {
        if(getActivity() == null){
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
        StringBuilder roomText = new StringBuilder("Хост: <u>" + response.getHostIP() +
                "</u><br>Игроки (" + response.players.size() + "/6):");
        for(int i = 0; i < response.players.size(); i++) {
            roomText.append((i == 0) ? "" : ", ").append("<u>")
                    .append(response.players.get(i).getName()).append("</u>");
        }

                viewBinding.playersText.setText(Html.fromHtml(String.valueOf(roomText), 0));
            }
        });

    }

    public void addChatMessage(ChatMessage chatMessage) {
        BattlePlayer sender = chatMessage.player;
        if(getActivity() == null){
            return;
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String chatText = viewBinding.chatText.getText().toString() + '\n'
                        + ((sender == null) ? "СЕРВЕР" : sender.getName()) + ':' + ' ' + chatMessage.text;
                viewBinding.chatText.setText(chatText);
                // Scroll to the bottom
                viewBinding.chatScrollView.scrollTo(0,viewBinding.chatScrollView.getWidth()*2);
                // Clear the chat edit text
                viewBinding.chatEditText.setText("");
            }
        });


    }


    private void updateIpAddress() {
        // FIX: Get host address is broken (keeps getting 127.0.0.1). Using IPv4-only method
        WifiManager wm = (WifiManager) getActivity().getSystemService(WIFI_SERVICE);
        String ipAddress = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        viewBinding.ipAddressText.setText(ipAddress);
    }

    private void doServerJob() {
        clientServer = GameServer.startServer(this);
        // TODO: go to the controller fragment on start
    }
    private void doClientJob() {
        clientServer = GameClient.makeConnection(ip, player, this, getActivity());
        // TODO: go to the battlefield fragment on start
    }

    private @Nullable GameClient getClient() {
        if(clientServer instanceof  GameClient) {
            return (GameClient) clientServer;
        }
        return null;
    }
    private @Nullable GameServer getServer() {
        if(clientServer instanceof  GameServer) {
            return (GameServer) clientServer;
        }
        return null;
    }







}