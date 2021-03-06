package itacademy.snowadv.fightinggamep2p.Classes.ClientServer;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.Toast;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.BattlePlayer;
import itacademy.snowadv.fightinggamep2p.Classes.Events.DisconnectedEvent;
import itacademy.snowadv.fightinggamep2p.Classes.Events.GameStartedEvent;
import itacademy.snowadv.fightinggamep2p.Classes.Notifiable;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.ChatMessage;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.ErrorMessagePacket;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.GameStatsPacket;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.GetLobbyStatusRequest;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.LobbyStatusUpdateResponse;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.ServerConnectionRequest;
import itacademy.snowadv.fightinggamep2p.Classes.ClientServer.Packets.StartTheGameRequest;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.LobbyFragment;
import itacademy.snowadv.fightinggamep2p.Fragments.ServerList.Callback;

import static android.content.Context.WIFI_SERVICE;

public class GameServer implements GameClientServer{
    private static final int FIXED_PORT_UDP = 54777;
    private static final int FIXED_PORT_TCP = 54555;
    private static String HOST_IP = "[ОШИБКА]";
    private static final int EACH_SIDE_MAX_PLAYERS_COUNT = 3;

    private LobbyFragment lobbyFragment;

    private Context activity;
    private GameStatsPacket gameStatsPacket;
    private final Server server = new Server();
    private final GameServerListener listener = new GameServerListener();
    private static final String TAG = "GameServer";

    private Callback<Object> callbackForBattleSurfaceView;

    List<BattlePlayer> players = new ArrayList<>();

    private GameServer(LobbyFragment lobbyFragment) {
        this.lobbyFragment = lobbyFragment;
        activity = lobbyFragment.getActivity();
    }

    public void setCallbackForBattleSurfaceView(Callback<Object> callbackForBattleSurfaceView) {
        this.callbackForBattleSurfaceView = callbackForBattleSurfaceView;
    }

    public static GameServer startServer(LobbyFragment lobbyFragment) {
        GameServer gameServer = new GameServer(lobbyFragment);
        GameClientServer.registerClasses(gameServer.server.getKryo());
        // It should be ran in another thread because some functionality is not async and may do
        // network job in main thread.
        final Activity activityContext = lobbyFragment.getActivity();
        new Thread(() -> {
            try {
                gameServer.server.start();
                gameServer.server.bind(FIXED_PORT_TCP, FIXED_PORT_UDP);
                gameServer.server.addListener(gameServer.listener);
            } catch(IOException ex) {
                if(activityContext != null) {
                    activityContext.runOnUiThread(() -> {
                        Toast.makeText(gameServer.activity, "Ошибка при запуске сервера." +
                                        " Скорее всего, нужные порты заняты. Перезагрузите устройство."
                                , Toast.LENGTH_LONG).show();
                    });

                }
                if(lobbyFragment.getActivity() instanceof Notifiable) {
                    ((Notifiable) lobbyFragment.getActivity())
                            .notifyFragmentIsDone(lobbyFragment);
                }
        }
        }).start();
        gameServer.updateLobbyInFragment();

        return gameServer;
    }


    public void stopServerAndStreamString(String text) {
        sendObjectToEveryone(new ErrorMessagePacket(
                text, true));
        server.stop();
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
        if(checkIfKindOrEvilPlayersAreTooMuch(player.getPlayerName().isKind())) {
            disconnectPlayerAndCloseConnection(player, "Достаточно игроков на сторооне " +
                    (player.getPlayerName().isKind() ? "добра!" : "зла!") + " Выберите другую сторону.");
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
            GameClientServer.sendTCPToConnectionAsync(getConnectionByID(player.getConnectionID()),
                    new ErrorMessagePacket(reason, true));
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
            if(player.getPlayerName().isKind() == isKind) count++;
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


    public void sendUpdatedGameStatsToEveryone() {
        if(gameStatsPacket == null) {
            return;
        }
        sendObjectToEveryone(gameStatsPacket);
        Log.d(TAG, "sendUpdatedGameStatsToEveryone - game stats object: "
                + gameStatsPacket.toString());
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
        ArrayList<BattlePlayer> receiverPlayers = new ArrayList<>(players);
            new Thread(() -> {
                for (BattlePlayer player :
                        receiverPlayers) {
                    try {
                        getConnectionByID(player.getConnectionID())
                                .sendTCP(object);
                    } catch(NullPointerException ex) {
                        Log.e(TAG, "sendObjectToEveryone: Tried to send message, but " +
                                "BattlePlayer died", ex);
                    }
                }


            }).start();
    }



    public GameStatsPacket getGameStatsPacket() {
        return gameStatsPacket;
    }

    public int getPlayersCount() {
        return players.size();
    }

    @Override
    public void sendChatMessage(ChatMessage chatMessage) {
        sendObjectToEveryone(chatMessage);
        lobbyFragment.addChatMessage(chatMessage);
    }

    public void startGame() {
        gameStatsPacket = new GameStatsPacket(players, GameStatsPacket.GameSide.KIND);
        if(activity instanceof Notifiable) {
            ((Notifiable)activity).notifyWithObject(new GameStartedEvent());
        }
    }

    public void sendStartEventToEveryone() {
        sendObjectToEveryone(new StartTheGameRequest(gameStatsPacket));
    }

    private String getIpAddress() {
        // FIX: Get host address is broken (keeps getting 127.0.0.1). Using IPv4-only method
        WifiManager wm = (WifiManager) activity.getSystemService(WIFI_SERVICE);
        return Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
    }

    public int getKindPlayersAmount() {
        int count = 0;
        for (BattlePlayer player :
                players) {
            if (player.getPlayerName().isKind()) {
                count++;
            }
        }
        return count;
    }

    public String getPlayersAsString() {
        return players.toString();
    }
    public int getEvilPlayersAmount() {
        int count = 0;
        for (BattlePlayer player :
                players) {
            if (!player.getPlayerName().isKind()) {
                count++;
            }
        }
        return count;
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
                if(activity instanceof Notifiable) {
                    ((Notifiable) activity).notifyWithObject(new DisconnectedEvent(false));
                }
            }
        }

        @Override
        public void received(Connection connection, Object object) {
            if(object instanceof ServerConnectionRequest) { // Request of connection
                ServerConnectionRequest request = (ServerConnectionRequest) object;
                connectPlayer(request.player);
            } else if(object instanceof GetLobbyStatusRequest) {
                GameClientServer.sendTCPToConnectionAsync(connection,
                        new LobbyStatusUpdateResponse(players, getIpAddress()));
            } else if(object instanceof ChatMessage) {
                if(lobbyFragment.isVisible()) {
                    lobbyFragment.addChatMessage((ChatMessage) object);
                }
                if(activity instanceof Notifiable) {
                    ((Notifiable) activity).notifyWithObject(object);
                }
                sendObjectToEveryone(object);
            } /*else if(object instanceof BattlePlayer.BattlePlayerAction) {
                Log.d(TAG, "received: " + object.toString());
            } */else { // Send rest of objects to the callback to battlefieldSurfaceView
                if(callbackForBattleSurfaceView != null) {
                    callbackForBattleSurfaceView.evaluate(object);
                }
            }
            sendUpdatedGameStatsToEveryone();
        }


    }

    public String getPlayersStatsAsString() {
        String result = "";
        for (BattlePlayer player :
                players) {
            result += '\n' + player.getHealth() + "/" + player.getStamina();
        }
        return result;
    }
}
