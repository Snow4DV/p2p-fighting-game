package itacademy.snowadv.fightinggamep2p.Classes.Server;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.Events.DisconnectedEvent;
import itacademy.snowadv.fightinggamep2p.Classes.NotifiableActivity;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ChatMessage;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ErrorMessagePacket;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameStatsPacket;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GetLobbyStatusRequest;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.LobbyStatusUpdateResponse;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ServerConnectionRequest;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.StartTheGameRequest;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.LobbyFragment;

import static android.content.Context.WIFI_SERVICE;

public class GameServer implements GameClientServer{
    private static final int FIXED_PORT_UDP = 54777;
    private static final int FIXED_PORT_TCP = 54555;
    private static String HOST_IP = "[ОШИБКА]";
    private static final int EACH_SIDE_MAX_PLAYERS_COUNT = 0;

    private LobbyFragment lobbyFragment;
    private Context activity;
    private GameStatsPacket gameStatsPacket;
    private final Server server = new Server();
    private final GameServerListener listener = new GameServerListener();

    List<BattlePlayer> players = new ArrayList<>();

    private GameServer(LobbyFragment lobbyFragment) {
        this.lobbyFragment = lobbyFragment;
        activity = lobbyFragment.getActivity();
    }

    public static GameServer startServer(LobbyFragment lobbyFragment) {
        GameServer gameServer = new GameServer(lobbyFragment);
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
        LobbyStatusUpdateResponse response = new LobbyStatusUpdateResponse(players, getIpAddress() );
        lobbyFragment.updateLobbyStatus(response);
    }

    private void connectPlayer(BattlePlayer player) {
        players.add(player);
        // If there are already enough player on kind/evil side
        if(checkIfKindOrEvilPlayersAreTooMuch(player.getPlayer().isKind())) {
            disconnectPlayerAndCloseConnection(player, "Достаточно игроков на сторооне " +
                    (player.getPlayer().isKind() ? "добра!" : "зла!") + " Выберите другую сторону.");
        }
        sendLobbyUpdateToEveryone();
    }

    private void disconnectPlayer(BattlePlayer player) {
        players.remove(player);
        sendLobbyUpdateToEveryone();
    }

    private void disconnectPlayerAndCloseConnection(BattlePlayer player, String reason) {
        players.remove(player);
        if(getConnectionByID(player.getConnectionID()) != null) {
            getConnectionByID(player.getConnectionID()).sendTCP(new ErrorMessagePacket(reason, true));
        }
        if(getConnectionByID(player.getConnectionID()) != null) {
            getConnectionByID(player.getConnectionID()).close();
        }
        sendLobbyUpdateToEveryone();
    }
    
    private boolean checkIfKindOrEvilPlayersAreTooMuch(boolean isKind){
        int count = 0;
        for (BattlePlayer player :
                players) {
            if(player.getPlayer().isKind() == isKind) count++;
        }
        if(count > EACH_SIDE_MAX_PLAYERS_COUNT) {
            return true;
        }
        return false;
    }

    private void disconnectPlayer(int connectionID) {
        for(BattlePlayer player: players ) {
            if(player.getConnectionID() == connectionID) {
               disconnectPlayer(player);
               break;
            }
        }
    }


    /* Function to send the lobby status to the players. Run on any player connection */
    private void sendLobbyUpdateToEveryone() {
        updateLobbyInFragment(); // Update the host's lobby
        sendObjectToEveryone(new LobbyStatusUpdateResponse(players, getIpAddress()));
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
                    getConnectionByID(player.getConnectionID())
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

    public void startGame() {
        gameStatsPacket = new GameStatsPacket(players, GameStatsPacket.GamePhase.KIND_MOVE);
        sendObjectToEveryone(new StartTheGameRequest(gameStatsPacket));

    }

    private String getIpAddress() {
        // FIX: Get host address is broken (keeps getting 127.0.0.1). Using IPv4-only method
        WifiManager wm = (WifiManager) activity.getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }


    private boolean isInGame() {
        return gameStatsPacket != null;
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
            if(isInGame()) {
                sendObjectToEveryone(new ErrorMessagePacket(
                        "Один из игроков отключился от сервера", true));
                stopServer();
                if(activity instanceof NotifiableActivity) {
                    ((NotifiableActivity) activity).notifyWithObject(new DisconnectedEvent(false));
                }
            }
        }

        @Override
        public void received(Connection connection, Object object) {
            if(object instanceof ServerConnectionRequest) { // Request of connection
                ServerConnectionRequest request = (ServerConnectionRequest) object;
                connectPlayer(request.player);
            } else if(object instanceof GetLobbyStatusRequest) {
                connection.sendTCP(new LobbyStatusUpdateResponse(players, getIpAddress()));
            } else if(object instanceof ChatMessage) {
                lobbyFragment.addChatMessage((ChatMessage) object);
                sendObjectToEveryone(object);
            }
        }


    }
}
