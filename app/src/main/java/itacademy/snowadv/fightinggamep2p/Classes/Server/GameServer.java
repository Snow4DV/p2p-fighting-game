package itacademy.snowadv.fightinggamep2p.Classes.Server;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GetLobbyStatusRequest;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.LobbyStatusUpdateResponse;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ServerConnectionRequest;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;

public class GameServer {
    private static final int FIXED_PORT_UDP = 54777;
    private static final int FIXED_PORT_TCP = 54555;
    private Context activity;

    private final Server server = new Server();
    private final GameServerListener listener = new GameServerListener();

    List<BattlePlayer> players = new ArrayList<>();
    BattlePlayer host; // TODO: init before starting server!

    private void startServer() {
        try {
            server.start();
            server.bind(FIXED_PORT_TCP, FIXED_PORT_UDP);
            initListeners();
        } catch(IOException ex) {
            Toast.makeText(activity, "Ошибка при запуске сервера." +
                            " Скорее всего, нужные порты заняты. Перезагрузите устройство."
                    , Toast.LENGTH_SHORT).show();
        }
    }

    private void initListeners() {
        server.addListener(listener);
    }

    private void connectPlayer(BattlePlayer player) {
        players.add(player);

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
            for(BattlePlayer player: players ) {
                if(player.connection.equals(connection)) {
                    players.remove(player);
                    break;
                }
            }
        }

        @Override
        public void received(Connection connection, Object object) {
            if(object instanceof ServerConnectionRequest) {
                ServerConnectionRequest request = (ServerConnectionRequest) object;
                connectPlayer(request.player);
            } else if(object instanceof GetLobbyStatusRequest) {
                GetLobbyStatusRequest request = (GetLobbyStatusRequest) object;
                connection.sendTCP(new LobbyStatusUpdateResponse(players, host));
            }
        }

    }
}
