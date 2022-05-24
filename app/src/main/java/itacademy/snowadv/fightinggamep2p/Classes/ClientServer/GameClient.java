package itacademy.snowadv.fightinggamep2p.Classes.ClientServer;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

import itacademy.snowadv.fightinggamep2p.Classes.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.Classes.Events.DisconnectedEvent;
import itacademy.snowadv.fightinggamep2p.Classes.Notifiable;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.ChatMessage;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.ErrorMessagePacket;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.GameActionRequest;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.GameStatsPacket;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.GetLobbyStatusRequest;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.LobbyStatusUpdateResponse;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.ServerConnectionRequest;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.StartTheGameRequest;
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
    private GameStatsPacket gameStatsPacket;
    private boolean isInterrupted = false;

    private GameClient(String ip, BattlePlayer player, LobbyFragment lobbyFragment, Context activity) {
        this.player = player; // TODO: redurant. remove
        this.lobbyFragment = lobbyFragment;
        this.activity = activity;
        listener = new GameClientListener();
        client = new Client();


        new Thread(() -> {
                GameClientServer.registerClasses(client.getKryo());
                client.addListener(listener);
                client.start();
            try {
                client.connect(2000, ip, FIXED_PORT_TCP, FIXED_PORT_UDP);
            } catch (IOException e) {
                Log.e(TAG, "GameClient failed to connect  to " + ip, e);
                if (activity instanceof Notifiable) {
                    ((Notifiable) activity).notifyWithObject(new DisconnectedEvent(true));
                }
            }
        }).start();


    }

    public void disconnect() {
        // Should be ran in a separate thread. Kryonet is a mess.
        new Thread(client::stop).start();
        isInterrupted = true;
    }


    public static GameClient makeConnection(String ip, BattlePlayer player,
                                            LobbyFragment lobbyFragment, Context activity) {
        return new GameClient(ip, player, lobbyFragment, activity);
    }

    private void disconnected(Connection connection) {
        if(activity instanceof Notifiable && !isInterrupted) {
            ((Notifiable) activity).notifyWithObject(new DisconnectedEvent(
                    toast != null && toast.getView().isShown()));
        }
    }

    /* Updates the content of lobby fragment */
    private void updateLobbyStatus(LobbyStatusUpdateResponse response) {
        lobbyFragment.updateLobbyStatus(response);
    }



    public void askForLobbyUpdate() {
        new Thread(() -> client.sendTCP(new GetLobbyStatusRequest())).start();

    }



    @Override
    public void sendChatMessage(ChatMessage chatMessage) {
        sendObjectAsync(chatMessage);

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
            GameClientServer.sendTCPToConnectionAsync(connection,
                    new ServerConnectionRequest(player));
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

            } else if(object instanceof StartTheGameRequest && activity instanceof Notifiable) {
                ((Notifiable) activity).notifyWithObject(
                        (StartTheGameRequest) object);
                gameStatsPacket = ((StartTheGameRequest)object).gameStatsPacket;
            } else if(object instanceof GameStatsPacket && activity instanceof Notifiable){
                ((Notifiable) activity).notifyWithObject((GameStatsPacket)object);
                gameStatsPacket = ((GameStatsPacket)object);
            }
        }
    }

    public BattlePlayer getMyBattlePlayer() {
        if(gameStatsPacket == null) {
            return player; // Return unassigned BattlePlayer
        }
        for (BattlePlayer player:
                gameStatsPacket.getPlayersList()) {
            if(player.getConnectionID() == client.getID()) {
                return player;
            }
        }
        Log.e(TAG, "getMyBattlePlayer: player with such connection id not found:" +
                client.getID());
        return null;
    }

    private void sendObjectAsync(Object object) {
        new Thread(() -> client.sendTCP(object)).start();
    }

    public void sendPlayerActionToServer(BattlePlayer.BattlePlayerAction action, BattlePlayer
            affectedPlayer) {
        sendObjectAsync(new GameActionRequest(getMyBattlePlayer(), action, affectedPlayer));
    }


}
