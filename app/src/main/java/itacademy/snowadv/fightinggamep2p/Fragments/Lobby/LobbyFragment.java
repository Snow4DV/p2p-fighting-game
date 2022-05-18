package itacademy.snowadv.fightinggamep2p.Fragments.Lobby;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Html;
import android.text.format.Formatter;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import itacademy.snowadv.fightinggamep2p.Classes.NotifiableActivity;
import itacademy.snowadv.fightinggamep2p.Classes.Server.GameClient;
import itacademy.snowadv.fightinggamep2p.Classes.Server.GameClientServer;
import itacademy.snowadv.fightinggamep2p.Classes.Server.GameServer;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ChatMessage;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.LobbyStatusUpdateResponse;
import itacademy.snowadv.fightinggamep2p.Fragments.PlayerChoiceFragment;
import itacademy.snowadv.fightinggamep2p.databinding.FragmentLobbyBinding;

import static android.content.Context.WIFI_SERVICE;

public class LobbyFragment extends Fragment {


    private FragmentLobbyBinding viewBinding;
    private PlayerChoiceFragment.RoleChoiceCallbackResult role;

    private GameClientServer clientServer;
    private boolean isServer = false;

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
        } else {
            doClientJob();
        }
        if(getActivity() instanceof NotifiableActivity) {
            ((NotifiableActivity)getActivity()).notifyWithObject(clientServer);
        }
        updateIpAddress();
        viewBinding.sendMessageButton.setOnClickListener(v -> {
            clientServer.sendChatMessage(new ChatMessage(player,
                    viewBinding.chatEditText.getText().toString()));
        });
        viewBinding.lobbyStartServerButton.setOnClickListener(v -> {
           getServer().startGame();
        });
        // Lock orientation
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        return viewBinding.getRoot();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    public void updateLobbyStatus(LobbyStatusUpdateResponse response) {
        StringBuilder roomText = new StringBuilder("Хост: <u>" + response.getHostIP() +
                "</u><br>Игроки (" + response.players.size() + "/6):");
        for(int i = 0; i < response.players.size(); i++) {
            roomText.append((i == 0) ? "" : ", ").append("<u>")
                    .append(response.players.get(i).getName()).append("</u>");
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewBinding.playersText.setText(Html.fromHtml(String.valueOf(roomText), 0));
            }
        });

    }

    public void addChatMessage(ChatMessage chatMessage) {
        BattlePlayer sender = chatMessage.player;
        String chatText = viewBinding.chatText.getText().toString() + '\n'
                + ((sender == null) ? "СЕРВЕР" : sender.getName()) + ':' + ' ' + chatMessage.text;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                viewBinding.chatText.setText(chatText);
            }
        });

        // Scroll to the bottom
        viewBinding.chatScrollView.scrollTo(0,viewBinding.chatScrollView.getWidth()*2);
        // Clear the chat edit text
        viewBinding.chatEditText.setText("");
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