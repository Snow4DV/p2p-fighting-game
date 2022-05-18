package itacademy.snowadv.fightinggamep2p.Classes.Server;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

import itacademy.snowadv.fightinggamep2p.Classes.Events.DisconnectedEvent;
import itacademy.snowadv.fightinggamep2p.Classes.NotifiableActivity;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ChatMessage;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ErrorMessagePacket;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameActionRequest;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameStatsPacket;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GetLobbyStatusRequest;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.LobbyStatusUpdateResponse;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ServerConnectionRequest;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.StartTheGameRequest;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.LobbyFragment;
import itacademy.snowadv.fightinggamep2p.MainActivity;

public class GameClient implements GameClientServer {
    private static final int FIXED_PORT_UDP = 54777;
    private static final int FIXED_PORT_TCP = 54555;
    private static final String TAG = "GameClient";

    private final Client client;
    private GameClientListener listener;
    private BattlePlayer player;
    private final LobbyFragment lobbyFragment;
    private Context activity;
    private Toast toast;

    private GameClient(String ip, BattlePlayer player, LobbyFragment lobbyFragment, Context activity) {
        this.player = player;
        this.lobbyFragment = lobbyFragment;
        // this.activity = lobbyFragment.getActivity();
        this.activity = activity;
        listener = new GameClientListener();
        client = new Client();
        GameClientServer.registerClasses(client.getKryo());
        client.addListener(listener);


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    client.start();
                    client.connect(1000, ip, FIXED_PORT_TCP, FIXED_PORT_UDP);
                } catch(IOException ex) {
                    Log.e(TAG, "GameClient failed to connect  to " + ip, ex);
                    if(activity instanceof NotifiableActivity) {
                        ((NotifiableActivity)activity).notifyWithObject(new DisconnectedEvent(true));
                    }
                }
            }
        }).start();


    }

    public void disconnect() {
        client.stop();
    }


    public static GameClient makeConnection(String ip, BattlePlayer player,
                                            LobbyFragment lobbyFragment, Context activity) {
        return new GameClient(ip, player, lobbyFragment, activity);
    }

    private void disconnected(Connection connection) {
        // TODO: do something when player disconnects
        if(activity instanceof NotifiableActivity) {
            ((NotifiableActivity) activity).notifyWithObject(new DisconnectedEvent(
                    toast != null && toast.getView().isShown()));
        }
    }

    /* Updates the content of lobby fragment */
    private void updateLobbyStatus(LobbyStatusUpdateResponse response) {
        lobbyFragment.updateLobbyStatus(response);
    }



    public void askForLobbyUpdate() {
        client.sendTCP(new GetLobbyStatusRequest());
    }

    @Override
    public void sendChatMessage(ChatMessage chatMessage) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.sendTCP(chatMessage);
            }
        }).start();

    }

    class GameClientListener implements Listener {

        @Override
        public void connected(Connection connection) {
            if(activity instanceof MainActivity) {
                ((MainActivity) activity).runOnUiThread(() -> {
                    Toast.makeText(activity, "Подключено", Toast.LENGTH_SHORT).show();
                });
            }

            Log.d(TAG, "connected: " + connection.toString());
            player.setConnectionID(connection.getID());
            connection.sendTCP(new ServerConnectionRequest(player));
            askForLobbyUpdate();
        }

        @Override
        public void disconnected(Connection connection) {
            Log.d(TAG, "disconnected: " + connection.toString());

            GameClient.this.disconnected(connection);
        }

        @Override
        public void received(Connection connection, Object object) {
            Log.d(TAG, "received: " + object.toString() + " by " + connection.toString());
            if(object instanceof LobbyStatusUpdateResponse) {
                updateLobbyStatus((LobbyStatusUpdateResponse) object);
            } else if(object instanceof ChatMessage) {
                lobbyFragment.addChatMessage((ChatMessage) object);
            } else if(object instanceof ErrorMessagePacket) {
                ErrorMessagePacket error = (ErrorMessagePacket) object;
                ((Activity) activity).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(error.disconnected) {
                            toast = Toast.makeText(activity, "Сервер отключил вас! Причина: " +
                                    error.errorText, Toast.LENGTH_LONG);
                            toast.show();
                        } else {
                            Toast.makeText(activity, "Ошибка на стороне сервера! Содержание: " +
                                    error.errorText, Toast.LENGTH_LONG).show();
                            toast.show();
                        }
                    }
                });

            } else if(object instanceof StartTheGameRequest && activity instanceof NotifiableActivity) {
                ((NotifiableActivity) activity).notifyWithObject(
                        (StartTheGameRequest) object);
            } else if(object instanceof GameStatsPacket && activity instanceof NotifiableActivity){
                ((NotifiableActivity) activity).notifyWithObject((GameStatsPacket)object);
            }
        }
    }

    public BattlePlayer getMyBattlePlayer() {
        return player;
    }


    public void sendPlayerActionToServer(BattlePlayer.BattlePlayerAction action) {
        client.sendTCP(new GameActionRequest(player, action));
    }


}
