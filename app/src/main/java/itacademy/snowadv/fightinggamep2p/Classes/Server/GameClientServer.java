package itacademy.snowadv.fightinggamep2p.Classes.Server;

import com.esotericsoftware.kryo.Kryo;

import java.net.Inet4Address;

import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ChatMessage;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GetLobbyStatusRequest;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.LobbyStatusUpdateResponse;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ServerConnectionRequest;
import itacademy.snowadv.fightinggamep2p.Fragments.Lobby.BattlePlayer;

public interface GameClientServer {
    static void registerClasses(Kryo kryo) {
        kryo.register(GetLobbyStatusRequest.class);
        kryo.register(LobbyStatusUpdateResponse.class);
        kryo.register(ServerConnectionRequest.class);
        kryo.register(BattlePlayer.class);
        kryo.register(BattlePlayer.BattlePlayerName.class);
        kryo.register(java.util.ArrayList.class);
        kryo.register(com.esotericsoftware.kryonet.Client.class);
        kryo.register(Inet4Address.class);
        kryo.register(ChatMessage.class);
    }
    void sendChatMessage(ChatMessage chatMessage);
}
