package itacademy.snowadv.fightinggamep2p.Classes.Server;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ChatMessage;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GetLobbyStatusRequest;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.LobbyStatusUpdateResponse;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ServerConnectionRequest;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.LobbyFragment;

public class GameServer implements GameClientServer{
    private static final int FIXED_PORT_UDP = 54777;
    private static final int FIXED_PORT_TCP = 54555;

    private LobbyFragment lobbyFragment;
    private Context activity;
    private final Server server = new Server();
    private final GameServerListener listener = new GameServerListener();

    List<BattlePlayer> players = new ArrayList<>();
    BattlePlayer hostPlayer;

    private GameServer(LobbyFragment lobbyFragment, BattlePlayer hostPlayer) {
        this.lobbyFragment = lobbyFragment;
        this.hostPlayer = hostPlayer;
        activity = lobbyFragment.getActivity();
    }

    public static GameServer startServer(LobbyFragment lobbyFragment, BattlePlayer hostPlayer) {
        GameServer gameServer = new GameServer(lobbyFragment, hostPlayer);
        GameClientServer.registerClasses(gameServer.server.getKryo());
        try {
            gameServer.server.start();
            gameServer.server.bind(FIXED_PORT_TCP, FIXED_PORT_UDP);
            gameServer.server.addListener(gameServer.listener);
        } catch(IOException ex) {
            Toast.makeText(gameServer.activity, "Ошибка при запуске сервера." +
                            " Скорее всего, нужные порты заняты. Перезагрузите устройство."
                    , Toast.LENGTH_SHORT).show();
        }
        gameServer.updateLobbyInFragment();

        return gameServer;
    }

    public void stopServer() {
        server.stop();
    }


    private void updateLobbyInFragment() {
        LobbyStatusUpdateResponse response = new LobbyStatusUpdateResponse(players, hostPlayer);
        lobbyFragment.updateLobbyStatus(response);
    }

    private void connectPlayer(BattlePlayer player) {
        players.add(player);
        sendLobbyUpdateToEveryone();
    }

    private void disconnectPlayer(BattlePlayer player) {
        players.remove(player);
        sendLobbyUpdateToEveryone();
    }

    private void disconnectPlayer(int connectionID) {
        for(BattlePlayer player: players ) {
            if(player.connectionID == connectionID) {
               disconnectPlayer(player);
               break;
            }
        }
    }


    /* Function to send the lobby status to the players. Run on any player connection */
    private void sendLobbyUpdateToEveryone() {
        updateLobbyInFragment(); // Update the host's lobby
        sendObjectToEveryone(new LobbyStatusUpdateResponse(players, hostPlayer));
    }


    private Connection getConnectionByID(int id) {
        for(Connection connection: server.getConnections()) {
            if(connection.getID() == id) {
                return connection;
            }
        }
        return null;
    }

    private void sendObjectToEveryone(Object object) {
        for (BattlePlayer player :
                players) {


            new Thread(new Runnable() { // Bad practice
                @Override
                public void run() {
                    getConnectionByID(player.connectionID)
                            .sendTCP(object);
                }
            }).start();

        }
    }



    @Override
    public void sendChatMessage(ChatMessage chatMessage) {
        sendObjectToEveryone(chatMessage);
        lobbyFragment.addChatMessage(chatMessage);
    }

    class  GameServerListener implements Listener {
        private static final String TAG = "GameServerListener";
        @Override
        public void connected(Connection connection) {
            Log.d(TAG, "connected: " + connection.toString());
        }

        @Override
        public void disconnected(Connection connection) {
            Log.d(TAG, "disconnected: " + connection.toString());
            disconnectPlayer(connection.getID());
        }

        @Override
        public void received(Connection connection, Object object) {
            if(object instanceof ServerConnectionRequest) { // Request of connection
                ServerConnectionRequest request = (ServerConnectionRequest) object;
                connectPlayer(request.player);
            } else if(object instanceof GetLobbyStatusRequest) {
                connection.sendTCP(new LobbyStatusUpdateResponse(players, hostPlayer));
            } else if(object instanceof ChatMessage) {
                lobbyFragment.addChatMessage((ChatMessage) object);
                sendObjectToEveryone(object);
            }
        }

    }
}
