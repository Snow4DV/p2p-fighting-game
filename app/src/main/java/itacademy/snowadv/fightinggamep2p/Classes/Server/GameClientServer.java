package itacademy.snowadv.fightinggamep2p.Classes.Server;

import android.graphics.Canvas;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;

import java.net.Inet4Address;

import itacademy.snowadv.fightinggamep2p.Classes.Drawables.Units.DrawableBattleUnit;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.Units.DrawableCriminal;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.Units.DrawableCriminalBoss;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.Units.DrawablePoliceman;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.Units.DrawableSchoolboy;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ChatMessage;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameActionRequest;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GameStatsPacket;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.GetLobbyStatusRequest;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.LobbyStatusUpdateResponse;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ServerConnectionRequest;
import itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.StartTheGameRequest;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.Sprite;
import itacademy.snowadv.fightinggamep2p.Classes.Drawables.SpritePainter;

public interface GameClientServer {
    /**
     * Registers classes that will be serialized
     * @param kryo Kryo instance for class registration
     */
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
        kryo.register(itacademy.snowadv.fightinggamep2p.Classes.Server.Packets.ErrorMessagePacket.class);
        kryo.register(GameStatsPacket.class);
        kryo.register(StartTheGameRequest.class);
        kryo.register(DrawableBattleUnit.class);
        kryo.register(GameActionRequest.class);
        kryo.register(BattlePlayer.BattlePlayerAction.class);
        kryo.register(GameStatsPacket.GameSide.class);
        kryo.register(DrawableCriminal.class);
        kryo.register(DrawableCriminalBoss.class);
        kryo.register(DrawablePoliceman.class);
        kryo.register(DrawableSchoolboy.class);
        kryo.register(android.graphics.Point.class);
        kryo.register(SpritePainter.class);
        kryo.register(Canvas.class);
        kryo.register(Sprite.class);
    }

    /**
     * Starts a new thread and sends TCP object to given connection
     * @param connection Object receiver
     * @param object Object that will be sent
     */
    static void sendTCPToConnectionAsync(Connection connection, Object object) {
        new Thread(() -> connection.sendTCP(object)).start();
    }
    void sendChatMessage(ChatMessage chatMessage);
}
